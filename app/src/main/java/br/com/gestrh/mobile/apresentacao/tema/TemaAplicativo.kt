package br.com.gestrh.mobile.apresentacao.tema

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val coresDoAplicativo = lightColorScheme(
    primary = Color(0xFF155E75),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCDEEF5),
    onPrimaryContainer = Color(0xFF123F4D),
    background = Color(0xFFF6F8FA),
    onBackground = Color(0xFF18232B),
    surface = Color.White,
    onSurface = Color(0xFF18232B),
    onSurfaceVariant = Color(0xFF465760)
)

@Composable
fun TemaGestRH(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = coresDoAplicativo,
        typography = Typography(),
        content = content
    )
}
