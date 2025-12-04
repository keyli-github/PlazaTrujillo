//Draweritems.kt
package com.keyli.plazatrujillo.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.keyli.plazatrujillo.data.UserRole

// Modelo de datos con roles permitidos
data class DrawerItem(
    val title: String,
    val icon: ImageVector,
    val route: String,
    val allowedRoles: List<UserRole>? = null // null = todos pueden acceder
)

// Lista de opciones con control de acceso por rol (igual que React)
val drawerOptions = listOf(
    DrawerItem(
        title = "Dashboard", 
        icon = Icons.Default.Dashboard, 
        route = "dashboard",
        allowedRoles = null // Todos
    ),
    DrawerItem(
        title = "Usuarios", 
        icon = Icons.Default.Person, 
        route = "usuarios",
        allowedRoles = listOf(UserRole.ADMIN) // Solo admin
    ),
    DrawerItem(
        title = "Reservas", 
        icon = Icons.Default.DateRange, 
        route = "reservas",
        allowedRoles = null // Todos (housekeeping solo lectura)
    ),
    DrawerItem(
        title = "Caja de Cobros", 
        icon = Icons.Default.AttachMoney, 
        route = "caja",
        allowedRoles = listOf(UserRole.ADMIN, UserRole.RECEPTIONIST)
    ),
    DrawerItem(
        title = "Lavanderia", 
        icon = Icons.Default.LocalLaundryService, 
        route = "lavanderia",
        allowedRoles = listOf(UserRole.ADMIN, UserRole.HOUSEKEEPING)
    ),
    DrawerItem(
        title = "Mantenimiento", 
        icon = Icons.Default.Bolt, 
        route = "mantenimiento",
        allowedRoles = listOf(UserRole.ADMIN, UserRole.HOUSEKEEPING)
    ),
    DrawerItem(
        title = "Mensajes", 
        icon = Icons.Default.Email, 
        route = "mensajes",
        allowedRoles = null // Todos
    ),
    DrawerItem(
        title = "ChatBot", 
        icon = Icons.Default.Face, 
        route = "chatbot",
        allowedRoles = null // Todos
    ),
    DrawerItem(
        title = "Página Web", 
        icon = Icons.Default.Language, 
        route = "webview",
        allowedRoles = null // Todos
    )
)