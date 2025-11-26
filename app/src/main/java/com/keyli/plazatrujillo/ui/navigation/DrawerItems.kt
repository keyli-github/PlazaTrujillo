package com.keyli.plazatrujillo.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

// Modelo de datos simple
data class DrawerItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)

// Lista de opciones basada en tu imagen
val drawerOptions = listOf(
    DrawerItem("Dashboard", Icons.Default.Dashboard, "dashboard"),
    DrawerItem("Usuarios", Icons.Default.Person, "usuarios"),
    DrawerItem("Reservas", Icons.Default.DateRange, "reservas"),
    DrawerItem("Caja de Cobros", Icons.Default.AttachMoney, "caja"),
    DrawerItem("Lavanderia", Icons.Default.LocalLaundryService, "lavanderia"),
    DrawerItem("Mantenimiento", Icons.Default.Bolt, "mantenimiento"), // Icono de rayo para mantenimiento
    DrawerItem("Mensajes", Icons.Default.Email, "mensajes"),
    DrawerItem("ChatBot", Icons.Default.Face, "chatbot")
)