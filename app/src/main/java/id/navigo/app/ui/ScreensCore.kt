package id.navigo.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.navigo.app.data.Contract
import id.navigo.app.util.Dates
import id.navigo.app.vm.AuthViewModel
import id.navigo.app.vm.ContractViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(auth: AuthViewModel, onNext: (loggedIn: Boolean, admin: Boolean) -> Unit) {
    val st by auth.state.collectAsStateWithLifecycle()
    LaunchedEffect(st.checked) {
        if (st.checked) { delay(1000); onNext(st.loggedIn, st.isAdmin) }
    }
    Column(Modifier.fillMaxSize().background(Color.White).systemBarsPadding(), Arrangement.Center, Alignment.CenterHorizontally) {
        Box(Modifier.size(96.dp).background(Primary, RoundedCornerShape(24.dp)), contentAlignment = Alignment.Center) {
            Text("N", color = Color.White, fontSize = 52.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(20.dp))
        Text("NaviGo", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Primary)
        Text("Navigasi hukum untuk UMKM & kreator", color = Muted, fontSize = 13.sp)
        Spacer(Modifier.height(48.dp))
        LinearProgressIndicator(Modifier.width(120.dp))
    }
}

@Composable
fun AuthScreen(vm: AuthViewModel, onLoggedIn: (admin: Boolean) -> Unit) {
    val st by vm.state.collectAsStateWithLifecycle()
    var register by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    LaunchedEffect(st.loggedIn) { if (st.loggedIn) onLoggedIn(st.isAdmin) }

    Column(
        Modifier.fillMaxSize().background(Bg).systemBarsPadding().imePadding()
            .verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(Modifier.height(32.dp))
        Text("Selamat datang", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Masuk untuk mengakses fitur asisten hukum", color = Muted, fontSize = 14.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = !register, onClick = { register = false }, label = { Text("Masuk") })
            FilterChip(selected = register, onClick = { register = true }, label = { Text("Daftar") })
        }
        if (vm.demoMode) Hint("Mode demo: Firebase belum dikonfigurasi (tidak ada google-services.json). Login diterima secara lokal; gunakan admin@navigo.id untuk masuk sebagai admin.", AmberFg)
        Field("Email", email, { email = it }, "nama@gmail.com", androidx.compose.ui.text.input.KeyboardType.Email)
        Field("Kata sandi", pass, { pass = it }, "••••••••", password = true)
        if (register) Field("Konfirmasi kata sandi", confirm, { confirm = it }, "••••••••", password = true)
        if (!register) TextButton(onClick = { vm.resetPassword(email) }, modifier = Modifier.align(Alignment.End)) { Text("Lupa kata sandi?", fontSize = 13.sp) }
        st.error?.let { Text(it, color = RedFg, fontSize = 13.sp) }
        st.info?.let { Text(it, color = GreenFg, fontSize = 13.sp) }
        PrimaryButton(if (register) "Daftar" else "Masuk", { vm.submit(register, email, pass, confirm) }, loading = st.loading)
        Text("atau", Modifier.fillMaxWidth(), textAlign = TextAlign.Center, color = Muted, fontSize = 13.sp)
        OutlinedButton(onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth().height(46.dp), shape = RoundedCornerShape(10.dp)) {
            Text("Masuk dengan Google (belum tersedia)")
        }
        Spacer(Modifier.height(8.dp))
        Hint("Dengan masuk, Anda menyetujui syarat penggunaan dan kebijakan privasi NaviGo.")
    }
}

@Composable
fun daysColors(c: Contract): Pair<Color, Color> {
    val d = c.daysLeft()
    return when { d < 0 -> Muted to Border; d <= 7 -> RedFg to RedBg; d <= 30 -> AmberFg to AmberBg; else -> GreenFg to GreenBg }
}

@Composable
fun ContractCard(c: Contract, onExtend: (() -> Unit)? = null, onDelete: (() -> Unit)? = null) {
    val (fg, bg) = daysColors(c)
    val d = c.daysLeft()
    NaviCard(Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.width(5.dp).height(52.dp).background(fg, RoundedCornerShape(3.dp)))
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(c.title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                if (c.party.isNotBlank()) Hint("Pihak: ${c.party}")
                Hint("Berakhir ${Dates.show(c.endDay)}")
            }
            Pill(if (d < 0) "Lewat" else "H-$d", bg, fg)
        }
        if (onExtend != null || onDelete != null) Row(Modifier.padding(top = 6.dp)) {
            onExtend?.let { TextButton(onClick = it) { Text("Perpanjang +12 bln", fontSize = 12.sp) } }
            Spacer(Modifier.weight(1f))
            onDelete?.let { TextButton(onClick = it) { Text("Hapus", fontSize = 12.sp, color = RedFg) } }
        }
    }
}

@Composable
private fun FeatureTile(title: String, sub: String, icon: ImageVector, modifier: Modifier, onClick: () -> Unit) {
    NaviCard(modifier, onClick) {
        IconTile(icon)
        Spacer(Modifier.height(10.dp))
        Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Text(sub, fontSize = 11.sp, color = Muted, minLines = 2)
    }
}

@Composable
fun HomeScreen(auth: AuthViewModel, contracts: ContractViewModel, go: (String) -> Unit, onLogout: () -> Unit) {
    val st by auth.state.collectAsStateWithLifecycle()
    val list by contracts.contracts.collectAsStateWithLifecycle()
    val upcoming = list.filter { !it.expired() }.take(2)
    Column(
        Modifier.fillMaxSize().background(Bg).statusBarsPadding().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Halo, ${st.name}", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Hint("Akun: ${st.email}")
            }
            IconButton(onClick = { go("contracts") }) { Icon(NIcons.Bell, "Tenggat", tint = Primary) }
            TextButton(onClick = onLogout) { Text("Keluar") }
        }
        SectionTitle("Fitur Utama")
        data class Item(val t: String, val s: String, val i: ImageVector, val r: String)
        val items = listOf(
            Item("Analisis MOU", "Ringkas & cek risiko kontrak", NIcons.Mou, "mou"),
            Item("Cari UU", "Temukan & pahami pasal", NIcons.Search, "uu"),
            Item("Chatbot Hukum", "Tanya hukum apa saja", NIcons.Chat, "chat"),
            Item("Buat Dokumen", "Draf PDF otomatis", NIcons.Docs, "docs"),
            Item("Buat Kontrak", "Pengingat otomatis", NIcons.Calendar, "contracts"),
            Item("Riwayat", "Analisis & dokumen", NIcons.History, "history")
        )
        items.chunked(2).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { FeatureTile(it.t, it.s, it.i, Modifier.weight(1f)) { go(it.r) } }
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.weight(1f)) { SectionTitle("Tenggat Terdekat") }
            TextButton(onClick = { go("contracts") }) { Text("Lihat semua", fontSize = 13.sp) }
        }
        if (upcoming.isEmpty()) Hint("Belum ada kontrak aktif. Tambahkan lewat menu Buat Kontrak atau simpan hasil Analisis MOU.")
        upcoming.forEach { ContractCard(it) }
    }
}
