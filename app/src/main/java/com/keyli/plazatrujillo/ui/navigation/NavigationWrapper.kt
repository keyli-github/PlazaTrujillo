package com.keyli.plazatrujillo.ui.navigation

import com.keyli.plazatrujillo.ui.screens.DashboardScreen
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
import com.keyli.plazatrujillo.ui.screens.LoginScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Detectamos en qué pantalla estamos
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "login"

    // Lógica para saber si mostramos el menú y la barra
    val showBars = currentRoute != "login"

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = showBars, // Bloquea el deslizar para abrir menú en el Login
        drawerContent = {
            // Solo renderizamos el contenido del menú si no estamos en login
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
        Scaffold(
            topBar = {
                // Solo mostramos la barra superior si NO estamos en Login
                if (showBars) {
                    CenterAlignedTopAppBar(
                        title = {
                            // Asegúrate de tener logo en res/drawable
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
                            containerColor = Color.White
                        )
                    )
                }
            }
        ) { paddingValues ->
            // AQUÍ ESTÁ LA MAGIA: El NavHost contiene TODO, incluido el Login
            NavHost(
                navController = navController,
                startDestination = "login", // Arrancamos en Login
                modifier = Modifier.padding(paddingValues)
            ) {
                // Pantalla de Login
                composable("login") {
                    LoginScreen(
                        onLoginSuccess = {
                            // Al loguearse, vamos al dashboard y borramos el login del historial
                            navController.navigate("dashboard") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    )
                }

                // Pantalla Dashboard
                composable("dashboard") {
                    DashboardScreen(navController)
                }

                // Resto de pantallas (Placeholders)
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
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Pantalla de $title", style = MaterialTheme.typography.headlineMedium)
    }
}