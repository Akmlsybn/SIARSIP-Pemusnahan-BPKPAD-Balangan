package com.example.arsipbpkpad.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,                // Color(0xFF1B5E20)
    onPrimary = White,
    primaryContainer = LightGreen,         // Color(0xFFE8F5E9)
    onPrimaryContainer = DarkGreen,
    secondary = PrimaryGreen,
    onSecondary = White,
    secondaryContainer = SecondaryGreenContainer, // Color(0xFFC8E6C9)
    onSecondaryContainer = DarkGreen,
    tertiary = ChipBlue,
    onTertiary = White,
    tertiaryContainer = ChipBlueBg,
    onTertiaryContainer = ChipBlue,
    background = BackgroundBlue,           // Color(0xFFF8FAF8)
    surface = White,                       // Color(0xFFFFFFFF)
    onBackground = TextPrimary,            // Color(0xFF1A231E)
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFFEBF2E9),    // Light soft green card background
    onSurfaceVariant = TextSecondary,      // Color(0xFF454D47)
    error = ErrorRed,
    onError = White
)

@Composable
fun ArsipBPKPADTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
