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

    val showBars = currentRoute != "login" &&
            currentRoute != "profile" &&
            currentRoute != "new_reservation" &&
            currentRoute != "new_movement" &&
            currentRoute != "new_comanda"

    // cerrar drawer siempre al cambiar de ruta
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
            modifier = Modifier.padding(paddingValues)
        ) {

            composable("login") {
                LoginScreen(
                    onLoginSuccess = {
                        scope.launch {
                            drawerState.snapTo(DrawerValue.Closed)
                        }
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

            composable("usuarios") { UsuarioScreen(navController) }
            composable("reservas") { ReservaScreen(navController) }
            composable("new_reservation") { NewReservationScreen(navController) }
            composable("new_movement") { NewMovementScreen(navController) }
            composable("new_comanda") { ComandaScreen(navController) }
            composable("caja") { CajaScreen(navController) }
            composable("lavanderia") { LavanderiaScreen(navController) }
            composable("mantenimiento") { MantenimientoScreen(navController) }
            composable("mensajes") { MensajeScreen(navController) }
            composable("chatbot") { ChatBotScreen(navController) }

        }
    }

    // ========================
    // UI PRINCIPAL
    // ========================

    if (showBars) {

        Scaffold(
            topBar = {

                CenterAlignedTopAppBar(

                    title = {
                        Image(
                            painter = painterResource(id = R.drawable.logoo),
                            contentDescription = "Logo",
                            modifier = Modifier.height(40.dp)
                        )
                    },

                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        ) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                    },

                    actions = {
                        IconButton(onClick = {}) {
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

        // Login sin Drawer ni AppBar
        content(PaddingValues())

    }
}
