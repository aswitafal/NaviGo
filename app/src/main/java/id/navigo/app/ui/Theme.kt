package id.navigo.app.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Palet diambil dari wireframe Figma: biru tua + putih
val Primary = Color(0xFF1E4DA1)
val PrimaryLight = Color(0xFFDCE6F7)
val Bg = Color(0xFFF5F7FB)
val Ink = Color(0xFF1B1F2A)
val Muted = Color(0xFF6B7385)
val Border = Color(0xFFE1E6F0)
val RedFg = Color(0xFFC93434); val RedBg = Color(0xFFFDE3E3)
val AmberFg = Color(0xFFB7791F); val AmberBg = Color(0xFFFFF0C8)
val GreenFg = Color(0xFF2F855A); val GreenBg = Color(0xFFDDF3E4)

@Composable
fun NaviGoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Primary, onPrimary = Color.White, primaryContainer = PrimaryLight, onPrimaryContainer = Primary,
            secondaryContainer = PrimaryLight, onSecondaryContainer = Primary,
            background = Bg, onBackground = Ink, surface = Color.White, onSurface = Ink,
            surfaceVariant = Color(0xFFEEF2FA), onSurfaceVariant = Muted, outline = Border, error = RedFg
        ),
        content = content
    )
}
