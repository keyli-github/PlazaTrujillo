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
import com.keyli.plazatrujillo.ui.screens.ChatBotScreen
import com.keyli.plazatrujillo.ui.screens.ComandaScreen
import com.keyli.plazatrujillo.ui.screens.DashboardScreen
import com.keyli.plazatrujillo.ui.screens.LavanderiaScreen
import com.keyli.plazatrujillo.ui.screens.LoginScreen
import com.keyli.plazatrujillo.ui.screens.MantenimientoScreen
import com.keyli.plazatrujillo.ui.screens.MensajeScreen
import com.keyli.plazatrujillo.ui.screens.NewMovementScreen
import com.keyli.plazatrujillo.ui.screens.NewReservationScreen
import com.keyli.plazatrujillo.ui.screens.ProfileScreen
import com.keyli.plazatrujillo.ui.screens.ReservaScreen
import com.keyli.plazatrujillo.ui.screens.UsuarioScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationWrapper(
    isDarkTheme: Boolean,      // 1. AÑADIDO: Recibe el estado del tema
    onToggleTheme: () -> Unit  // 2. AÑADIDO: Recibe la función para cambiar
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "login"

    val showBars = currentRoute != "login" && currentRoute != "profile" && currentRoute != "new_reservation" && currentRoute != "new_movement" && currentRoute != "new_comanda"

    // Adaptamos el color de la barra según el tema que recibimos
    val topBarContainerColor = if (isDarkTheme) Color(0xFF1E1E1E) else Color.White
    val topBarContentColor = if (isDarkTheme) Color.White else Color.Black

    Scaffold(
        topBar = {
            if (showBars) {
                CenterAlignedTopAppBar(
                    title = {
                        Image(
                            painter = painterResource(id = R.drawable.logohp),
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
            gesturesEnabled = showBars,
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
                    // 3. SOLUCIÓN DEL ERROR: Pasamos los parámetros aquí
                    DashboardScreen(
                        navController = navController,
                        isDarkTheme = isDarkTheme,
                        onToggleTheme = onToggleTheme
                    )
                }

                composable("profile") {
                    ProfileScreen(navController = navController)
                }

                composable("usuarios") {
                    UsuarioScreen(navController = navController) // Llamada a la pantalla real
                }

                composable("mantenimiento") {
                    MantenimientoScreen(navController = navController) // Llamada a la pantalla real
                }

                composable("chatbot") {
                    ChatBotScreen(navController = navController) // Llamada a la pantalla real
                }

                composable("mensajes") {
                    MensajeScreen(navController = navController) // Llamada a la pantalla real
                }

                composable("reservas") {
                    ReservaScreen(navController = navController)
                }

                composable("new_reservation") {
                    NewReservationScreen(navController = navController)
                }

                composable("new_movement") {
                    NewMovementScreen(navController = navController)
                }

                composable("new_comanda") {
                    ComandaScreen(navController = navController)
                }
                composable("caja") { ScreenPlaceholder("Caja") }

                composable("lavanderia") {
                    LavanderiaScreen(navController = navController)
                }

            }
        }
    }
}

@Composable
fun ScreenPlaceholder(title: String) {
    Box(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Pantalla de $title", style = MaterialTheme.typography.headlineMedium)
    }
}