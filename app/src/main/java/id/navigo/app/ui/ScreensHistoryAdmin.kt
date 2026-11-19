package id.navigo.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.navigo.app.util.Dates
import id.navigo.app.vm.*

@Composable
fun HistoryScreen(
    hist: HistoryViewModel, mou: MouViewModel, chat: ChatViewModel, docs: DocViewModel, contracts: ContractViewModel,
    onBack: () -> Unit, go: (String) -> Unit
) {
    val analyses by hist.analyses.collectAsStateWithLifecycle()
    val files by hist.docs.collectAsStateWithLifecycle()
    val sessions by chat.sessions.collectAsStateWithLifecycle()
    val ctx = LocalContext.current
    var tab by remember { mutableIntStateOf(0) }
    var q by remember { mutableStateOf("") }

    Screen("Riwayat", onBack) {
        TabRow(selectedTabIndex = tab, containerColor = androidx.compose.ui.graphics.Color.Transparent) {
            listOf("Analisis", "Dokumen", "Chat").forEachIndexed { i, s -> Tab(selected = tab == i, onClick = { tab = i }, text = { Text(s) }) }
        }
        OutlinedTextField(q, { q = it }, Modifier.fillMaxWidth(), singleLine = true, placeholder = { Text("Cari…") }, leadingIcon = { Icon(NIcons.Search, null) })
        when (tab) {
            0 -> {
                analyses.filter { it.title.contains(q, true) }.forEach { a ->
                    NaviCard(Modifier.fillMaxWidth()) {
                        Row { Text(a.title, Modifier.weight(1f), fontWeight = FontWeight.SemiBold, fontSize = 15.sp); LevelPill(a.level) }
                        Hint("${Dates.dateTime(a.createdAt)} · ${a.source}")
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Hint("Skor ${a.score}/100"); Spacer(Modifier.weight(1f))
                            TextButton(onClick = { mou.openSaved(a); go("mou_result") }) { Text("Buka", fontSize = 12.sp) }
                            TextButton(onClick = {
                                contracts.prefill = Prefill(title = a.title, endDay = a.endDay); go("contract_add")
                            }) { Text("Tenggat", fontSize = 12.sp) }
                            TextButton(onClick = { hist.deleteAnalysis(a) }) { Text("Hapus", fontSize = 12.sp, color = RedFg) }
                        }
                    }
                }
                if (analyses.isEmpty()) Hint("Belum ada analisis MOU.")
            }
            1 -> {
                files.filter { it.fileName.contains(q, true) }.forEach { d ->
                    NaviCard(Modifier.fillMaxWidth()) {
                        Text(d.fileName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Hint("${d.templateName} · ${Dates.dateTime(d.createdAt)} · ${d.sizeKb} KB")
                        Row {
                            TextButton(onClick = { openFile(ctx, d.path) }) { Text("Buka", fontSize = 12.sp) }
                            TextButton(onClick = { shareFile(ctx, d.path) }) { Text("Bagikan", fontSize = 12.sp) }
                            TextButton(onClick = {
                                contracts.prefill = Prefill(title = d.templateName, party = d.partyName); go("contract_add")
                            }) { Text("Tenggat", fontSize = 12.sp) }
                            Spacer(Modifier.weight(1f))
                            TextButton(onClick = { hist.deleteDoc(d) }) { Text("Hapus", fontSize = 12.sp, color = RedFg) }
                        }
                    }
                }
                if (files.isEmpty()) Hint("Belum ada dokumen PDF.")
            }
            else -> {
                sessions.filter { it.title.contains(q, true) }.forEach { s ->
                    NaviCard(Modifier.fillMaxWidth(), onClick = { chat.open(s.id); go("chat") }) {
                        Text(s.title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        Hint("Terakhir: \"${s.lastText}\"")
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Hint("${Dates.dateTime(s.updatedAt)} · ${s.msgCount} pesan"); Spacer(Modifier.weight(1f))
                            TextButton(onClick = { chat.deleteSession(s) }) { Text("Hapus", fontSize = 12.sp, color = RedFg) }
                        }
                    }
                }
                PrimaryButton("+ Percakapan Baru", { chat.newSession(); go("chat") })
            }
        }
        Hint("Riwayat tersimpan di perangkat dan dapat dihapus kapan saja.")
    }
}

// ---------------- Admin ----------------

