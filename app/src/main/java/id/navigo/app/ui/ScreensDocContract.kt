package id.navigo.app.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.navigo.app.data.formFields
import id.navigo.app.domain.DocEngine
import id.navigo.app.domain.PdfWriter
import id.navigo.app.util.Dates
import id.navigo.app.vm.ContractViewModel
import id.navigo.app.vm.DocViewModel
import id.navigo.app.vm.HistoryViewModel
import id.navigo.app.vm.Prefill
import java.io.File
import java.time.LocalDate

fun shareFile(ctx: Context, path: String) {
    val uri = FileProvider.getUriForFile(ctx, "${ctx.packageName}.fileprovider", File(path))
    val i = Intent(Intent.ACTION_SEND).apply { type = "application/pdf"; putExtra(Intent.EXTRA_STREAM, uri); addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) }
    ctx.startActivity(Intent.createChooser(i, "Bagikan dokumen"))
}

fun openFile(ctx: Context, path: String) {
    val uri = FileProvider.getUriForFile(ctx, "${ctx.packageName}.fileprovider", File(path))
    val i = Intent(Intent.ACTION_VIEW).apply { setDataAndType(uri, "application/pdf"); addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) }
    runCatching { ctx.startActivity(i) }.onFailure { Toast.makeText(ctx, "Tidak ada aplikasi pembaca PDF.", Toast.LENGTH_SHORT).show() }
}

@Composable
fun TemplatePickScreen(vm: DocViewModel, onBack: () -> Unit, onPick: () -> Unit) {
    val list by vm.templates.collectAsStateWithLifecycle()
    Screen("Buat Dokumen", onBack) {
        Hint("Pilih jenis dokumen. Draf disusun dari template risiko rendah–menengah dan perlu ditinjau sebelum ditandatangani.")
        list.forEach { t ->
            NaviCard(Modifier.fillMaxWidth(), onClick = { vm.select(t); onPick() }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconTile(NIcons.Docs, 40)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(t.name, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        Hint(t.description)
                    }
                    Text("›", fontSize = 22.sp, color = Muted)
                }
            }
        }
        if (list.isEmpty()) Hint("Belum ada template aktif.")
    }
}

@Composable
fun DocFormScreen(vm: DocViewModel, onBack: () -> Unit, onNext: () -> Unit) {
    val st by vm.state.collectAsStateWithLifecycle()
    val gate = rememberConsentGate()
    val t = st.template
    Screen(t?.name ?: "Formulir", onBack) {
        if (t == null) { Hint("Pilih template dulu."); return@Screen }
        SectionTitle("Isi data dokumen")
        t.formFields().forEach { f ->
            Field(f.label + if (f.required) "" else " (opsional)", st.values[f.key].orEmpty(), { vm.setValue(f.key, it) },
                keyboard = if (f.type == "NUMBER") KeyboardType.Number else KeyboardType.Text, error = st.errors[f.key])
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Saran klausul tambahan dengan AI", Modifier.weight(1f), fontSize = 14.sp)
            Switch(checked = st.useAi, onCheckedChange = { vm.setAi(it) })
        }
        Hint("Butuh internet. Jika gagal, dipakai klausul standar template.")
        st.message?.let { Hint(it, AmberFg) }
        PrimaryButton("Lanjut ke Pratinjau", { if (st.useAi) gate { vm.prepare(onNext) } else vm.prepare(onNext) }, loading = st.busy)
    }
}

@Composable
fun DocPreviewScreen(vm: DocViewModel, onBack: () -> Unit, onDone: () -> Unit) {
    val st by vm.state.collectAsStateWithLifecycle()
    Screen("Pratinjau Dokumen", onBack) {
        val body = st.template?.body.orEmpty()
        NaviCard(Modifier.fillMaxWidth()) {
            Text(DocEngine.annotated(body, st.values, Primary), fontSize = 12.sp, lineHeight = 17.sp)
        }
        Hint("Biru = diisi dari input Anda; abu-abu = belum diisi.")
        st.message?.let { Text(it, color = RedFg, fontSize = 13.sp) }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlineBtn("Ubah Data", onBack, Modifier.weight(1f))
            PrimaryButton("Buat PDF", { vm.generate(onDone) }, Modifier.weight(1f), loading = st.busy)
        }
    }
}

