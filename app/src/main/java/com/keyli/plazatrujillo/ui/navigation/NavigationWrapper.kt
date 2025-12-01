package com.keyli.plazatrujillo.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import com.keyli.plazatrujillo.R
import com.keyli.plazatrujillo.ui.components.AppDrawer
import com.keyli.plazatrujillo.ui.screens.*
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

    // --- LISTA DE PANTALLAS FULL SCREEN (Sin Barra Superior ni Menú Lateral) ---
    // Agrega aquí cualquier pantalla que deba ocupar todo el celular sin el logo arriba.
    val fullScreenRoutes = listOf(
        "login",
        "new_reservation",
        "new_reservation_screen",
        "new_movement",
        "new_comanda",
        "new_usuario",
        "register_briquetas", // Formulario Briquetas
        "bloq_habitacion",    // Formulario Bloqueo
        "report_incidencias"  // Formulario Incidencias
    )

    // Si la ruta actual NO está en la lista, mostramos las barras
    val showBars = currentRoute !in fullScreenRoutes

    // Cerrar el drawer automáticamente al cambiar de pantalla
    LaunchedEffect(currentRoute) {
        if (drawerState.isOpen) drawerState.close()
    }

    // ========================
    // CONTENIDO DE NAVEGACIÓN
    // ========================
    val content: @Composable (PaddingValues) -> Unit = { paddingValues ->

        NavHost(
            navController = navController,
            startDestination = "login",
            // IMPORTANTE: Este padding es lo que hace que el ChatBot quede
            // EXACTAMENTE debajo del logo, sin dejar huecos raros ni superponerse.
            modifier = Modifier.padding(paddingValues)
        ) {
            // --- LOGIN & DASHBOARD ---
            composable("login") {
                LoginScreen(
                    onLoginSuccess = {
                        scope.launch { drawerState.snapTo(DrawerValue.Closed) }
                        navController.navigate("dashboard") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }
            composable("dashboard") {
                DashboardScreen(navController, isDarkTheme, onToggleTheme)
            }

            // --- PANTALLAS PRINCIPALES ---
            composable("profile") { ProfileScreen(navController) }
            composable("usuarios") { UsuarioScreen(navController) }
            composable("reservas") { ReservaScreen(navController) }
            composable("caja") { CajaScreen(navController) }
            composable("lavanderia") { LavanderiaScreen(navController) }
            composable("comanda_screen") { ComandaScreen(navController) }

            // --- FORMULARIOS (Full Screen) ---
            composable("new_usuario") { NewUsuario(navController) }
            composable("new_reservation") { NewReservationScreen(navController) }

            // --- MANTENIMIENTO ---
            composable("mantenimiento") { MantenimientoScreen(navController) }
            composable("register_briquetas") { RegisterBriquetasScreen(navController) }
            composable("bloq_habitacion") { BloqHabitacionScreen(navController) }
            composable("report_incidencias") { ReportIncidenciasScreen(navController) }

            // --- COMUNICACIÓN ---
            // NOTA: Como NO están en 'fullScreenRoutes', mostrarán el Logo arriba.
            composable("mensajes") { MensajeScreen(navController) }
            composable("chatbot") { ChatBotScreen(navController) }
        }
    }

    // ========================
    // ESTRUCTURA VISUAL (SCAFFOLD)
    // ========================

    if (showBars) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Image(
                            painter = painterResource(id = R.drawable.logoo),
                            contentDescription = "Logo",
                            modifier = Modifier.height(40.dp) // Altura controlada del logo
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú", tint = Color.Black)
                        }
                    },
                    actions = {
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Opciones", tint = Color.Black)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White, // Fondo blanco para integrarse con el diseño
                        scrolledContainerColor = Color.White
                    )
                )
            }
        ) { padding ->
            // Menú Lateral (Drawer)
            ModalNavigationDrawer(
                drawerState = drawerState,
                gesturesEnabled = drawerState.isOpen,
                scrimColor = Color.Black.copy(alpha = 0.4f),
                drawerContent = {
                    AppDrawer(
                        currentRoute = currentRoute,
                        onNavigate = { route ->
                            scope.launch {
                                drawerState.close()
                                navController.navigate(route) {
                                    popUpTo("dashboard") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            ) {
                // Aquí pasamos el padding al NavHost
                content(padding)
            }
        }
    } else {
        // Pantallas Pantalla Completa (Login, Formularios)
        content(PaddingValues(0.dp))
    }
}