package com.keyli.plazatrujillo.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Esquema CLARO
private val LightColorScheme = lightColorScheme(
    primary = OrangePrimary,
    secondary = OrangeSecondary,
    background = LightBackground,
    surface = LightSurface,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    onBackground = TextBlack,
    onSurface = TextBlack
)

// Esquema OSCURO
private val DarkColorScheme = darkColorScheme(
    primary = OrangePrimary,
    secondary = OrangeSecondary,
    background = DarkBackground, // Negro suave
    surface = DarkSurface,       // Gris oscuro
    onPrimary = androidx.compose.ui.graphics.Color.White,
    onBackground = TextWhite,    // Texto blanco
    onSurface = TextWhite
)

@Composable
fun PlazaTrujilloTheme(
    darkTheme: Boolean, // AHORA ES OBLIGATORIO PASAR ESTE VALOR
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            // Si el tema es oscuro, los iconos de la barra NO deben ser oscuros (false)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography, // Asegúrate de que Type.kt tenga "AppTypography"
        content = content
    )
}