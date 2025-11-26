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
import com.keyli.plazatrujillo.ui.screens.ProfileScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationWrapper(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "login"

    // --- CORRECCIÓN AQUÍ ---
    // Ocultamos la barra y el menú si estamos en Login O en Perfil
    val showBars = currentRoute != "login" && currentRoute != "profile"

    // Colores dinámicos para la barra superior
    val topBarContainerColor = if (isDarkTheme) Color(0xFF1E1E1E) else Color.White
    val topBarContentColor = if (isDarkTheme) Color.White else Color.Black

    Scaffold(
        topBar = {
            // Solo mostramos la barra principal si showBars es true
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
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menú",
                                tint = topBarContentColor
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Opciones",
                                tint = topBarContentColor
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = topBarContainerColor
                    )
                )
            }
        }
    ) { paddingValues ->

        ModalNavigationDrawer(
            modifier = Modifier.padding(paddingValues),
            drawerState = drawerState,
            gesturesEnabled = showBars, // Bloquea el deslizar en Login y Perfil
            scrimColor = Color.Black.copy(alpha = 0.5f),
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
            // CONTENIDO DE NAVEGACIÓN
            NavHost(
                navController = navController,
                startDestination = "login"
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

                composable("dashboard") {
                    DashboardScreen(
                        navController = navController,
                        isDarkTheme = isDarkTheme,
                        onToggleTheme = onToggleTheme
                    )
                }

                // Pantalla de Perfil (Ahora ocupará toda la pantalla limpia)
                composable("profile") {
                    ProfileScreen(navController = navController)
                }

                // Placeholders
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
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Pantalla de $title", style = MaterialTheme.typography.headlineMedium)
    }
}