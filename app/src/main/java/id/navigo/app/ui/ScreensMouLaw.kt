package id.navigo.app.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.navigo.app.domain.MouEngine
import id.navigo.app.vm.LawViewModel
import id.navigo.app.vm.MouViewModel

@Composable
fun MouUploadScreen(vm: MouViewModel, onBack: () -> Unit, onResult: () -> Unit) {
    val st by vm.state.collectAsStateWithLifecycle()
    val gate = rememberConsentGate()
    var showSource by remember { mutableStateOf(false) }
    var showPaste by remember { mutableStateOf(false) }
    var paste by remember { mutableStateOf("") }
    val pdf = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { it?.let { u -> vm.loadUri(u, true) } }
    val gallery = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { it?.let { u -> vm.loadUri(u, false) } }
    val camera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { it?.let { b -> vm.loadBitmap(b) } }

    Screen("Analisis MOU", onBack) {
        NaviCard(Modifier.fillMaxWidth(), onClick = { pdf.launch(arrayOf("application/pdf")) }) {
            Column(Modifier.fillMaxWidth().padding(vertical = 18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                IconTile(NIcons.Add, 40)
                Spacer(Modifier.height(10.dp))
                Text("Ketuk untuk memilih dokumen", fontWeight = FontWeight.SemiBold)
                Hint("PDF, foto, atau tempel teks (maks 10 MB)")
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AssistChip(onClick = { pdf.launch(arrayOf("application/pdf")) }, label = { Text("PDF") })
            AssistChip(onClick = { showSource = true }, label = { Text("Kamera / Galeri") })
            AssistChip(onClick = { paste = st.text; showPaste = true }, label = { Text("Tempel Teks") })
        }
        if (st.title.isNotBlank() && st.text.isNotBlank()) Text("Dokumen: ${st.title} (${st.source})", fontWeight = FontWeight.Medium, fontSize = 14.sp)

        SectionTitle("Fokus analisis (opsional)")
        listOf("Hak cipta / HKI", "Pembayaran", "Pengakhiran", "Sengketa").chunked(2).forEach { row ->
            Row(Modifier.fillMaxWidth()) {
                row.forEach { f ->
                    Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = f in st.focus, onCheckedChange = { vm.toggleFocus(f) })
                        Text(f, fontSize = 14.sp)
                    }
                }
            }
        }
        NaviCard(Modifier.fillMaxWidth()) {
            Text("Teks dokumen diekstrak di perangkat. File asli tidak diunggah; data pribadi diganti token sebelum dikirim ke AI setelah Anda setuju.", fontSize = 12.sp, color = Primary)
        }
        Hint(st.status)
        st.error?.let { Text(it, color = RedFg, fontSize = 13.sp) }
        Spacer(Modifier.weight(1f, fill = false))
        PrimaryButton("Mulai Analisis", { gate { vm.analyze(onResult) } }, enabled = st.text.isNotBlank(), loading = st.loading)
    }

    if (showSource) AlertDialog(
        onDismissRequest = { showSource = false }, title = { Text("Ambil foto dokumen") },
        text = { Text("Pilih sumber gambar. Teks dibaca di perangkat dengan OCR.") },
        confirmButton = { TextButton(onClick = { showSource = false; camera.launch(null) }) { Text("Kamera") } },
        dismissButton = { TextButton(onClick = { showSource = false; gallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }) { Text("Galeri") } }
    )
    if (showPaste) AlertDialog(
        onDismissRequest = { showPaste = false }, title = { Text("Tempel teks kontrak") },
        text = { OutlinedTextField(paste, { paste = it }, Modifier.fillMaxWidth().heightIn(min = 160.dp), placeholder = { Text("Tempel isi perjanjian di sini…") }) },
        confirmButton = { TextButton(onClick = { vm.setText(paste); showPaste = false }) { Text("Gunakan") } },
        dismissButton = { TextButton(onClick = { showPaste = false }) { Text("Batal") } }
    )
}

