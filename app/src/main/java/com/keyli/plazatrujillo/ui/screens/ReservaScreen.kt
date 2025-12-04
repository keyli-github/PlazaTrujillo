package com.keyli.plazatrujillo.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
    val fullDate: String,
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
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // --- CABECERA ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Gestión Hotelera",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Button(
                onClick = { navController.navigate("new_reservation") },
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
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // --- TABS ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                    Icons.Default.Home,
                    "Estado de\nHabitaciones",
                    selectedTab == 0
                ) { selectedTab = 0 }
                TabItemCompact(
                    Icons.Default.Assignment,
                    "Reservas/\nVentas Activas",
                    selectedTab == 1
                ) { selectedTab = 1 }
                TabItemCompact(
                    Icons.Default.History,
                    "Historial de\nClientes",
                    selectedTab == 2
                ) { selectedTab = 2 }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- CONTENIDO ---
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
        val greyIcon = if (isDark) Color(0xFFAAAAAA) else Color(0xFF616161)

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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Habitaciones",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "Listado actual de ocupación",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            FloorSelector(selectedFloor) { selectedFloor = it }
            Spacer(modifier = Modifier.width(12.dp))
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
                        Icons.Default.FreeBreakfast,
                        null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Reporte de\nDesayunos",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Habitación",
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Tipo",
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Estado",
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                if (filteredRooms.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No hay habitaciones en este piso",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                } else {
                    filteredRooms.forEach { room ->
                        RoomRowSimple(room)
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(
                                alpha = 0.5f
                            )
                        )
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
// VISTA 2: RESERVAS (CON SCROLL HORIZONTAL)
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
                Text(
                    "Buscar",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Campo de texto con colores adaptados
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = {
                        Text(
                            "Nombre o DNI",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangePrimary,
                        cursorColor = OrangePrimary,
                        focusedContainerColor = if (isDark) Color(0xFF2D2D2D) else Color.White,
                        unfocusedContainerColor = if (isDark) Color(0xFF2D2D2D) else Color.White
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "Historial de Clientes",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "Clientes con estado Check-out",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
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
// COMPONENTES AUXILIARES (HELPERS)
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

    // Estados del calendario
    var displayMonth by remember { mutableIntStateOf(currentCalendar.get(Calendar.MONTH)) }
    var displayYear by remember { mutableIntStateOf(currentCalendar.get(Calendar.YEAR)) }
    var isMonthView by remember { mutableStateOf(true) } // Estado del Toggle (Mes vs Agenda)

    var showNoteDialog by remember { mutableStateOf(false) }
    var selectedDateString by remember { mutableStateOf("") }
    var noteText by remember { mutableStateOf("") }

    // Cargar eventos y notas al iniciar
    LaunchedEffect(Unit) {
        onLoadEvents()
        onLoadNotes()
    }

    // Funciones de navegación
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

    // Actualizar nota al seleccionar fecha
    LaunchedEffect(selectedDateString, showNoteDialog) {
        if (showNoteDialog) {
            noteText = calendarNotes[selectedDateString] ?: ""
        }
    }

    // Dialogo de notas
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

    // --- UI DEL CALENDARIO ---
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            // 1. Títulos
            Text(
                text = "Calendario de Reservas",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Visualiza todas las reservas por canal de reserva",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Fila de Botones: Hoy | Anterior | Siguiente (CENTRADOS)
            Row(
                modifier = Modifier.fillMaxWidth(),
                // Esto junta los botones y centra todo el grupo en la pantalla
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón Hoy
                OutlinedButton(
                    onClick = { setToday() },
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Hoy", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), fontSize = 13.sp)
                }

                // Botón Anterior
                OutlinedButton(
                    onClick = { updateDate(-1) },
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Anterior", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
                }

                // Botón Siguiente
                OutlinedButton(
                    onClick = { updateDate(1) },
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Siguiente", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Título del Mes (Centrado)
            val cal = Calendar.getInstance()
            cal.set(Calendar.MONTH, displayMonth)
            cal.set(Calendar.YEAR, displayYear)
            val monthName = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale("es", "ES")) ?: ""

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = "$monthName de $displayYear".replaceFirstChar { it.uppercase() },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4. Toggle Mes / Agenda (Estilo Segmented Control)
            val borderColor = MaterialTheme.colorScheme.outlineVariant
            val activeBg = if(isDark) Color(0xFF424242) else Color(0xFFE0E0E0) // Gris claro cuando está activo
            val inactiveBg = Color.Transparent

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Row(
                    modifier = Modifier
                        .height(32.dp)
                        .border(1.dp, borderColor, RoundedCornerShape(6.dp))
                        .clip(RoundedCornerShape(6.dp))
                ) {
                    // Botón Mes
                    Box(
                        modifier = Modifier
                            .background(if (isMonthView) activeBg else inactiveBg)
                            .clickable { isMonthView = true }
                            .padding(horizontal = 24.dp)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Mes",
                            fontSize = 13.sp,
                            fontWeight = if(isMonthView) FontWeight.Bold else FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Divisor vertical
                    VerticalDivider(modifier = Modifier.fillMaxHeight().width(1.dp), color = borderColor)

                    // Botón Agenda
                    Box(
                        modifier = Modifier
                            .background(if (!isMonthView) activeBg else inactiveBg)
                            .clickable { isMonthView = false }
                            .padding(horizontal = 24.dp)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Agenda",
                            fontSize = 13.sp,
                            fontWeight = if(!isMonthView) FontWeight.Bold else FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 5. Contenido del Calendario (Grid o Agenda)
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
                // Vista Agenda (Lógica original)
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val currentMonthEvents = calendarEvents.filter { event ->
                    try {
                        val startStr = event.start?.substring(0, 10) ?: return@filter false
                        val endStr = event.end?.substring(0, 10) ?: startStr
                        val eventStart = dateFormat.parse(startStr) ?: return@filter false
                        val eventEnd = dateFormat.parse(endStr) ?: eventStart

                        val calendarCalc = Calendar.getInstance()
                        calendarCalc.set(Calendar.YEAR, displayYear)
                        calendarCalc.set(Calendar.MONTH, displayMonth)
                        calendarCalc.set(Calendar.DAY_OF_MONTH, 1)
                        val monthStart = calendarCalc.time

                        calendarCalc.set(Calendar.DAY_OF_MONTH, calendarCalc.getActualMaximum(Calendar.DAY_OF_MONTH))
                        val monthEnd = calendarCalc.time

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
    
    // Colores del calendario
    val borderColor = if (isDark) Color.Gray.copy(alpha = 0.3f) else Color.LightGray
    val todayBg = if (isDark) Color(0xFF1E3A5F) else Color(0xFFE3F2FD)
    val otherMonthBg = if (isDark) Color(0xFF2D2D2D) else Color(0xFFF5F5F5)

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

        for (i in 0 until offset) items.add(CalendarDayItem("", "", false, false))
        for (i in 1..daysInMonth) {
            val isToday =
                (todayCal.get(Calendar.YEAR) == year && todayCal.get(Calendar.MONTH) == month && todayCal.get(
                    Calendar.DAY_OF_MONTH
                ) == i)
            val dateStr = String.format("%04d-%02d-%02d", year, month + 1, i)
            items.add(CalendarDayItem(i.toString(), dateStr, true, isToday))
        }
        items
    }

    Column {
        Row(modifier = Modifier.fillMaxWidth()) {
            daysOfWeek.forEach { day ->
                Text(
                    day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider()
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

@Composable
fun NoteDialog(date: String, onDismiss: () -> Unit, onSave: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nota del día: $date") },
        text = { Text("Aquí puedes escribir notas...") },
        confirmButton = { Button(onClick = onSave) { Text("Guardar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
fun SummaryCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    count: String,
    bg: Color,
    tint: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(bg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = tint, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                label,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                count,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun FloorSelector(selectedFloor: Int, onFloorSelected: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .height(40.dp)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
    ) {
        FloorOption("1ro", selectedFloor == 1) { onFloorSelected(1) }
        VerticalDivider()
        FloorOption("2do", selectedFloor == 2) { onFloorSelected(2) }
        VerticalDivider()
        FloorOption("3ro", selectedFloor == 3) { onFloorSelected(3) }
    }
}

@Composable
fun FloorOption(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(45.dp)
            .background(if (isSelected) OrangePrimary else Color.Transparent)
            .clickable(onClick = onClick), contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun TabItemCompact(icon: ImageVector, label: String, isActive: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(4.dp)
            .widthIn(min = 70.dp)
    ) {
        Icon(
            icon,
            null,
            tint = if (isActive) OrangePrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            label,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            color = if (isActive) OrangePrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        if (isActive) Box(
            modifier = Modifier
                .height(3.dp)
                .width(40.dp)
                .background(OrangePrimary, RoundedCornerShape(2.dp))
        )
    }
}

@Composable
fun RoomRowSimple(item: RoomItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(item.number, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        Text(item.type, modifier = Modifier.weight(1f))
        Box(modifier = Modifier.weight(1f)) {
            Text(
                item.status,
                color = item.statusColor,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        }
    }
}

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
    var expandedRoom by remember { mutableStateOf(false) }
    
    // Cargar habitaciones disponibles excluyendo la reserva actual
    LaunchedEffect(checkIn, checkOut) {
        if (checkIn.isNotBlank() && checkOut.isNotBlank()) {
            viewModel.loadAvailableRooms(checkIn, checkOut, reservation.reservationId)
        }
    }
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
                        ExposedDropdownMenuBox(
                            expanded = expandedRoom,
                            onExpandedChange = { expandedRoom = !expandedRoom }
                        ) {
                            OutlinedTextField(
                                value = room,
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                shape = RoundedCornerShape(8.dp),
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRoom) },
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = OrangePrimary),
                                singleLine = true
                            )
                            ExposedDropdownMenu(
                                expanded = expandedRoom,
                                onDismissRequest = { expandedRoom = false }
                            ) {
                                uiState.availableRooms.forEach { availableRoom ->
                                    DropdownMenuItem(
                                        text = { Text("${availableRoom.code} - ${availableRoom.type ?: ""}") },
                                        onClick = {
                                            room = availableRoom.code ?: ""
                                            // Actualizar también el tipo si está disponible
                                            if (!availableRoom.type.isNullOrEmpty()) {
                                                roomType = when (availableRoom.type) {
                                                    "S", "Simple" -> "Simple"
                                                    "D", "Doble" -> "Doble"
                                                    "T", "Triple" -> "Triple"
                                                    "M", "Matrimonial" -> "Matrimonial"
                                                    else -> availableRoom.type ?: roomType
                                                }
                                            }
                                            expandedRoom = false
                                        }
                                    )
                                }
                            }
                        }
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