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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.keyli.plazatrujillo.ui.viewmodel.UserViewModel
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

    // 1. AGREGA "recovery" AQUI PARA QUE NO SALGA EL MENÚ
    val fullScreenRoutes = listOf(
        "login", "recovery", "new_reservation", "new_reservation_screen", "new_movement",
        "new_comanda", "new_usuario", "edit_usuario", "register_briquetas", "bloq_habitacion",
        "report_incidencias", "profile",
    )

    val showBars = currentRoute !in fullScreenRoutes

    LaunchedEffect(currentRoute) {
        if (drawerState.isOpen) drawerState.close()
    }

    val content: @Composable (PaddingValues) -> Unit = { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(paddingValues)
        ) {

            // --- LOGIN (ACTUALIZADO) ---
            composable("login") {
                LoginScreen(
                    onLoginSuccess = {
                        scope.launch { drawerState.snapTo(DrawerValue.Closed) }
                        navController.navigate("dashboard") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    // 2. NUEVO PARAMETRO DE NAVEGACIÓN
                    onNavigateToRecovery = {
                        navController.navigate("recovery")
                    }
                )
            }

            // --- RECUPERAR CONTRASEÑA (NUEVO) ---
            composable("recovery") {
                RecupContraseña(
                    onNavigateToLogin = {
                        navController.popBackStack() // Regresa al Login
                    }
                )
            }

            // --- DASHBOARD ---
            composable("dashboard") {
                DashboardScreen(navController, isDarkTheme, onToggleTheme)
            }

            // --- RESTO DE PANTALLAS ---
            composable("profile") { ProfileScreen(navController) }
            composable("usuarios") { UsuarioScreen(navController) }
            composable("reservas") { ReservaScreen(navController) }
            composable("caja") { CajaScreen(navController) }
            composable("lavanderia") { LavanderiaScreen(navController) }
            composable("comanda_screen") { ComandaScreen(navController) }
            composable("new_usuario") { NewUsuario(navController) }
            composable("edit_usuario/{uid}") { backStackEntry ->
                val uid = backStackEntry.arguments?.getString("uid") ?: ""
                EditUsuarioScreen(navController, uid)
            }
            composable("new_reservation") { NewReservationScreen(navController) }
            composable("mantenimiento") { MantenimientoScreen(navController) }
            composable("register_briquetas") { RegisterBriquetasScreen(navController) }
            composable("bloq_habitacion") { BloqHabitacionScreen(navController) }
            composable("report_incidencias") { ReportIncidenciasScreen(navController) }
            composable("mensajes") { MensajeScreen(navController) }
            composable("chatbot") { ChatBotScreen(navController) }
        }
    }

    if (showBars) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Image(
                            painter = painterResource(id = R.drawable.logoo),
                            contentDescription = "Logo",
                            modifier = Modifier.height(40.dp),
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú", tint = MaterialTheme.colorScheme.onSurface)
                        }
                    },
                    actions = {
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Opciones", tint = MaterialTheme.colorScheme.onSurface)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        scrolledContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        ) { padding ->
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
                content(padding)
            }
        }
    } else {
        content(PaddingValues(0.dp))
    }
}