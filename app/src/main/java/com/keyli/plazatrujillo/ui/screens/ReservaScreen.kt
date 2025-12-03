package com.keyli.plazatrujillo.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.keyli.plazatrujillo.ui.theme.*
import com.keyli.plazatrujillo.ui.viewmodel.ReservationViewModel
import com.keyli.plazatrujillo.data.model.Room
import com.keyli.plazatrujillo.data.model.Reservation
import com.keyli.plazatrujillo.data.model.UpdateReservationRequest
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.keyli.plazatrujillo.data.model.CalendarEvent

// --- DATA CLASSES ---
data class RoomItem(
    val number: String,
    val type: String,
    val status: String,
    val statusColor: Color
)

data class ReservaItem(
    val id: String,
    val nombre: String,
    val habitacion: String,
    val tipoHab: String,
    val precio: String,
    val estado: String,
    val estadoColor: Color
)

data class CalendarDayItem(
    val dayString: String,
    val fullDate: String, // Para saber qué fecha exacta es (YYYY-MM-DD)
    val isCurrentMonth: Boolean,
    val isToday: Boolean
)

@Composable
fun ReservaScreen(
    navController: NavHostController,
    viewModel: ReservationViewModel = viewModel()
) {
    val scrollState = rememberScrollState()
    val uiState by viewModel.uiState.collectAsState()

    // Cargar datos al iniciar
    LaunchedEffect(Unit) {
        viewModel.loadReservations()
        viewModel.loadAllRooms()
        viewModel.loadCalendarEvents()
        viewModel.loadCalendarNotes()
    }

    // 0 = Estado de Habitaciones
    // 1 = Reservas/Ventas Activas
    // 2 = Historial de Clientes
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Fondo Global
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {

        // --- CABECERA CON EL BOTÓN NUEVO ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Gestión Hotelera",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground // Texto dinámico
            )

            // --- BOTÓN NUEVA RESERVA ---
            Button(
                onClick = {
                    navController.navigate("new_reservation")
                },
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Nueva reserva",
                    fontSize = 12.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Administra habitaciones, ventas y clientes",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) // Gris dinámico
        )

        Spacer(modifier = Modifier.height(20.dp))

        // --- PESTAÑAS PRINCIPALES ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), // Superficie dinámica
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(vertical = 12.dp, horizontal = 4.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TabItemCompact(
                    icon = Icons.Default.Home,
                    label = "Estado de\nHabitaciones",
                    isActive = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                TabItemCompact(
                    icon = Icons.Default.Assignment,
                    label = "Reservas/\nVentas Activas",
                    isActive = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                TabItemCompact(
                    icon = Icons.Default.History,
                    label = "Historial de\nClientes",
                    isActive = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- CONTENIDO CAMBIANTE ---
        when (selectedTab) {
            0 -> RoomStatusView(navController, uiState.allRooms, uiState.isLoading, uiState.calendarEvents, uiState.calendarNotes, uiState.isSavingNote, viewModel)
            1 -> ReservasListView(navController, uiState.reservations, uiState.isLoading, viewModel, uiState.calendarEvents, uiState.calendarNotes, uiState.isSavingNote)
            2 -> AccountStatusView(navController, uiState.reservations, uiState.isLoading)
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

// ==========================================================
// VISTA 1: ESTADO DE HABITACIONES
// ==========================================================
@Composable
fun RoomStatusView(
    navController: NavHostController,
    apiRooms: List<Room>,
    isLoading: Boolean,
    calendarEvents: List<CalendarEvent> = emptyList(),
    calendarNotes: Map<String, String> = emptyMap(),
    isSavingNote: Boolean = false,
    viewModel: ReservationViewModel? = null
) {
    // Estado para el piso seleccionado: 1, 2 o 3
    var selectedFloor by remember { mutableIntStateOf(1) }
    val isDark = isReservaDarkTheme()

    // Convertir habitaciones de la API a RoomItem con colores
    val allRooms = apiRooms.map { room ->
        val statusColor = when (room.status?.lowercase()) {
            "disponible" -> StatusGreen
            "ocupada" -> StatusBlue
            "bloqueada" -> StatusGrey
            "reservada" -> StatusPurple
            else -> StatusGreen
        }
        RoomItem(
            number = room.code ?: "",
            type = room.type ?: "-",
            status = room.status ?: "Disponible",
            statusColor = statusColor
        )
    }

    val filteredRooms = allRooms.filter { room ->
        room.number.startsWith(selectedFloor.toString().take(1))
    }
    
    // Calcular conteos por estado
    val disponibles = allRooms.count { it.status.lowercase() == "disponible" }
    val ocupadas = allRooms.count { it.status.lowercase() == "ocupada" }
    val reservadas = allRooms.count { it.status.lowercase() == "reservada" }
    val bloqueadas = allRooms.count { it.status.lowercase() == "bloqueada" }

    Column {

        // --- SECCIÓN DE RESUMEN (Adaptada a Dark Mode) ---
        // Definimos los colores base
        val greenIcon = Color(0xFF2E7D32)
        val blueIcon = Color(0xFF1565C0)
        val purpleIcon = Color(0xFF7B1FA2)
        val greyIcon = if(isDark) Color(0xFFAAAAAA) else Color(0xFF616161)

        // Fondos: En modo claro usamos pastel, en oscuro usamos el color base con transparencia baja
        val greenBg = if (isDark) greenIcon.copy(alpha = 0.2f) else Color(0xFFE8F5E9)
        val blueBg = if (isDark) blueIcon.copy(alpha = 0.2f) else Color(0xFFE3F2FD)
        val purpleBg = if (isDark) purpleIcon.copy(alpha = 0.2f) else Color(0xFFF3E5F5)
        val greyBg = if (isDark) Color(0xFF333333) else Color(0xFFF5F5F5)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SummaryCard(Modifier.weight(1f), Icons.Outlined.CheckCircle, "Disponibles", "$disponibles Hab.", greenBg, greenIcon)
            SummaryCard(Modifier.weight(1f), Icons.Default.Group, "Ocupadas", "$ocupadas Hab.", blueBg, blueIcon)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SummaryCard(Modifier.weight(1f), Icons.Default.Assignment, "Reservadas", "$reservadas Hab.", purpleBg, purpleIcon)
            SummaryCard(Modifier.weight(1f), Icons.Default.Settings, "Mantenimiento", "$bloqueadas Hab.", greyBg, greyIcon)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- CABECERA ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Habitaciones",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Listado actual de ocupación",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha=0.6f),
                    lineHeight = 14.sp
                )
            }

            FloorSelector(
                selectedFloor = selectedFloor,
                onFloorSelected = { selectedFloor = it }
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Botón Reporte de Desayunos
            Surface(
                onClick = { navController.navigate("comanda_screen") },
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.height(40.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.FreeBreakfast,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Reporte de\nDesayunos",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- TABLA DE HABITACIONES ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("Habitación", modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurface.copy(alpha=0.6f), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("Tipo", modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurface.copy(alpha=0.6f), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("Estado", modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurface.copy(alpha=0.6f), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                if (filteredRooms.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center){
                        Text("No hay habitaciones en este piso", color = MaterialTheme.colorScheme.onSurface.copy(alpha=0.6f))
                    }
                } else {
                    filteredRooms.forEach { room ->
                        RoomRowSimple(item = room)
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha=0.5f))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Calendario
        DynamicCalendarSection(
            calendarEvents = calendarEvents,
            calendarNotes = calendarNotes,
            isSavingNote = isSavingNote,
            onLoadEvents = { viewModel?.loadCalendarEvents() },
            onLoadNotes = { viewModel?.loadCalendarNotes() },
            onSaveNote = { date, text -> viewModel?.saveCalendarNote(date, text) },
            onDeleteNote = { date -> viewModel?.deleteCalendarNote(date) }
        )
    }
}

// ==========================================================
// VISTA 2: RESERVAS / VENTAS ACTIVAS
// ==========================================================
@Composable
fun ReservasListView(
    navController: NavHostController,
    reservations: List<Reservation>,
    isLoading: Boolean,
    viewModel: ReservationViewModel,
    calendarEvents: List<CalendarEvent> = emptyList(),
    calendarNotes: Map<String, String> = emptyMap(),
    isSavingNote: Boolean = false
) {
    val isDark = isReservaDarkTheme()
    var searchText by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var reservationToDelete by remember { mutableStateOf<Reservation?>(null) }
    
    // Estados para modales de Ver y Editar
    var showViewDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedReservation by remember { mutableStateOf<Reservation?>(null) }
    
    // Filtrar solo reservas activas (no canceladas ni check-out)
    val activeReservations = reservations.filter { 
        val status = it.status?.lowercase() ?: ""
        status != "cancelada" && status != "check-out"
    }.filter {
        if (searchText.isBlank()) true
        else {
            val guest = it.guest?.lowercase() ?: ""
            val doc = it.documentNumber?.lowercase() ?: ""
            guest.contains(searchText.lowercase()) || doc.contains(searchText.lowercase())
        }
    }
    
    // Dialog de confirmación de eliminación
    if (showDeleteDialog && reservationToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de eliminar la reserva de ${reservationToDelete?.guest ?: "este huésped"}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        reservationToDelete?.reservationId?.let { viewModel.deleteReservation(it) }
                        showDeleteDialog = false
                        reservationToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = StatusRed)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
    
    // Dialog de Ver Reserva
    if (showViewDialog && selectedReservation != null) {
        ViewReservationDialog(
            reservation = selectedReservation!!,
            onDismiss = { 
                showViewDialog = false
                selectedReservation = null
            }
        )
    }
    
    // Dialog de Editar Reserva
    if (showEditDialog && selectedReservation != null) {
        EditReservationDialog(
            reservation = selectedReservation!!,
            viewModel = viewModel,
            onDismiss = { 
                showEditDialog = false
                selectedReservation = null
            },
            onSave = { request ->
                selectedReservation?.reservationId?.let { id ->
                    viewModel.updateReservation(id, request)
                }
                showEditDialog = false
                selectedReservation = null
            }
        )
    }
    
    Column {
        // Buscador
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Buscar reserva",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = { Text("Nombre de huésped o documento", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedBorderColor = OrangePrimary,
                        cursorColor = OrangePrimary,
                        focusedContainerColor = if(isDark) Color(0xFF2D2D2D) else Color.White,
                        unfocusedContainerColor = if(isDark) Color(0xFF2D2D2D) else Color.White,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Tabla de reservas
        if (isLoading) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 50.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = OrangePrimary)
                }
            }
        } else if (activeReservations.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 50.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Outlined.Description,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No hay reservas activas",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        if (searchText.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Intenta con otro término de búsqueda",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Título
                    Text(
                        "Reservas/Ventas Activas",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "${activeReservations.size} reserva${if (activeReservations.size != 1) "s" else ""} activa${if (activeReservations.size != 1) "s" else ""}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    
                    activeReservations.forEach { reservation ->
                        ReservationRow(
                            reservation = reservation,
                            isDark = isDark,
                            onView = {
                                selectedReservation = reservation
                                showViewDialog = true
                            },
                            onEdit = {
                                selectedReservation = reservation
                                showEditDialog = true
                            },
                            onDelete = {
                                reservationToDelete = reservation
                                showDeleteDialog = true
                            }
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        DynamicCalendarSection(
            calendarEvents = calendarEvents,
            calendarNotes = calendarNotes,
            isSavingNote = isSavingNote,
            onLoadEvents = { viewModel.loadCalendarEvents() },
            onLoadNotes = { viewModel.loadCalendarNotes() },
            onSaveNote = { date, text -> viewModel.saveCalendarNote(date, text) },
            onDeleteNote = { date -> viewModel.deleteCalendarNote(date) }
        )
    }
}

@Composable
fun ReservationRow(
    reservation: Reservation, 
    isDark: Boolean,
    onView: () -> Unit = {},
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    val statusColor = when (reservation.status?.lowercase()) {
        "confirmada" -> StatusGreen
        "check-in" -> OrangePrimary
        "check-out" -> StatusGrey
        "cancelada" -> StatusRed
        "reservada" -> StatusPurple
        else -> StatusGreen
    }
    
    // Color del canal (similar a React)
    val channelColor = when (reservation.channel?.lowercase()) {
        "booking" -> StatusPurple
        "whatsapp" -> StatusGreen
        "directsale", "venta directa" -> OrangePrimary
        else -> StatusGrey
    }
    
    val paidColor = if (reservation.paid == true) StatusGreen else StatusRed
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        // Primera fila: Canal, Huésped, Habitación
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Canal Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(channelColor.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = reservation.channel ?: "-",
                    color = channelColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Huésped
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reservation.guest ?: "-",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
                Text(
                    text = "Hab. ${reservation.room ?: "-"}",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Segunda fila: Fechas y Total
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Check-in",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    fontSize = 10.sp
                )
                Text(
                    text = reservation.checkIn ?: "-",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 12.sp
                )
            }
            Column {
                Text(
                    text = "Check-out",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    fontSize = 10.sp
                )
                Text(
                    text = reservation.checkOut ?: "-",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 12.sp
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Total",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    fontSize = 10.sp
                )
                Text(
                    text = reservation.total ?: "-",
                    color = OrangePrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Spacer(modifier = Modifier.height(10.dp))
        
        // Tercera fila: Estado, Pagado, Acciones
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Estado Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = reservation.status ?: "-",
                        color = statusColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Pagado Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(paidColor.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (reservation.paid == true) "Pagado" else "Pendiente",
                        color = paidColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // Botones de acción
            Row {
                // Ver
                IconButton(
                    onClick = onView,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Filled.Visibility,
                        contentDescription = "Ver",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                }
                
                // Editar
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Filled.Edit,
                        contentDescription = "Editar",
                        tint = OrangePrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                
                // Eliminar
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Eliminar",
                        tint = StatusRed,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

// ==========================================================
// VISTA 3: HISTORIAL DE CLIENTES
// ==========================================================
@Composable
fun AccountStatusView(
    navController: NavHostController,
    reservations: List<Reservation>,
    isLoading: Boolean
) {
    val isDark = isReservaDarkTheme()
    var searchText by remember { mutableStateOf("") }
    
    // Filtrar reservas con estado Check-out
    val checkoutReservations = reservations.filter { 
        it.status?.lowercase() == "check-out"
    }.filter {
        if (searchText.isBlank()) true
        else {
            val guest = it.guest?.lowercase() ?: ""
            val doc = it.documentNumber?.lowercase() ?: ""
            guest.contains(searchText.lowercase()) || doc.contains(searchText.lowercase())
        }
    }
    
    Column {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                Text("Buscar", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(8.dp))

                // Campo de texto con colores adaptados
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = { Text("Nombre o DNI", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedBorderColor = OrangePrimary,
                        cursorColor = OrangePrimary,
                        focusedContainerColor = if(isDark) Color(0xFF2D2D2D) else Color.White,
                        unfocusedContainerColor = if(isDark) Color(0xFF2D2D2D) else Color.White,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text("Historial de Clientes", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text("Clientes con estado Check-out", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha=0.6f))

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(10.dp))

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = OrangePrimary)
                    }
                } else if (checkoutReservations.isEmpty()) {
                    Spacer(modifier = Modifier.height(40.dp))
                    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Outlined.Description, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha=0.4f), modifier = Modifier.size(60.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No hay registros disponibles", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha=0.6f), fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("No se encontraron clientes con estado Check-out", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                } else {
                    // Mostrar lista de clientes con check-out
                    checkoutReservations.forEach { reservation ->
                        HistoryClientRow(reservation)
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DynamicCalendarSection(
            calendarEvents = emptyList(),
            calendarNotes = emptyMap(),
            isSavingNote = false,
            onLoadEvents = { },
            onLoadNotes = { },
            onSaveNote = { _, _ -> },
            onDeleteNote = { }
        )
    }
}

@Composable
fun HistoryClientRow(reservation: Reservation) {
    // Color del canal (similar a React)
    val channelColor = when (reservation.channel?.lowercase()) {
        "booking" -> StatusPurple
        "whatsapp" -> StatusGreen
        "directsale", "venta directa" -> OrangePrimary
        else -> StatusGrey
    }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        // Primera fila: Cliente y Canal
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Canal Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(channelColor.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = reservation.channel ?: "-",
                    color = channelColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Datos del cliente
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reservation.guest ?: "-",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
                Text(
                    text = "${reservation.documentType ?: "DNI"}: ${reservation.documentNumber ?: "-"}",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Segunda fila: Habitación, Tipo, Check-out, Total
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Habitación",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    fontSize = 10.sp
                )
                Text(
                    text = reservation.room ?: "-",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 12.sp
                )
            }
            Column {
                Text(
                    text = "Tipo",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    fontSize = 10.sp
                )
                Text(
                    text = reservation.roomType ?: "-",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 12.sp
                )
            }
            Column {
                Text(
                    text = "Check-out",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    fontSize = 10.sp
                )
                Text(
                    text = reservation.checkOut ?: "-",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 12.sp
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Total",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    fontSize = 10.sp
                )
                Text(
                    text = reservation.total ?: "-",
                    color = OrangePrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ==========================================================
// COMPONENTE CALENDARIO
// ==========================================================
@Composable
fun DynamicCalendarSection(
    calendarEvents: List<CalendarEvent> = emptyList(),
    calendarNotes: Map<String, String> = emptyMap(),
    isSavingNote: Boolean = false,
    onLoadEvents: () -> Unit = {},
    onLoadNotes: () -> Unit = {},
    onSaveNote: (String, String) -> Unit = { _, _ -> },
    onDeleteNote: (String) -> Unit = {}
) {
    val currentCalendar = remember { Calendar.getInstance() }
    val isDark = isReservaDarkTheme()

    var displayMonth by remember { mutableIntStateOf(currentCalendar.get(Calendar.MONTH)) }
    var displayYear by remember { mutableIntStateOf(currentCalendar.get(Calendar.YEAR)) }

    var isMonthView by remember { mutableStateOf(true) }
    var showNoteDialog by remember { mutableStateOf(false) }
    var selectedDateString by remember { mutableStateOf("") }
    var noteText by remember { mutableStateOf("") }

    // Cargar eventos y notas al iniciar
    LaunchedEffect(Unit) {
        onLoadEvents()
        onLoadNotes()
    }

    fun updateDate(monthOffset: Int) {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, displayYear)
        cal.set(Calendar.MONTH, displayMonth)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.add(Calendar.MONTH, monthOffset)

        displayMonth = cal.get(Calendar.MONTH)
        displayYear = cal.get(Calendar.YEAR)
    }

    fun setToday() {
        val cal = Calendar.getInstance()
        displayMonth = cal.get(Calendar.MONTH)
        displayYear = cal.get(Calendar.YEAR)
    }

    // Actualizar el texto de la nota cuando se selecciona una fecha
    LaunchedEffect(selectedDateString, showNoteDialog) {
        if (showNoteDialog) {
            noteText = calendarNotes[selectedDateString] ?: ""
        }
    }

    if (showNoteDialog) {
        NoteDialog(
            date = selectedDateString,
            initialText = noteText,
            isSaving = isSavingNote,
            onDismiss = { showNoteDialog = false },
            onSave = { text ->
                onSaveNote(selectedDateString, text)
                showNoteDialog = false
            },
            onDelete = {
                onDeleteNote(selectedDateString)
                showNoteDialog = false
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            Text(
                text = "Calendario de Reservas",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Visualiza todas las reservas por canal de reserva",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha=0.6f)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Controles Superiores
            val borderColor = MaterialTheme.colorScheme.outlineVariant
            val textColor = MaterialTheme.colorScheme.onSurface

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                OutlinedButton(
                    onClick = { setToday() },
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, borderColor),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                    modifier = Modifier.height(32.dp)
                ) { Text("Hoy", color = textColor.copy(alpha=0.7f), fontSize = 12.sp) }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedButton(
                    onClick = { updateDate(-1) },
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, borderColor),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                    modifier = Modifier.height(32.dp)
                ) { Text("Anterior", color = textColor, fontSize = 12.sp) }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedButton(
                    onClick = { updateDate(1) },
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, borderColor),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                    modifier = Modifier.height(32.dp)
                ) { Text("Siguiente", color = textColor, fontSize = 12.sp) }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Título Mes/Año
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                val cal = Calendar.getInstance()
                cal.set(Calendar.MONTH, displayMonth)
                cal.set(Calendar.YEAR, displayYear)
                val monthName = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale("es", "ES")) ?: ""

                Text(
                    text = "$monthName de $displayYear".replaceFirstChar { it.uppercase() },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Toggle Mes/Agenda
            val activeBg = if(isDark) Color(0xFF424242) else Color(0xFFE0E0E0)
            val inactiveBg = if(isDark) Color(0xFF1E1E1E) else Color.White

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Row(
                    modifier = Modifier
                        .border(1.dp, borderColor, RoundedCornerShape(4.dp))
                        .height(32.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(if (isMonthView) activeBg else inactiveBg)
                            .padding(horizontal = 16.dp)
                            .fillMaxHeight()
                            .clickable { isMonthView = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Mes", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = if(isMonthView) FontWeight.Bold else FontWeight.Normal)
                    }
                    VerticalDivider(color = borderColor)
                    Box(
                        modifier = Modifier
                            .background(if (!isMonthView) activeBg else inactiveBg)
                            .padding(horizontal = 16.dp)
                            .fillMaxHeight()
                            .clickable { isMonthView = false },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Agenda", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = if(!isMonthView) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (isMonthView) {
                CalendarGridLegacy(
                    month = displayMonth,
                    year = displayYear,
                    events = calendarEvents,
                    notes = calendarNotes,
                    onDayClick = { clickedDayItem ->
                        selectedDateString = clickedDayItem.fullDate
                        showNoteDialog = true
                    }
                )
            } else {
                // Vista Agenda - mostrar eventos que tienen algún día en el mes actual
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val currentMonthEvents = calendarEvents.filter { event ->
                    try {
                        val startStr = event.start?.substring(0, 10) ?: return@filter false
                        val endStr = event.end?.substring(0, 10) ?: startStr
                        
                        val eventStart = dateFormat.parse(startStr) ?: return@filter false
                        val eventEnd = dateFormat.parse(endStr) ?: eventStart
                        
                        // Crear fechas del primer y último día del mes actual
                        val cal = Calendar.getInstance()
                        cal.set(Calendar.YEAR, displayYear)
                        cal.set(Calendar.MONTH, displayMonth)
                        cal.set(Calendar.DAY_OF_MONTH, 1)
                        val monthStart = cal.time
                        
                        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
                        val monthEnd = cal.time
                        
                        // El evento está en el mes si hay alguna intersección
                        !eventEnd.before(monthStart) && !eventStart.after(monthEnd)
                    } catch (e: Exception) {
                        false
                    }
                }
                
                if (currentMonthEvents.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay eventos en este rango.",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha=0.6f),
                            fontSize = 14.sp
                        )
                    }
                } else {
                    Column {
                        currentMonthEvents.forEach { event ->
                            AgendaEventItem(event = event)
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        }
                    }
                }
            }
        }
    }
}

// Componente para mostrar un evento en la vista Agenda
@Composable
fun AgendaEventItem(event: CalendarEvent) {
    val calendarType = event.extendedProps?.calendar ?: "DirectSale"
    val eventColor = when (calendarType) {
        "Booking" -> Color(0xFF22C55E)      // Verde
        "WhatsApp" -> Color(0xFF25D366)     // Verde WhatsApp
        "DirectSale" -> Color(0xFFFB6514)   // Naranja
        else -> Color(0xFF6B7280)           // Gris
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Indicador de color
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(40.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(eventColor)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = event.title ?: "-",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${event.start ?: "-"} → ${event.end ?: "-"}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        
        // Badge del canal
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(eventColor.copy(alpha = 0.15f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = calendarType,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = eventColor
            )
        }
    }
}

// Ventana Emergente "Nota del día"
@Composable
fun NoteDialog(
    date: String,
    initialText: String = "",
    isSaving: Boolean = false,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
    onDelete: () -> Unit
) {
    val isDark = isReservaDarkTheme()
    var text by remember(initialText) { mutableStateOf(initialText) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(text = "Nota del día", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = date, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha=0.6f))
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Outlined.Close, contentDescription = "Cerrar", tint = MaterialTheme.colorScheme.onSurface)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Fondo para input en dark mode
                val bgInput = if(isDark) Color(0xFF2D2D2D) else Color.Transparent

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(bgInput, RoundedCornerShape(8.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    if (text.isEmpty()) {
                        Text("Escribe una nota breve", color = MaterialTheme.colorScheme.onSurface.copy(alpha=0.4f), fontSize = 14.sp)
                    }
                    BasicTextField(
                        value = text,
                        onValueChange = { text = it },
                        textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp),
                        modifier = Modifier.fillMaxSize(),
                        enabled = !isSaving
                    )
                    Icon(
                        imageVector = Icons.Default.DragHandle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha=0.4f),
                        modifier = Modifier.align(Alignment.BottomEnd).size(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { onSave(text) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4C6EF5)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(40.dp),
                        enabled = !isSaving && text.isNotBlank()
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Guardar", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    OutlinedButton(
                        onClick = onDelete,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.height(40.dp),
                        enabled = !isSaving && initialText.isNotBlank()
                    ) {
                        Text("Eliminar", color = if (initialText.isNotBlank()) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                    }
                }
            }
        }
    }
}

// Lógica de grilla usando java.util.Calendar
@Composable
fun CalendarGridLegacy(
    month: Int,
    year: Int,
    events: List<CalendarEvent> = emptyList(),
    notes: Map<String, String> = emptyMap(),
    onDayClick: (CalendarDayItem) -> Unit
) {
    val isDark = isReservaDarkTheme()
    val daysOfWeek = listOf("Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb")

    // Agrupar eventos por fecha - cada evento abarca desde start hasta end
    val eventsByDate = remember(events) {
        val map = mutableMapOf<String, MutableList<CalendarEvent>>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        
        events.forEach { event ->
            try {
                val startStr = event.start?.substring(0, 10) ?: return@forEach
                val endStr = event.end?.substring(0, 10) ?: startStr
                
                val startDate = dateFormat.parse(startStr) ?: return@forEach
                val endDate = dateFormat.parse(endStr) ?: startDate
                
                val calendar = Calendar.getInstance()
                calendar.time = startDate
                
                // Agregar el evento a cada día del rango
                while (!calendar.time.after(endDate)) {
                    val dateKey = dateFormat.format(calendar.time)
                    if (map[dateKey] == null) {
                        map[dateKey] = mutableListOf()
                    }
                    map[dateKey]?.add(event)
                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                }
            } catch (e: Exception) {
                // Ignorar eventos con fechas inválidas
            }
        }
        map
    }

    val calendarItems = remember(month, year) {
        val items = mutableListOf<CalendarDayItem>()
        val cal = Calendar.getInstance()
        val todayCal = Calendar.getInstance()

        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month)
        cal.set(Calendar.DAY_OF_MONTH, 1)

        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK)

        val offset = firstDayOfWeek - 1

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        val prevCal = cal.clone() as Calendar
        prevCal.add(Calendar.MONTH, -1)
        val daysInPrevMonth = prevCal.getActualMaximum(Calendar.DAY_OF_MONTH)

        for (i in 0 until offset) {
            val dayNum = daysInPrevMonth - offset + 1 + i
            prevCal.set(Calendar.DAY_OF_MONTH, dayNum)
            items.add(CalendarDayItem(dayNum.toString(), dateFormat.format(prevCal.time), false, false))
        }

        val currentMonthCal = cal.clone() as Calendar
        for (i in 1..daysInMonth) {
            currentMonthCal.set(Calendar.DAY_OF_MONTH, i)
            val isToday = (todayCal.get(Calendar.YEAR) == year &&
                    todayCal.get(Calendar.MONTH) == month &&
                    todayCal.get(Calendar.DAY_OF_MONTH) == i)

            items.add(CalendarDayItem(i.toString(), dateFormat.format(currentMonthCal.time), true, isToday))
        }

        val totalCells = 42
        val remaining = totalCells - items.size
        val nextCal = cal.clone() as Calendar
        nextCal.add(Calendar.MONTH, 1)

        for (i in 1..remaining) {
            nextCal.set(Calendar.DAY_OF_MONTH, i)
            items.add(CalendarDayItem(String.format("%02d", i), dateFormat.format(nextCal.time), false, false))
        }

        items
    }

    Column {
        Row(modifier = Modifier.fillMaxWidth()) {
            daysOfWeek.forEach { day ->
                Text(text = day, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        // Colores de celdas
        val todayBg = Color(0xFF2C3E50) // Azul oscuro siempre
        val otherMonthBg = if(isDark) Color(0xFF2A2A2A) else Color(0xFFF2F2F2)
        val borderColor = if(isDark) Color(0xFF444444) else Color(0xFFF5F5F5)

        val weeks = calendarItems.chunked(7)
        weeks.forEach { week ->
            Row(modifier = Modifier.fillMaxWidth().height(64.dp)) {
                week.forEach { dayItem ->
                    val dayEvents = eventsByDate[dayItem.fullDate] ?: emptyList()
                    val dayNote = notes[dayItem.fullDate]
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .border(0.5.dp, borderColor)
                            .clickable {
                                onDayClick(dayItem)
                            }
                    ) {
                        // Fondo del día
                        if (dayItem.isToday) {
                            Box(modifier = Modifier.fillMaxSize().background(todayBg))
                        } else if (!dayItem.isCurrentMonth) {
                            Box(modifier = Modifier.fillMaxSize().background(otherMonthBg))
                        }
                        
                        Column(
                            modifier = Modifier.fillMaxSize().padding(2.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Número del día
                            Text(
                                text = dayItem.dayString,
                                color = when {
                                    dayItem.isToday -> Color.White
                                    dayItem.isCurrentMonth -> MaterialTheme.colorScheme.onSurface
                                    else -> MaterialTheme.colorScheme.onSurface.copy(alpha=0.5f)
                                },
                                fontSize = 11.sp,
                                fontWeight = if(dayItem.isToday) FontWeight.Bold else FontWeight.Normal
                            )
                            
                            // Nota del día (si existe)
                            if (!dayNote.isNullOrBlank()) {
                                Text(
                                    text = dayNote,
                                    fontSize = 7.sp,
                                    color = if (dayItem.isToday) Color.White.copy(alpha = 0.8f) 
                                           else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    maxLines = 1,
                                    modifier = Modifier.padding(horizontal = 1.dp)
                                )
                            }
                            
                            // Indicadores de eventos (máximo 2)
                            if (dayEvents.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(1.dp))
                                dayEvents.take(2).forEach { event ->
                                    val eventColor = when (event.extendedProps?.calendar) {
                                        "Booking" -> Color(0xFF22C55E)      // Verde
                                        "WhatsApp" -> Color(0xFF25D366)     // Verde WhatsApp
                                        "DirectSale" -> Color(0xFFFB6514)   // Naranja
                                        else -> Color(0xFF6B7280)           // Gris
                                    }
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(3.dp)
                                            .padding(horizontal = 1.dp)
                                            .clip(RoundedCornerShape(1.dp))
                                            .background(eventColor)
                                    )
                                    Spacer(modifier = Modifier.height(1.dp))
                                }
                                // Indicador de más eventos
                                if (dayEvents.size > 2) {
                                    Text(
                                        text = "+${dayEvents.size - 2}",
                                        fontSize = 7.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// UI Kits auxiliares
@Composable
fun SummaryCard(modifier: Modifier = Modifier, icon: ImageVector, label: String, count: String, bg: Color, tint: Color) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha=0.5f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.Start) {
            Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)).background(bg), contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = label, color = MaterialTheme.colorScheme.onSurface.copy(alpha=0.6f), fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = count, color = MaterialTheme.colorScheme.onSurface, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun FloorSelector(selectedFloor: Int, onFloorSelected: (Int) -> Unit) {
    val isDark = isReservaDarkTheme()
    val borderColor = MaterialTheme.colorScheme.outlineVariant
    val containerColor = if(isDark) Color(0xFF2D2D2D) else Color.White

    Row(
        modifier = Modifier.height(40.dp).border(1.dp, borderColor, RoundedCornerShape(8.dp)).clip(RoundedCornerShape(8.dp)).background(containerColor)
    ) {
        FloorOption("1ro", selectedFloor == 1) { onFloorSelected(1) }
        VerticalDivider(modifier = Modifier.width(1.dp).fillMaxHeight(), color = borderColor)
        FloorOption("2do", selectedFloor == 2) { onFloorSelected(2) }
        VerticalDivider(modifier = Modifier.width(1.dp).fillMaxHeight(), color = borderColor)
        FloorOption("3ro", selectedFloor == 3) { onFloorSelected(3) }
    }
}

@Composable
fun FloorOption(label: String, isSelected: Boolean, onClick: () -> Unit) {
    val bgColor = if (isSelected) OrangePrimary else Color.Transparent
    val textColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface

    Box(
        modifier = Modifier.fillMaxHeight().width(45.dp).background(bgColor).clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, color = textColor, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, fontSize = 13.sp)
    }
}

@Composable
fun TabItemCompact(icon: ImageVector, label: String, isActive: Boolean, onClick: () -> Unit) {
    val contentColor = if (isActive) OrangePrimary else MaterialTheme.colorScheme.onSurface.copy(alpha=0.6f)

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }.padding(horizontal = 4.dp).widthIn(min = 70.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, color = contentColor, fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal, fontSize = 11.sp, textAlign = TextAlign.Center, lineHeight = 12.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Box(modifier = Modifier.width(40.dp).height(3.dp).background(if (isActive) OrangePrimary else Color.Transparent, RoundedCornerShape(2.dp)))
    }
}

@Composable
fun RoomRowSimple(item: RoomItem) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(text = item.number, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp, modifier = Modifier.weight(1f))
        Text(text = item.type, color = MaterialTheme.colorScheme.onSurface.copy(alpha=0.6f), fontSize = 14.sp, modifier = Modifier.weight(1f))
        Box(modifier = Modifier.weight(1f)) {
            Box(modifier = Modifier.clip(RoundedCornerShape(50)).background(item.statusColor.copy(alpha = 0.1f)).padding(horizontal = 12.dp, vertical = 6.dp)) {
                Text(text = item.status, color = item.statusColor, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun ReservaRow(item: ReservaItem) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(0.7f)) {
            Text(text = item.id.split("-")[0], color = MaterialTheme.colorScheme.onSurface.copy(alpha=0.6f), fontSize = 11.sp)
            Text(text = item.id.split("-")[1], color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Text(text = item.nombre, color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1.3f), lineHeight = 16.sp)
        Column(modifier = Modifier.weight(0.7f)) {
            Text(text = item.tipoHab, color = MaterialTheme.colorScheme.onSurface.copy(alpha=0.6f), fontSize = 11.sp)
            Text(text = item.habitacion, color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp)
        }
        Text(text = item.precio, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(0.9f))
        Row(modifier = Modifier.weight(1.2f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(item.estadoColor.copy(alpha = 0.15f)).padding(horizontal = 6.dp, vertical = 4.dp)) {
                Text(text = item.estado, color = item.estadoColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar", tint = OrangePrimary, modifier = Modifier.size(16.dp).clickable { })
        }
    }
}

// Función auxiliar privada para evitar conflicto de nombres con otros archivos
@Composable
private fun isReservaDarkTheme(): Boolean {
    return MaterialTheme.colorScheme.surface.luminance() < 0.5f
}

// ==========================================================
// DIALOG: VER DETALLES DE RESERVA
// ==========================================================
@Composable
fun ViewReservationDialog(
    reservation: Reservation,
    onDismiss: () -> Unit
) {
    val isDark = isReservaDarkTheme()
    
    val statusColor = when (reservation.status?.lowercase()) {
        "confirmada" -> StatusGreen
        "check-in" -> OrangePrimary
        "check-out" -> StatusGrey
        "cancelada" -> StatusRed
        "reservada" -> StatusPurple
        else -> StatusGreen
    }
    
    val channelColor = when (reservation.channel?.lowercase()) {
        "booking" -> StatusPurple
        "whatsapp" -> StatusGreen
        "directsale", "venta directa" -> OrangePrimary
        else -> StatusGrey
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Título
                Text(
                    "Detalles de Reserva/Venta",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Canal y Estado
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Canal", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(channelColor.copy(alpha = 0.15f))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                reservation.channel ?: "-",
                                color = channelColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Estado", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(statusColor.copy(alpha = 0.15f))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                reservation.status ?: "-",
                                color = statusColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(16.dp))
                
                // Huésped
                DetailRow("Huésped", reservation.guest ?: "-")
                
                // Documento
                DetailRow(
                    "Documento",
                    "${reservation.documentType ?: "DNI"}: ${reservation.documentNumber ?: "-"}"
                )
                
                // Habitaciones
                val roomsText = if (reservation.rooms?.isNotEmpty() == true) {
                    reservation.rooms.joinToString(", ")
                } else {
                    reservation.room ?: "-"
                }
                DetailRow("Habitación(es)", roomsText)
                
                // Tipo de habitación
                DetailRow("Tipo de Habitación", reservation.roomType ?: "-")
                
                // Fechas
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Check-in", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(reservation.checkIn ?: "-", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Check-out", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(reservation.checkOut ?: "-", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Horas
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Hora de llegada", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(reservation.arrivalTime ?: "-", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Hora de salida", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(reservation.departureTime ?: "-", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Personas
                DetailRow(
                    "Personas",
                    "${reservation.numAdults ?: 0} Adultos, ${reservation.numChildren ?: 0} Niños"
                )
                
                // Total
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Total", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        reservation.total ?: "-",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = OrangePrimary
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Pagado
                val paidColor = if (reservation.paid == true) StatusGreen else StatusRed
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Pagado: ", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(paidColor.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            if (reservation.paid == true) "Sí" else "No",
                            color = paidColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(16.dp))
                
                // Dirección
                DetailRow("Dirección", reservation.address ?: "-")
                
                // Ubicación
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Departamento", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(reservation.department ?: "-", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Provincia", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(reservation.province ?: "-", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                DetailRow("Distrito", reservation.district ?: "-")
                
                // Acompañantes
                if (reservation.companions?.isNotEmpty() == true) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Acompañantes", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.height(4.dp))
                    reservation.companions.forEach { companion ->
                        Text(
                            "• ${companion.name ?: "-"}",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Botón Cerrar
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cerrar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
    }
}

// ==========================================================
// DIALOG: EDITAR RESERVA
// ==========================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditReservationDialog(
    reservation: Reservation,
    viewModel: ReservationViewModel,
    onDismiss: () -> Unit,
    onSave: (UpdateReservationRequest) -> Unit
) {
    val isDark = isReservaDarkTheme()
    val uiState by viewModel.uiState.collectAsState()
    
    // Estados editables
    var guest by remember { mutableStateOf(reservation.guest ?: "") }
    var room by remember { mutableStateOf(reservation.room ?: "") }
    var roomType by remember { mutableStateOf(reservation.roomType ?: "") }
    var checkIn by remember { mutableStateOf(reservation.checkIn ?: "") }
    var checkOut by remember { mutableStateOf(reservation.checkOut ?: "") }
    var arrivalTime by remember { mutableStateOf(reservation.arrivalTime ?: "") }
    var departureTime by remember { mutableStateOf(reservation.departureTime ?: "") }
    var total by remember { mutableStateOf(reservation.total ?: "") }
    var paid by remember { mutableStateOf(reservation.paid ?: false) }
    var channel by remember { mutableStateOf(reservation.channel ?: "") }
    
    // Dropdown states
    var expandedRoomType by remember { mutableStateOf(false) }
    var expandedChannel by remember { mutableStateOf(false) }
    
    val roomTypes = listOf("Simple", "Doble", "Triple", "Matrimonial")
    val channels = listOf("Booking", "WhatsApp", "DirectSale")
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Título
                Text(
                    "Editar Reserva/Venta",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "Actualiza la información de la reserva",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Huésped
                Text("Huésped", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = guest,
                    onValueChange = { guest = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangePrimary,
                        cursorColor = OrangePrimary
                    ),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Habitación y Tipo
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Habitación", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = room,
                            onValueChange = { room = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = OrangePrimary,
                                cursorColor = OrangePrimary
                            ),
                            singleLine = true
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Tipo", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(4.dp))
                        ExposedDropdownMenuBox(
                            expanded = expandedRoomType,
                            onExpandedChange = { expandedRoomType = !expandedRoomType }
                        ) {
                            OutlinedTextField(
                                value = roomType,
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                shape = RoundedCornerShape(8.dp),
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRoomType) },
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = OrangePrimary)
                            )
                            ExposedDropdownMenu(
                                expanded = expandedRoomType,
                                onDismissRequest = { expandedRoomType = false }
                            ) {
                                roomTypes.forEach { type ->
                                    DropdownMenuItem(
                                        text = { Text(type) },
                                        onClick = {
                                            roomType = type
                                            expandedRoomType = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Check-in y Check-out
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Check-in", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = checkIn,
                            onValueChange = { checkIn = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            placeholder = { Text("YYYY-MM-DD", fontSize = 12.sp) },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = OrangePrimary, cursorColor = OrangePrimary),
                            singleLine = true
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Check-out", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = checkOut,
                            onValueChange = { checkOut = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            placeholder = { Text("YYYY-MM-DD", fontSize = 12.sp) },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = OrangePrimary, cursorColor = OrangePrimary),
                            singleLine = true
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Horas
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Hora llegada", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = arrivalTime,
                            onValueChange = { arrivalTime = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            placeholder = { Text("HH:MM", fontSize = 12.sp) },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = OrangePrimary, cursorColor = OrangePrimary),
                            singleLine = true
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Hora salida", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = departureTime,
                            onValueChange = { departureTime = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            placeholder = { Text("HH:MM", fontSize = 12.sp) },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = OrangePrimary, cursorColor = OrangePrimary),
                            singleLine = true
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Total y Canal
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Total (S/)", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = total,
                            onValueChange = { total = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = OrangePrimary, cursorColor = OrangePrimary),
                            singleLine = true
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Canal", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(4.dp))
                        ExposedDropdownMenuBox(
                            expanded = expandedChannel,
                            onExpandedChange = { expandedChannel = !expandedChannel }
                        ) {
                            OutlinedTextField(
                                value = channel,
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                shape = RoundedCornerShape(8.dp),
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedChannel) },
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = OrangePrimary)
                            )
                            ExposedDropdownMenu(
                                expanded = expandedChannel,
                                onDismissRequest = { expandedChannel = false }
                            ) {
                                channels.forEach { ch ->
                                    DropdownMenuItem(
                                        text = { Text(ch) },
                                        onClick = {
                                            channel = ch
                                            expandedChannel = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Pagado checkbox
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = paid,
                        onCheckedChange = { paid = it },
                        colors = CheckboxDefaults.colors(checkedColor = OrangePrimary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Pagado", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Botones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = {
                            val request = UpdateReservationRequest(
                                guest = guest.ifBlank { null },
                                room = room.ifBlank { null },
                                roomType = roomType.ifBlank { null },
                                checkIn = checkIn.ifBlank { null },
                                checkOut = checkOut.ifBlank { null },
                                arrivalTime = arrivalTime.ifBlank { null },
                                departureTime = departureTime.ifBlank { null },
                                total = total.ifBlank { null },
                                paid = paid
                            )
                            onSave(request)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !uiState.isLoading
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Guardar", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}