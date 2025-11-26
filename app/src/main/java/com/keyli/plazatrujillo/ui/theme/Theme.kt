package com.keyli.plazatrujillo.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Esquema de colores CLARO (El principal que usaremos)
private val LightColorScheme = lightColorScheme(
    primary = OrangePrimary,
    secondary = OrangeSecondary,
    background = LightBackground, // El gris suave #F5F7FA
    surface = LightSurface,       // Blanco puro #FFFFFF
    onPrimary = androidx.compose.ui.graphics.Color.White, // Texto sobre botones naranja
    onSecondary = androidx.compose.ui.graphics.Color.White,
    onBackground = TextBlack,     // Texto oscuro sobre fondo claro
    onSurface = TextBlack         // Texto oscuro sobre tarjetas
)

// Esquema de colores OSCURO (Por si acaso el usuario fuerza el modo oscuro en el sistema)
// Aunque en tu MainActivity lo forzaremos a usar LightColorScheme si quieres que siempre sea blanca.
private val DarkColorScheme = darkColorScheme(
    primary = OrangePrimary,
    secondary = OrangeSecondary,
    background = androidx.compose.ui.graphics.Color(0xFF121212),
    surface = androidx.compose.ui.graphics.Color(0xFF1E1E1E),
    onPrimary = androidx.compose.ui.graphics.Color.White,
    onBackground = androidx.compose.ui.graphics.Color.White,
    onSurface = androidx.compose.ui.graphics.Color.White
)

@Composable
fun PlazaTrujilloTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color está disponible en Android 12+
    dynamicColor: Boolean = false, // Lo ponemos en false para priorizar TUS colores (naranja) sobre los del sistema
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // Si quieres forzar el tema claro siempre, cambia la lógica aquí o pasa darkTheme = false al llamar al tema
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Pinta la barra de estado del color primario o background según prefieras.
            // Aquí la ponemos del color de fondo para que se vea integrada.
            window.statusBarColor = colorScheme.background.toArgb()

            // Si el tema es claro (no oscuro), los iconos de la barra (hora, batería) deben ser oscuros para verse.
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography, // Asegúrate de tener Type.kt creado, si no, borra esta línea
        content = content
    )
}