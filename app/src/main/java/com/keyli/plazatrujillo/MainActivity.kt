package com.keyli.plazatrujillo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.keyli.plazatrujillo.ui.navigation.NavigationWrapper
import com.keyli.plazatrujillo.ui.theme.PlazaTrujilloTheme
import com.keyli.plazatrujillo.ui.theme.ThemeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            // 1. Instanciamos el ViewModel que recuerda si es de día o de noche
            val themeViewModel: ThemeViewModel = viewModel()
            val isDarkTheme = themeViewModel.isDarkTheme

            // 2. Aplicamos el tema a toda la app
            PlazaTrujilloTheme(darkTheme = isDarkTheme) {
                // 3. Pasamos el estado y la función de cambiar tema a la navegación
                NavigationWrapper(
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = { themeViewModel.toggleTheme() }
                )
            }
        }
    }
}