@Composable
fun AdminDashboardScreen(vm: AdminViewModel, go: (String) -> Unit, onLogout: () -> Unit) {
    val stats by vm.stats.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { vm.loadUsers() }
    Screen("Dashboard Admin", null) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(stats.first to "Pengguna", stats.second to "Analisis hari ini", stats.third to "Kuota AI (%)").forEach { (n, l) ->
                NaviCard(Modifier.weight(1f)) { Text("$n", fontSize = 22.sp, fontWeight = FontWeight.Bold); Hint(l) }
            }
        }
        listOf("Kelola Data UU & Pasal" to "admin_laws", "Kelola Template Dokumen" to "admin_templates", "Kelola Akun Pengguna" to "admin_users").forEach { (t, r) ->
            NaviCard(Modifier.fillMaxWidth(), onClick = { go(r) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconTile(NIcons.Settings); Spacer(Modifier.width(12.dp))
                    Text(t, Modifier.weight(1f), fontWeight = FontWeight.Medium); Text("›", fontSize = 22.sp, color = Muted)
                }
            }
        }
        Hint("Admin juga dapat memakai fitur pengguna: keluar lalu masuk dengan akun pengguna biasa.")
        OutlineBtn("Keluar", onLogout)
    }
}

@Composable
fun AdminLawsScreen(vm: AdminViewModel, onBack: () -> Unit) {
    val laws by vm.laws.collectAsStateWithLifecycle()
    var q by remember { mutableStateOf("") }
    var add by remember { mutableStateOf(false) }
    var edit by remember { mutableStateOf<id.navigo.app.data.Law?>(null) }
    Screen("Kelola Data UU", onBack, fab = {
        ExtendedFloatingActionButton(onClick = { add = true }, containerColor = Primary, contentColor = androidx.compose.ui.graphics.Color.White) { Text("+ Tambah UU") }
    }) {
        OutlinedTextField(q, { q = it }, Modifier.fillMaxWidth(), singleLine = true, placeholder = { Text("Cari judul / nomor UU") })
        laws.filter { it.title.contains(q, true) || it.number.contains(q, true) }.forEach { l ->
            NaviCard(Modifier.fillMaxWidth()) {
                Text(l.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Hint("${l.number} · ${l.topic}")
                Row { TextButton(onClick = { edit = l }) { Text("Ubah") }; TextButton(onClick = { vm.deleteLaw(l) }) { Text("Hapus", color = RedFg) } }
            }
        }
        Hint("Perubahan tersimpan di database lokal perangkat (sinkron Firestore belum diimplementasikan).")
        Spacer(Modifier.height(72.dp))
    }
    if (add) {
        var t by remember { mutableStateOf("") }; var n by remember { mutableStateOf("") }; var tp by remember { mutableStateOf("") }
        var d by remember { mutableStateOf("") }; var an by remember { mutableStateOf("") }; var at by remember { mutableStateOf("") }
        AlertDialog(onDismissRequest = { add = false }, title = { Text("Tambah UU") }, text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedTextField(t, { t = it }, label = { Text("Judul") }); OutlinedTextField(n, { n = it }, label = { Text("Nomor (mis. UU 5/2020)") })
                OutlinedTextField(tp, { tp = it }, label = { Text("Topik") }); OutlinedTextField(d, { d = it }, label = { Text("Deskripsi") })
                OutlinedTextField(an, { an = it }, label = { Text("No. pasal pertama") }); OutlinedTextField(at, { at = it }, label = { Text("Ringkasan pasal") })
            }
        }, confirmButton = { TextButton(onClick = { if (t.isNotBlank()) { vm.addLaw(t, n, tp, d, an, at); add = false } }) { Text("Simpan") } },
            dismissButton = { TextButton(onClick = { add = false }) { Text("Batal") } })
    }
    edit?.let { l ->
        var t by remember { mutableStateOf(l.title) }
        AlertDialog(onDismissRequest = { edit = null }, title = { Text("Ubah judul") }, text = { OutlinedTextField(t, { t = it }) },
            confirmButton = { TextButton(onClick = { vm.renameLaw(l, t); edit = null }) { Text("Simpan") } },
            dismissButton = { TextButton(onClick = { edit = null }) { Text("Batal") } })
    }
}

