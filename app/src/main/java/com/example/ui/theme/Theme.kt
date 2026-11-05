package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = KhojPrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0ECFF),
    onPrimaryContainer = KhojDeepBlue,
    secondary = KhojAccentOrange,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFECCC),
    onSecondaryContainer = Color(0xFF7A3E00),
    tertiary = KhojDeepBlue,
    onTertiary = Color.White,
    background = KhojSoftBg,
    onBackground = Gray900,
    surface = KhojLightBg,
    onSurface = Gray900,
    surfaceVariant = Color(0xFFE8EEF8),
    onSurfaceVariant = Gray700,
    outline = Gray300
)

private val DarkColorScheme = darkColorScheme(
    primary = KhojPrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = KhojDeepBlue,
    onPrimaryContainer = Color(0xFFD3E3FD),
    secondary = KhojAccentOrange,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF633200),
    onSecondaryContainer = Color(0xFFFFDDB8),
    tertiary = Color(0xFF82B1FF),
    onTertiary = Color.Black,
    background = KhojDarkBg,
    onBackground = Color(0xFFE2E8F0),
    surface = KhojDarkCard,
    onSurface = Color(0xFFE2E8F0),
    surfaceVariant = KhojDarkSurface,
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF334155)
)

@Composable
fun KhojTheme(
    darkTheme: Boolean = false,
    themePalette: String = "Ocean Blue",
    content: @Composable () -> Unit
) {
    val baseScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val colorScheme = when (themePalette) {
        "Emerald" -> if (darkTheme) {
            baseScheme.copy(primary = EmeraldGreen, onPrimary = Color.Black)
        } else {
            baseScheme.copy(primary = EmeraldGreen, onPrimary = Color.White)
        }
        "Purple Dream" -> if (darkTheme) {
            baseScheme.copy(primary = PurpleDream, onPrimary = Color.Black)
        } else {
            baseScheme.copy(primary = PurpleDream, onPrimary = Color.White)
        }
        "Sunset Orange" -> if (darkTheme) {
            baseScheme.copy(primary = SunsetOrange, onPrimary = Color.Black)
        } else {
            baseScheme.copy(primary = SunsetOrange, onPrimary = Color.White)
        }
        "Midnight Black" -> if (darkTheme) {
            baseScheme.copy(background = MidnightBlack, surface = Color(0xFF12161F))
        } else baseScheme
        else -> baseScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
