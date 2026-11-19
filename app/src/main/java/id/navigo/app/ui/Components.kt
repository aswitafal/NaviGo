package id.navigo.app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.navigo.app.util.Prefs

object NIcons {
    val Back = Icons.Default.ArrowBack
    val Home = Icons.Default.Home
    val Mou = Icons.Default.CheckCircle
    val Search = Icons.Default.Search
    val Chat = Icons.Default.Email
    val Docs = Icons.Default.List
    val Send = Icons.Default.Send
    val Add = Icons.Default.Add
    val Bell = Icons.Default.Notifications
    val Calendar = Icons.Default.DateRange
    val History = Icons.Default.Refresh
    val Share = Icons.Default.Share
    val Check = Icons.Default.Check
    val Settings = Icons.Default.Settings
    val Person = Icons.Default.Person
    val Delete = Icons.Default.Delete
    val Edit = Icons.Default.Edit
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Screen(
    title: String, onBack: (() -> Unit)?, fab: (@Composable () -> Unit)? = null,
    scroll: Boolean = true, content: @Composable ColumnScope.() -> Unit
) {
    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.SemiBold, fontSize = 17.sp) },
                navigationIcon = { if (onBack != null) IconButton(onClick = onBack) { Icon(NIcons.Back, "Kembali") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
            val m = Modifier.fillMaxWidth().weight(1f)
            Column(
                (if (scroll) m.verticalScroll(rememberScrollState()) else m).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp), content = content
            )
        }
        if (fab != null) Box(Modifier.align(Alignment.BottomEnd).padding(16.dp)) { fab() }
    }
}

@Composable
fun NaviCard(modifier: Modifier = Modifier, onClick: (() -> Unit)? = null, content: @Composable ColumnScope.() -> Unit) {
    val shape = RoundedCornerShape(12.dp)
    val colors = CardDefaults.cardColors(containerColor = Color.White)
    val border = BorderStroke(1.dp, Border)
    if (onClick != null) Card(onClick = onClick, modifier = modifier, shape = shape, colors = colors, border = border) {
        Column(Modifier.padding(14.dp), content = content)
    } else Card(modifier = modifier, shape = shape, colors = colors, border = border) {
        Column(Modifier.padding(14.dp), content = content)
    }
}

@Composable
fun PrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier.fillMaxWidth(), enabled: Boolean = true, loading: Boolean = false) {
    Button(onClick = onClick, enabled = enabled && !loading, modifier = modifier.height(46.dp), shape = RoundedCornerShape(10.dp)) {
        if (loading) CircularProgressIndicator(Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp) else Text(text)
    }
}

@Composable
fun OutlineBtn(text: String, onClick: () -> Unit, modifier: Modifier = Modifier.fillMaxWidth(), enabled: Boolean = true) {
    OutlinedButton(onClick = onClick, enabled = enabled, modifier = modifier.height(46.dp), shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Primary)) { Text(text, color = Primary) }
}

@Composable
fun Pill(text: String, bg: Color, fg: Color) {
    Text(text, color = fg, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
        modifier = Modifier.background(bg, RoundedCornerShape(50)).padding(horizontal = 8.dp, vertical = 3.dp))
}

fun levelColors(l: String): Pair<Color, Color> = when (l.lowercase()) {
    "tinggi", "berisiko", "kedaluwarsa", "diblokir" -> RedBg to RedFg
    "sedang", "waspada", "draf" -> AmberBg to AmberFg
    "aman", "rendah", "aktif", "berlaku" -> GreenBg to GreenFg
    else -> PrimaryLight to Primary
}

@Composable
fun LevelPill(text: String) { val (bg, fg) = levelColors(text); Pill(text.uppercase(), bg, fg) }

@Composable
fun Field(
    label: String, value: String, onChange: (String) -> Unit, placeholder: String = "",
    keyboard: KeyboardType = KeyboardType.Text, error: String? = null, password: Boolean = false,
    singleLine: Boolean = true, minLines: Int = 1
) {
    Column {
        Text(label, fontSize = 13.sp, color = Muted, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = value, onValueChange = onChange, modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = Muted.copy(alpha = 0.6f)) },
            singleLine = singleLine, minLines = minLines, isError = error != null,
            supportingText = if (error != null) ({ Text(error) }) else null,
            keyboardOptions = KeyboardOptions(keyboardType = keyboard),
            visualTransformation = if (password) PasswordVisualTransformation() else VisualTransformation.None,
            shape = RoundedCornerShape(10.dp)
        )
    }
}

@Composable
fun SectionTitle(t: String) = Text(t, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)

@Composable
fun Hint(t: String, color: Color = Muted) = Text(t, fontSize = 12.sp, color = color)

@Composable
fun IconTile(icon: ImageVector, size: Int = 28) {
    Box(Modifier.size(size.dp).background(PrimaryLight, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
        Icon(icon, null, tint = Primary, modifier = Modifier.size((size * 0.6f).dp))
    }
}

data class NavTab(val route: String, val label: String, val icon: ImageVector)
val navTabs = listOf(
    NavTab("home", "Beranda", NIcons.Home), NavTab("mou", "MOU", NIcons.Mou), NavTab("uu", "UU", NIcons.Search),
    NavTab("chat", "Chat", NIcons.Chat), NavTab("docs", "Dokumen", NIcons.Docs)
)

@Composable
fun BottomBar(selected: String, onSelect: (String) -> Unit) {
    NavigationBar(containerColor = Color.White) {
        navTabs.forEach {
            NavigationBarItem(selected = selected == it.route, onClick = { onSelect(it.route) },
                icon = { Icon(it.icon, null) }, label = { Text(it.label, fontSize = 11.sp) })
        }
    }
}

/** Persetujuan pengguna sebelum teks dikirim ke AI (data pribadi tetap dianonimisasi). */
@Composable
fun rememberConsentGate(): (() -> Unit) -> Unit {
    val ctx = LocalContext.current
    var pending by remember { mutableStateOf<(() -> Unit)?>(null) }
    pending?.let { act ->
        AlertDialog(
            onDismissRequest = { pending = null },
            title = { Text("Izin penggunaan AI") },
            text = { Text("Teks dokumen/pertanyaan akan dikirim ke layanan Gemini untuk dianalisis. Data pribadi (nama, NIK, NPWP, telepon, email, rekening) diganti token lebih dulu, dan file asli tidak diunggah.") },
            confirmButton = { TextButton(onClick = { Prefs.setConsent(ctx, true); pending = null; act() }) { Text("Setuju") } },
            dismissButton = { TextButton(onClick = { pending = null }) { Text("Batal") } }
        )
    }
    return remember {
        { action: () -> Unit -> if (Prefs.hasConsent(ctx)) { action() } else { pending = action } }
    }
}