@Composable
fun AdminTemplatesScreen(vm: AdminViewModel, onBack: () -> Unit, onEdit: (Long) -> Unit) {
    val list by vm.templates.collectAsStateWithLifecycle()
    Screen("Kelola Template Dokumen", onBack) {
        list.forEach { t ->
            NaviCard(Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(t.name, Modifier.weight(1f), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    LevelPill(if (t.active) "Aktif" else "Draf")
                }
                Hint("Versi ${t.version} · dipakai ${t.usedCount}x")
                Row {
                    TextButton(onClick = { onEdit(t.id) }) { Text("Ubah") }
                    TextButton(onClick = { vm.toggleTemplate(t) }) { Text(if (t.active) "Nonaktifkan" else "Publikasikan") }
                }
            }
        }
    }
}

@Composable
fun AdminTemplateEditScreen(vm: AdminViewModel, id: Long, onBack: () -> Unit) {
    val list by vm.templates.collectAsStateWithLifecycle()
    val t = list.firstOrNull { it.id == id }
    Screen("Ubah Template", onBack) {
        if (t == null) { Hint("Template tidak ditemukan."); return@Screen }
        var name by remember(t.id) { mutableStateOf(t.name) }
        var body by remember(t.id) { mutableStateOf(t.body) }
        var active by remember(t.id) { mutableStateOf(t.active) }
        Field("Nama Template", name, { name = it })
        Field("Isi Template (gunakan {{placeholder}})", body, { body = it }, singleLine = false, minLines = 10)
        Hint("Placeholder tersedia: {{nomor}}, {{tanggal}}, {{klausul_tambahan}}, dan field formulir template ini.")
        Row(verticalAlignment = Alignment.CenterVertically) { Text("Template aktif", Modifier.weight(1f)); Switch(active, { active = it }) }
        Hint("Versi naik otomatis saat disimpan.")
        PrimaryButton("Simpan Template", { vm.saveTemplate(t, name, body, active); onBack() })
    }
}

@Composable
fun AdminUsersScreen(vm: AdminViewModel, onBack: () -> Unit, onOpen: (String) -> Unit) {
    val users by vm.users.collectAsStateWithLifecycle()
    val sample by vm.usersSample.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { vm.loadUsers() }
    var q by remember { mutableStateOf("") }
    Screen("Kelola Akun Pengguna", onBack) {
        OutlinedTextField(q, { q = it }, Modifier.fillMaxWidth(), singleLine = true, placeholder = { Text("Cari nama atau email") })
        if (sample) Hint("Data contoh: Firebase/Firestore belum aktif.", AmberFg)
        Hint("${users.size} akun · ${users.count { it.status == "diblokir" }} diblokir")
        users.filter { it.name.contains(q, true) || it.email.contains(q, true) }.forEach { u ->
            NaviCard(Modifier.fillMaxWidth(), onClick = { onOpen(u.id) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) { Text(u.name, fontWeight = FontWeight.SemiBold); Hint(u.email) }
                    LevelPill(u.status)
                }
                Spacer(Modifier.height(4.dp)); Pill(u.role.replaceFirstChar { it.uppercase() }, PrimaryLight, Primary)
            }
        }
    }
}

@Composable
fun AdminUserDetailScreen(vm: AdminViewModel, id: String, onBack: () -> Unit) {
    val users by vm.users.collectAsStateWithLifecycle()
    val u = users.firstOrNull { it.id == id }
    Screen("Detail Akun", onBack) {
        if (u == null) { Hint("Akun tidak ditemukan."); return@Screen }
        Text(u.name, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Hint(u.email)
        LevelPill(u.status)
        NaviCard(Modifier.fillMaxWidth()) {
            Hint("Bergabung: ${u.joined}"); Hint("Nama usaha: ${u.business}"); Hint("Peran: ${u.role}")
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlineBtn(if (u.role == "admin") "Jadikan Pengguna" else "Jadikan Admin", { vm.updateUser(u, role = if (u.role == "admin") "user" else "admin") }, Modifier.weight(1f))
            if (u.status == "diblokir") PrimaryButton("Buka Blokir", { vm.updateUser(u, status = "aktif") }, Modifier.weight(1f))
            else PrimaryButton("Blokir", { vm.updateUser(u, status = "diblokir") }, Modifier.weight(1f))
        }
        Hint("Penghapusan akun harus dilakukan lewat Firebase Console / Admin SDK (tidak aman dari aplikasi klien).")
    }
}
