package com.fadetogo.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val FadeToGoDarkColorScheme = darkColorScheme(
    primary = AccentWhite,
    onPrimary = DeepBlack,
    secondary = TextSilver,
    onSecondary = DeepBlack,
    background = DeepBlack,
    onBackground = TextWhite,
    surface = SurfaceBlack,
    onSurface = TextWhite,
    surfaceVariant = CardBlack,
    onSurfaceVariant = TextSilver,
    error = ErrorRed,
    onError = TextWhite,
    outline = DividerGray
)

@Composable
fun FadeToGoTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = FadeToGoDarkColorScheme,
        typography = Typography,
        content = content
    )
}