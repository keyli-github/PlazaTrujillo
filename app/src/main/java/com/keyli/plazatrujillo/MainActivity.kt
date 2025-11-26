package com.keyli.plazatrujillo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.keyli.plazatrujillo.ui.navigation.NavigationWrapper // Importante: Importar tu navegación
import com.keyli.plazatrujillo.ui.theme.PlazaTrujilloTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Esto hace que la app ocupe toda la pantalla (detrás de la barra de estado)

        setContent {
            PlazaTrujilloTheme {
                // Aquí llamamos a nuestro componente principal de navegación
                // Él decidirá si mostrar el Login o el Dashboard
                NavigationWrapper()
            }
        }
    }
}