@Composable
fun MouResultScreen(vm: MouViewModel, onBack: () -> Unit, onSaveDeadline: () -> Unit, onRevise: () -> Unit) {
    val st by vm.state.collectAsStateWithLifecycle()
    val r = st.result
    Screen("Hasil Analisis", onBack) {
        if (r == null) { Hint("Belum ada hasil analisis."); return@Screen }
        val (bg, fg) = levelColors(r.level)
        NaviCard(Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Hint("Skor Keamanan")
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("${r.score}", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = fg)
                        Text("/100", color = Muted, modifier = Modifier.padding(bottom = 10.dp, start = 4.dp))
                    }
                    Pill(r.level, bg, fg)
                }
                Column(Modifier.weight(1f)) {
                    LinearProgressIndicator(progress = { r.score / 100f }, Modifier.fillMaxWidth().height(10.dp), color = fg, trackColor = bg)
                    Spacer(Modifier.height(8.dp))
                    Hint("Ada ${r.risks.size} poin yang perlu ditinjau.")
                }
            }
        }
        if (r.limited) Hint("Mode terbatas: hanya checklist klausul lokal (AI tidak tersedia). Skor bisa terlalu optimistis.", AmberFg)
        NaviCard(Modifier.fillMaxWidth()) {
            Text("Ringkasan", fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Text(r.summary, fontSize = 13.sp)
        }
        SectionTitle("Klausul Berisiko (${r.risks.size})")
        r.risks.forEach {
            NaviCard(Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.Top) {
                    Text(it.title, Modifier.weight(1f), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    LevelPill(it.level)
                }
                if (it.suggestion.isNotBlank()) Hint("Saran: ${it.suggestion}")
                if (it.basis.isNotBlank()) Hint("Dasar: ${it.basis}", Primary)
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlineBtn("Simpan ke Tenggat", onSaveDeadline, Modifier.weight(1f))
            PrimaryButton("Buat Dokumen Revisi", onRevise, Modifier.weight(1f))
        }
        Hint("Hasil AI bukan nasihat hukum. Konsultasikan dengan ahli hukum untuk keputusan penting.")
    }
}

@Composable
fun LawSearchScreen(vm: LawViewModel, onBack: () -> Unit, onOpen: () -> Unit) {
    val st by vm.state.collectAsStateWithLifecycle()
    Screen("Cari Undang-Undang", onBack) {
        OutlinedTextField(st.query, vm::setQuery, Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp),
            placeholder = { Text("mis. hak cipta lagu") }, leadingIcon = { Icon(NIcons.Search, null) })
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Semua", "UU", "PP", "Permen").forEach {
                FilterChip(selected = st.category == it, onClick = { vm.setCategory(it) }, label = { Text(it) })
            }
        }
        when {
            st.query.trim().length < 3 -> {
                Hint("Ketik minimal 3 karakter. Topik populer:")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("hak cipta", "merek", "perjanjian", "data pribadi").forEach {
                        AssistChip(onClick = { vm.setQuery(it) }, label = { Text(it) })
                    }
                }
            }
            !st.searched || st.hits.isEmpty() -> NaviCard(Modifier.fillMaxWidth()) {
                Text("Tidak ketemu? Coba kata kunci lain, misalnya \"royalti\", \"wanprestasi\", atau \"lisensi\".", fontSize = 13.sp)
            }
            else -> {
                Hint("${st.hits.size} hasil · diurutkan berdasarkan relevansi")
                st.hits.forEach { h ->
                    NaviCard(Modifier.fillMaxWidth(), onClick = { vm.select(h.law); onOpen() }) {
                        Text(h.law.title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        Hint(h.law.topic)
                        Text(h.law.description, fontSize = 12.sp, maxLines = 2)
                        if (h.pasal.isNotEmpty()) {
                            Spacer(Modifier.height(6.dp))
                            Pill("Pasal " + h.pasal.take(4).joinToString(", "), PrimaryLight, Primary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LawDetailScreen(vm: LawViewModel, onBack: () -> Unit, onAskChat: (String) -> Unit) {
    val st by vm.state.collectAsStateWithLifecycle()
    val gate = rememberConsentGate()
    var tab by remember { mutableIntStateOf(0) }
    val law = st.selected
    Screen(law?.number ?: "Detail", onBack) {
        if (law == null) { Hint("Peraturan belum dipilih."); return@Screen }
        Text(law.title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Hint(law.topic); LevelPill(if (law.active) "Berlaku" else "Nonaktif")
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(tab == 0, { tab = 0 }, { Text("Rangkuman AI") }, Modifier.weight(1f))
            FilterChip(tab == 1, { tab = 1 }, { Text("Teks Pasal") }, Modifier.weight(1f))
        }
        if (tab == 0) {
            NaviCard(Modifier.fillMaxWidth()) {
                Text("Rangkuman bahasa awam", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(6.dp))
                if (law.aiSummary != null) law.aiSummary.lines().forEach { Text("• $it", fontSize = 13.sp, modifier = Modifier.padding(bottom = 6.dp)) }
                else Hint("Belum ada rangkuman. Buat rangkuman AI (butuh internet).")
            }
            st.message?.let { Text(it, color = RedFg, fontSize = 13.sp) }
            PrimaryButton(if (law.aiSummary == null) "Buat rangkuman AI" else "Buat ulang rangkuman", { gate { vm.summarize() } }, loading = st.summaryLoading)
        } else {
            st.articles.forEach {
                NaviCard(Modifier.fillMaxWidth()) {
                    Text("Pasal ${it.number}", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text(it.text, fontSize = 13.sp)
                }
            }
        }
        Hint("Sumber: JDIH / peraturan.go.id. Teks pasal di aplikasi berupa ringkasan; rujuk naskah resmi untuk kepastian hukum. Hasil AI bukan nasihat hukum.")
        OutlineBtn("Tanya Chatbot", { onAskChat("Jelaskan ${law.title} untuk usaha saya") })
    }
}
