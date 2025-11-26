package com.keyli.plazatrujillo.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.keyli.plazatrujillo.R
import com.keyli.plazatrujillo.ui.components.AppDrawer
import com.keyli.plazatrujillo.ui.screens.DashboardScreen
import com.keyli.plazatrujillo.ui.screens.LoginScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "login"

    // Lógica para saber si mostramos las barras
    val showBars = currentRoute != "login"

    // 1. EL SCAFFOLD ES EL PADRE (Para que la TopBar esté siempre encima)
    Scaffold(
        topBar = {
            // Solo mostramos la barra si no es Login
            if (showBars) {
                CenterAlignedTopAppBar(
                    title = {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "Logo",
                            modifier = Modifier.height(40.dp)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                    },
                    actions = {
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Opciones")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White,
                        titleContentColor = Color.Black,
                        navigationIconContentColor = Color.Black,
                        actionIconContentColor = Color.Black
                    )
                )
            }
        }
    ) { paddingValues ->

        // 2. EL DRAWER ESTÁ DENTRO DEL CONTENIDO DEL SCAFFOLD
        // Usamos 'modifier.padding(paddingValues)' para empujarlo debajo de la TopBar
        ModalNavigationDrawer(
            modifier = Modifier.padding(paddingValues),
            drawerState = drawerState,
            gesturesEnabled = showBars, // Bloquea gestos en Login
            scrimColor = Color.Black.copy(alpha = 0.5f), // Sombra oscura solo en el contenido
            drawerContent = {
                if (showBars) {
                    AppDrawer(
                        currentRoute = currentRoute,
                        onNavigate = { route ->
                            navController.navigate(route) {
                                popUpTo("dashboard") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onCloseDrawer = { scope.launch { drawerState.close() } }
                    )
                }
            }
        ) {
            // 3. CONTENIDO DE NAVEGACIÓN (NavHost)
            NavHost(
                navController = navController,
                startDestination = "login" // Arranca en Login
                // Nota: Ya no ponemos padding aquí porque el Drawer ya lo tiene
            ) {
                composable("login") {
                    LoginScreen(
                        onLoginSuccess = {
                            navController.navigate("dashboard") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    )
                }

                composable("dashboard") { DashboardScreen(navController) }

                // Pantallas Placeholder
                composable("usuarios") { ScreenPlaceholder("Usuarios") }
                composable("reservas") { ScreenPlaceholder("Reservas") }
                composable("caja") { ScreenPlaceholder("Caja") }
                composable("lavanderia") { ScreenPlaceholder("Lavandería") }
                composable("mantenimiento") { ScreenPlaceholder("Mantenimiento") }
                composable("mensajes") { ScreenPlaceholder("Mensajes") }
                composable("chatbot") { ScreenPlaceholder("ChatBot") }
            }
        }
    }
}

@Composable
fun ScreenPlaceholder(title: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), // Un poco de margen interno
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Pantalla de $title", style = MaterialTheme.typography.headlineMedium)
    }
}