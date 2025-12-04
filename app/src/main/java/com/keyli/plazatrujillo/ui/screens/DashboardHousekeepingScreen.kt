package com.keyli.plazatrujillo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.keyli.plazatrujillo.data.UserRole
import com.keyli.plazatrujillo.data.UserSession
import com.keyli.plazatrujillo.ui.theme.OrangePrimary
import com.keyli.plazatrujillo.ui.theme.StatusRed
import com.keyli.plazatrujillo.ui.viewmodel.MantenimientoViewModel
import com.keyli.plazatrujillo.ui.viewmodel.ReservationViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardHousekeepingScreen(
    navController: NavController,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onNavigateToMantenimiento: () -> Unit = {}
) {
    val mantenimientoViewModel: MantenimientoViewModel = viewModel()
    val reservationViewModel: ReservationViewModel = viewModel()
    
    val mantenimientoState by mantenimientoViewModel.uiState.collectAsState()
    val reservationState by reservationViewModel.uiState.collectAsState()
    
    // Datos del usuario
    val currentUserName by UserSession.userName.collectAsState()
    val currentUserEmail by UserSession.userEmail.collectAsState()
    
    // Estados para menús
    var showProfileMenu by remember { mutableStateOf(false) }
    var showNotifications by remember { mutableStateOf(false) }
    
    val textColor = MaterialTheme.colorScheme.onBackground
    val subTextColor = if (isDarkTheme) Color.Gray else Color.DarkGray
    val menuBgColor = if (isDarkTheme) Color(0xFF2C2C2C) else Color.White
    
    // Cargar datos
    LaunchedEffect(Unit) {
        mantenimientoViewModel.loadSystemStatus()
        mantenimientoViewModel.loadIssues()
        mantenimientoViewModel.loadBlockedRooms()
        mantenimientoViewModel.loadAllRooms()
        reservationViewModel.loadReservations()
        reservationViewModel.loadAllRooms()
    }
    
    // Calcular estadísticas
    val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
    
    val rooms = reservationState.allRooms
    val reservations = reservationState.reservations
    val blockedRooms = mantenimientoState.blockedRooms
    val issues = mantenimientoState.issues
    val systemStatus = mantenimientoState.waterHeatingSystem
    
    val totalRooms = rooms.size
    
    // Habitaciones ocupadas (reservas con Check-in activo)
    val occupiedRoomCodes = remember(reservations) {
        reservations.filter { reservation ->
            val status = reservation.status?.lowercase() ?: ""
            if (status == "cancelada") return@filter false
            
            val checkIn = reservation.checkIn ?: return@filter false
            val checkOut = reservation.checkOut ?: return@filter false
            
            // Reserva activa si checkIn <= hoy < checkOut
            today >= checkIn && today < checkOut
        }.flatMap { it.rooms ?: listOf(it.room ?: "") }.toSet()
    }
    
    // Habitaciones bloqueadas activas
    val blockedRoomCodes = remember(blockedRooms) {
        blockedRooms.filter { blocked ->
            val blockedUntil = blocked.blockedUntil ?: return@filter false
            blockedUntil >= today
        }.map { it.room ?: "" }.toSet()
    }
    
    val occupiedCount = occupiedRoomCodes.size
    val blockedCount = blockedRoomCodes.size
    val availableCount = maxOf(0, totalRooms - occupiedCount - blockedCount)
    
    // Incidencias pendientes (últimas 5) - todas las incidencias son pendientes
    val pendingIssues = issues.take(5)
    
    // Check-ins y Check-outs del día
    val todayCheckIns = reservations.filter { it.checkIn == today && it.status?.lowercase() != "cancelada" }
    val todayCheckOuts = reservations.filter { it.checkOut == today && it.status?.lowercase() != "cancelada" }
    
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header con Hola, nombre y iconos
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Hola, ${currentUserName ?: "Hotelero"}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                        Text(
                            text = "Aquí tienes el resumen de hoy",
                            fontSize = 14.sp,
                            color = subTextColor
                        )
                    }
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Botón tema
                        IconBox(
                            icon = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            onClick = onToggleTheme,
                            isDarkTheme = isDarkTheme
                        )
                        // Botón notificaciones
                        IconBox(
                            icon = Icons.Default.Notifications,
                            onClick = { showNotifications = true },
                            isDarkTheme = isDarkTheme
                        )
                        // Botón perfil
                        IconBox(
                            icon = Icons.Default.Person,
                            onClick = { showProfileMenu = true },
                            isDarkTheme = isDarkTheme
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        
        // Tarjetas de estado de habitaciones
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Total",
                    value = "$totalRooms Hab.",
                    icon = Icons.Default.Home,
                    iconColor = Color(0xFF3B82F6),
                    bgColor = Color(0xFFDBEAFE)
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Disponibles",
                    value = "$availableCount Hab.",
                    icon = Icons.Default.CheckCircle,
                    iconColor = Color(0xFF22C55E),
                    bgColor = Color(0xFFDCFCE7)
                )
            }
        }
        
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Ocupadas",
                    value = "$occupiedCount Hab.",
                    icon = Icons.Default.Group,
                    iconColor = OrangePrimary,
                    bgColor = OrangePrimary.copy(alpha = 0.15f)
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Bloqueadas",
                    value = "$blockedCount Hab.",
                    icon = Icons.Default.Lock,
                    iconColor = Color(0xFFEF4444),
                    bgColor = Color(0xFFFEE2E2)
                )
            }
        }
        
        // Check-in y Check-out del día
        item {
            Text(
                text = "Hoy",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CheckCard(
                    modifier = Modifier.weight(1f),
                    title = "Check-ins",
                    count = todayCheckIns.size,
                    color = Color(0xFF22C55E)
                )
                CheckCard(
                    modifier = Modifier.weight(1f),
                    title = "Check-outs",
                    count = todayCheckOuts.size,
                    color = Color(0xFFEF4444)
                )
            }
        }
        
        // Sistema de Agua Caliente
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Sistema de Agua Caliente",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Estado: ",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        AssistChip(
                            onClick = { },
                            label = { Text(systemStatus.operationalStatus) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (systemStatus.operationalStatus == "Operativo") 
                                    Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                                labelColor = if (systemStatus.operationalStatus == "Operativo") 
                                    Color(0xFF22C55E) else Color(0xFFEF4444)
                            )
                        )
                    }
                    
                    systemStatus.nextMaintenanceDate?.let { nextDate ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Próximo Cambio: $nextDate ${systemStatus.nextMaintenanceTime ?: ""}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    TextButton(
                        onClick = onNavigateToMantenimiento
                    ) {
                        Text(
                            text = "Ver detalles →",
                            color = OrangePrimary
                        )
                    }
                }
            }
        }
        
        // Incidencias Pendientes
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Incidencias Pendientes",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        TextButton(onClick = onNavigateToMantenimiento) {
                            Text("Ver todas →", color = OrangePrimary)
                        }
                    }
                    
                    if (pendingIssues.isEmpty()) {
                        Text(
                            text = "No hay incidencias pendientes",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
        
        // Lista de incidencias
        items(pendingIssues) { issue ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Hab. ${issue.room}",
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            PriorityBadge(priority = issue.priority ?: "Media")
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = issue.problem ?: "",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        
        // Habitaciones Bloqueadas
        if (blockedRooms.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Habitaciones Bloqueadas",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            TextButton(onClick = onNavigateToMantenimiento) {
                                Text("Ver todas →", color = OrangePrimary)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        blockedRooms.take(5).forEach { blocked ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Hab. ${blocked.room}",
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Hasta: ${blocked.blockedUntil ?: "-"}",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // Espacio final
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
    
    // MENÚS FLOTANTES (Perfil y Notificaciones)
    if (showProfileMenu) {
        Box(modifier = Modifier.fillMaxSize().clickable { showProfileMenu = false }) {
            Box(modifier = Modifier.align(Alignment.TopEnd).padding(top = 50.dp, end = 8.dp)) {
                MaterialTheme(shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(12.dp))) {
                    DropdownMenu(
                        expanded = true, 
                        onDismissRequest = { showProfileMenu = false }, 
                        modifier = Modifier.background(menuBgColor).width(240.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Hotelero", fontSize = 12.sp, color = Color.Gray)
                            Text(currentUserEmail ?: "Sin correo", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = textColor)
                        }
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                        DropdownMenuItem(
                            text = { Text("Editar Perfil", color = textColor) }, 
                            leadingIcon = { Icon(Icons.Default.Edit, null, tint = textColor) }, 
                            onClick = { showProfileMenu = false; navController.navigate("profile") }
                        )
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                        DropdownMenuItem(
                            text = { Text("Cerrar Sesión", color = StatusRed, fontWeight = FontWeight.Bold) }, 
                            leadingIcon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, null, tint = StatusRed) }, 
                            onClick = { 
                                showProfileMenu = false
                                UserSession.clear()
                                navController.navigate("login") { popUpTo(0) } 
                            }
                        )
                    }
                }
            }
        }
    }
    
    if (showNotifications) {
        Box(modifier = Modifier.fillMaxSize().clickable { showNotifications = false }) {
            Box(modifier = Modifier.align(Alignment.TopEnd).padding(top = 50.dp, end = 50.dp)) {
                MaterialTheme(shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(12.dp))) {
                    DropdownMenu(
                        expanded = true, 
                        onDismissRequest = { showNotifications = false }, 
                        modifier = Modifier.width(320.dp).background(menuBgColor)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Notificaciones", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textColor)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("No hay notificaciones nuevas", fontSize = 14.sp, color = subTextColor)
                        }
                    }
                }
            }
        }
    }
  }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    bgColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(bgColor, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = title,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun CheckCard(
    modifier: Modifier = Modifier,
    title: String,
    count: Int,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = count.toString(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
private fun PriorityBadge(priority: String) {
    val (bgColor, textColor) = when (priority.lowercase()) {
        "alta" -> Color(0xFFFEE2E2) to Color(0xFFEF4444)
        "media" -> Color(0xFFFEF3C7) to Color(0xFFF59E0B)
        "baja" -> Color(0xFFDCFCE7) to Color(0xFF22C55E)
        else -> Color(0xFFFEF3C7) to Color(0xFFF59E0B)
    }
    
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = bgColor
    ) {
        Text(
            text = priority,
            fontSize = 12.sp,
            color = textColor,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}