@Composable
fun DocReadyScreen(vm: DocViewModel, contracts: ContractViewModel, onBack: () -> Unit, onDeadline: () -> Unit, onHome: () -> Unit) {
    val st by vm.state.collectAsStateWithLifecycle()
    val ctx = LocalContext.current
    val d = st.ready
    Screen("Dokumen Siap", onBack) {
        if (d == null) { Hint("Belum ada dokumen."); return@Screen }
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(80.dp).background(GreenBg, RoundedCornerShape(40.dp)), contentAlignment = Alignment.Center) {
                Icon(NIcons.Check, null, tint = GreenFg, modifier = Modifier.size(44.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text("PDF berhasil dibuat", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        }
        NaviCard(Modifier.fillMaxWidth()) {
            Text(d.fileName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Hint("${d.sizeKb} KB · ${d.pages} halaman")
        }
        PrimaryButton("Bagikan ke Pihak Kedua", { shareFile(ctx, d.path) })
        OutlineBtn("Buka PDF", { openFile(ctx, d.path) })
        OutlineBtn("Unduh / Simpan", {
            val ok = runCatching { PdfWriter.saveToDownloads(ctx, File(d.path)) }.getOrDefault(false)
            Toast.makeText(ctx, if (ok) "Tersimpan di Download/NaviGo" else "Gagal menyimpan. Periksa ruang penyimpanan.", Toast.LENGTH_LONG).show()
        })
        OutlineBtn("Daftarkan Tenggat Kontrak", {
            contracts.prefill = Prefill(title = d.templateName + if (d.partyName.isNotBlank()) " - ${d.partyName}" else "", party = d.partyName)
            onDeadline()
        })
        TextButton(onClick = onHome, modifier = Modifier.align(Alignment.CenterHorizontally)) { Text("Kembali ke Beranda") }
        Hint("Cetak dan tanda tangani di atas meterai cukup agar dokumen kuat sebagai alat bukti.")
    }
}

// ---------------- Contract ----------------

@Composable
fun ContractDashboardScreen(vm: ContractViewModel, onBack: () -> Unit, onAdd: () -> Unit) {
    val list by vm.contracts.collectAsStateWithLifecycle()
    val ctx = LocalContext.current
    var filter by remember { mutableIntStateOf(0) }
    var granted by remember {
        mutableStateOf(Build.VERSION.SDK_INT < 33 || ContextCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED)
    }
    val perm = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted = it }
    val active = list.filter { !it.expired() }
    val red = active.count { it.daysLeft() <= 7 }
    val yellow = active.count { it.daysLeft() in 8..30 }
    val green = active.count { it.daysLeft() > 30 }
    val shown = when (filter) { 1 -> active; 2 -> list.filter { it.expired() }; else -> list }

    Screen("Tenggat Kontrak", onBack, fab = {
        FloatingActionButton(onClick = onAdd, containerColor = Primary, contentColor = Color.White) { Icon(NIcons.Add, "Tambah") }
    }) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(Triple(red, "Merah", RedBg to RedFg), Triple(yellow, "Kuning", AmberBg to AmberFg), Triple(green, "Hijau", GreenBg to GreenFg)).forEach { (n, l, c) ->
                Column(Modifier.weight(1f).background(c.first, RoundedCornerShape(12.dp)).padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$n", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = c.second)
                    Text(l, fontSize = 12.sp, color = c.second)
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Semua", "Aktif", "Kedaluwarsa").forEachIndexed { i, s -> FilterChip(filter == i, { filter = i }, { Text(s) }) }
        }
        shown.forEach { c -> ContractCard(c, onExtend = { vm.extend(c) }, onDelete = { vm.delete(c) }) }
        if (shown.isEmpty()) Hint("Belum ada kontrak. Ketuk + untuk menambah.")
        if (!granted) NaviCard(Modifier.fillMaxWidth(), onClick = { perm.launch(Manifest.permission.POST_NOTIFICATIONS) }) {
            Text("Izin notifikasi belum aktif. Ketuk untuk mengaktifkan pengingat H-30, H-14, H-7, dan H-1.", fontSize = 13.sp, color = AmberFg)
        }
        Spacer(Modifier.height(72.dp))
    }
}

@Composable
fun AddContractScreen(vm: ContractViewModel, history: HistoryViewModel, onBack: () -> Unit, onSaved: () -> Unit) {
    val ctx = LocalContext.current
    val analyses by history.analyses.collectAsStateWithLifecycle()
    val docs by history.docs.collectAsStateWithLifecycle()
    val pre = vm.prefill
    var title by remember { mutableStateOf(pre.title) }
    var party by remember { mutableStateOf(pre.party) }
    var start by remember { mutableStateOf(Dates.inputFmt(LocalDate.now().toEpochDay())) }
    var end by remember { mutableStateOf(pre.endDay?.let { Dates.inputFmt(it) } ?: "") }
    var offsets by remember { mutableStateOf(setOf(30, 14, 7, 1)) }
    var error by remember { mutableStateOf<String?>(null) }

    Screen("Tambah Kontrak", onBack) {
        SectionTitle("Impor dari")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AssistChip(onClick = { analyses.firstOrNull()?.let { title = it.title; it.endDay?.let { e -> end = Dates.inputFmt(e) } } }, label = { Text("Hasil Analisis") })
            AssistChip(onClick = { docs.firstOrNull()?.let { title = it.templateName; party = it.partyName } }, label = { Text("Dokumen PDF") })
            AssistChip(onClick = { title = ""; party = ""; end = "" }, label = { Text("Manual") })
        }
        Field("Judul Kontrak", title, { title = it }, "mis. PKS Kreator - PT Maju")
        Field("Pihak Kedua", party, { party = it }, "mis. PT Maju Jaya")
        Field("Tanggal Mulai (dd/MM/yyyy)", start, { start = it }, "01/01/2026")
        Field("Tanggal Berakhir (dd/MM/yyyy)", end, { end = it }, "31/12/2026")
        SectionTitle("Pengingat")
        Row(Modifier.fillMaxWidth()) {
            listOf(30, 14, 7, 1).forEach { d ->
                Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(d in offsets, { offsets = if (d in offsets) offsets - d else offsets + d })
                    Text("H-$d", fontSize = 13.sp)
                }
            }
        }
        Hint("Pengingat yang sudah lewat otomatis dilewati.")
        error?.let { Text(it, color = RedFg, fontSize = 13.sp) }
        PrimaryButton("Simpan & Jadwalkan", {
            error = vm.save(title, party, start, end, offsets) { expired ->
                if (expired) Toast.makeText(ctx, "Kontrak sudah kedaluwarsa, disimpan dengan status Kedaluwarsa.", Toast.LENGTH_LONG).show()
                onSaved()
            }
        })
    }
}
