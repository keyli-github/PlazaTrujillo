package com.keyli.plazatrujillo

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.keyli.plazatrujillo.data.websocket.WebSocketService
import com.keyli.plazatrujillo.ui.navigation.NavigationWrapper
import com.keyli.plazatrujillo.ui.theme.PlazaTrujilloTheme
import com.keyli.plazatrujillo.ui.theme.ThemeViewModel

// FragmentActivity es necesario para la autenticación biométrica
class MainActivity : FragmentActivity() {
    
    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        if (user != null) {
            // Usuario autenticado, conectar WebSocket
            WebSocketService.connect()
        } else {
            // Usuario no autenticado, desconectar WebSocket
            WebSocketService.disconnect()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Registrar listener de autenticación
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener)

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
    
    override fun onDestroy() {
        super.onDestroy()
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener)
        WebSocketService.disconnect()
    }
}