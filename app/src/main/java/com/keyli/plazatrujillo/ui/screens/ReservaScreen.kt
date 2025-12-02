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
import androidx.navigation.NavHostController
import com.keyli.plazatrujillo.data.model.Reservation
import com.keyli.plazatrujillo.ui.theme.OrangePrimary
import com.keyli.plazatrujillo.ui.theme.StatusGreen
import com.keyli.plazatrujillo.ui.viewmodel.ReservaViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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
    viewModel: ReservaViewModel = viewModel()
) {
    val scrollState = rememberScrollState()

    // Observamos los datos del ViewModel
    val reservas by viewModel.reservas.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // 0 = Estado de Habitaciones, 1 = Reservas, 2 = Clientes
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
            0 -> RoomStatusView(navController, reservas.size)
            1 -> ReservasListView(navController, reservas, isLoading)
            2 -> AccountStatusView(navController)
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

// ==========================================================
// VISTA 1: ESTADO DE HABITACIONES
// ==========================================================
@Composable
fun RoomStatusView(navController: NavHostController, totalReservas: Int) {
    var selectedFloor by remember { mutableIntStateOf(1) }
    val isDark = isReservaDarkTheme()

    val allRooms = remember {
        listOf(
            RoomItem("111", "-", "Disponible", StatusGreen),
            RoomItem("112", "-", "Disponible", StatusGreen),
            RoomItem("113", "-", "Disponible", StatusGreen),
            RoomItem("210", "-", "Disponible", StatusGreen),
            RoomItem("211", "-", "Disponible", StatusGreen),
            RoomItem("212", "-", "Disponible", StatusGreen),
            RoomItem("213", "-", "Disponible", StatusGreen),
            RoomItem("214", "-", "Disponible", StatusGreen),
            RoomItem("215", "-", "Disponible", StatusGreen),
            RoomItem("310", "-", "Disponible", StatusGreen),
            RoomItem("311", "-", "Disponible", StatusGreen),
            RoomItem("312", "-", "Disponible", StatusGreen),
            RoomItem("313", "-", "Disponible", StatusGreen),
            RoomItem("314", "-", "Disponible", StatusGreen),
            RoomItem("315", "-", "Disponible", StatusGreen),
        )
    }

    val filteredRooms = allRooms.filter { room ->
        room.number.startsWith(selectedFloor.toString().take(1))
    }

    Column {
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
            SummaryCard(
                Modifier.weight(1f),
                Icons.Outlined.CheckCircle,
                "Disponibles",
                "15 Hab.",
                greenBg,
                greenIcon
            )
            SummaryCard(
                Modifier.weight(1f),
                Icons.Default.Group,
                "Ocupadas",
                "0 Hab.",
                blueBg,
                blueIcon
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SummaryCard(
                Modifier.weight(1f),
                Icons.Default.Assignment,
                "Reservadas",
                "$totalReservas Hab.",
                purpleBg,
                purpleIcon
            )
            SummaryCard(
                Modifier.weight(1f),
                Icons.Default.Settings,
                "Mantenimiento",
                "0 Hab.",
                greyBg,
                greyIcon
            )
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
        DynamicCalendarSection()
    }
}

// ==========================================================
// VISTA 2: RESERVAS (CON SCROLL HORIZONTAL)
// ==========================================================
@Composable
fun ReservasListView(
    navController: NavHostController,
    reservas: List<Reservation>,
    isLoading: Boolean
) {
    Column {
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = OrangePrimary)
            }
        } else if (reservas.isEmpty()) {
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
                    Text(
                        "No hay reservas registradas",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                val horizontalScrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .horizontalScroll(horizontalScrollState) // SCROLL AQUÍ
                ) {
                    // Cabecera con ancho fijo (500.dp total)
                    Row(
                        modifier = Modifier.width(500.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "ID",
                            modifier = Modifier.width(60.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Huésped",
                            modifier = Modifier.width(130.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Habitación",
                            modifier = Modifier.width(80.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Total",
                            modifier = Modifier.width(80.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Estado",
                            modifier = Modifier.width(110.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier.width(500.dp)
                    )

                    reservas.forEach { reserva ->
                        val uiItem = ReservaItem(
                            id = reserva.reservationId,
                            nombre = reserva.guest,
                            habitacion = reserva.room ?: "S/A",
                            tipoHab = "-",
                            precio = reserva.total,
                            estado = reserva.status,
                            estadoColor = getStatusColor(reserva.status)
                        )
                        ReservaRow(uiItem)
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(
                                alpha = 0.5f
                            ), modifier = Modifier.width(500.dp)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        DynamicCalendarSection()
    }
}

// ==========================================================
// ITEM RESERVA (ANCHO FIJO)
// ==========================================================
@Composable
fun ReservaRow(item: ReservaItem) {
    val parts = item.id.split("-")
    val prefix = parts.getOrElse(0) { "" }
    val number = parts.getOrElse(1) { item.id }

    Row(
        modifier = Modifier
            .width(500.dp) // Ancho fijo para coincidir con cabecera
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Columna 1: ID
        Column(modifier = Modifier.width(60.dp)) {
            Text(
                text = prefix,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                fontSize = 11.sp
            )
            Text(
                text = number,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
        // Columna 2: Nombre
        Text(
            text = item.nombre,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.width(130.dp),
            lineHeight = 16.sp,
            maxLines = 2
        )
        // Columna 3: Habitación
        Column(modifier = Modifier.width(80.dp)) {
            Text(
                text = item.tipoHab,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                fontSize = 11.sp
            )
            Text(
                text = item.habitacion,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 12.sp
            )
        }
        // Columna 4: Precio
        Text(
            text = item.precio,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            modifier = Modifier.width(80.dp)
        )
        // Columna 5: Estado
        Row(
            modifier = Modifier.width(110.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(item.estadoColor.copy(alpha = 0.15f))
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            ) {
                Text(
                    text = item.estado,
                    color = item.estadoColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Editar",
                tint = OrangePrimary,
                modifier = Modifier
                    .size(16.dp)
                    .clickable { })
        }
    }
}

// ==========================================================
// VISTA 3: CLIENTES
// ==========================================================
@Composable
fun AccountStatusView(navController: NavHostController) {
    val isDark = isReservaDarkTheme()
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
                var searchText by remember { mutableStateOf("") }
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Cliente",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        "Total",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(40.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Outlined.Description,
                        null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        modifier = Modifier.size(60.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No hay registros disponibles",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        DynamicCalendarSection()
    }
}

// ==========================================================
// COMPONENTES AUXILIARES (HELPERS)
// ==========================================================

@Composable
fun DynamicCalendarSection() {
    val currentCalendar = remember { Calendar.getInstance() }
    val isDark = isReservaDarkTheme()
    var displayMonth by remember { mutableIntStateOf(currentCalendar.get(Calendar.MONTH)) }
    var displayYear by remember { mutableIntStateOf(currentCalendar.get(Calendar.YEAR)) }
    var isMonthView by remember { mutableStateOf(true) }
    var showNoteDialog by remember { mutableStateOf(false) }
    var selectedDateString by remember { mutableStateOf("") }

    fun updateDate(monthOffset: Int) {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, displayYear)
        cal.set(Calendar.MONTH, displayMonth)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.add(Calendar.MONTH, monthOffset)
        displayMonth = cal.get(Calendar.MONTH)
        displayYear = cal.get(Calendar.YEAR)
    }

    if (showNoteDialog) {
        NoteDialog(
            date = selectedDateString,
            onDismiss = { showNoteDialog = false },
            onSave = { showNoteDialog = false })
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "Calendario de Reservas",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                OutlinedButton(
                    onClick = { updateDate(-1) },
                    shape = RoundedCornerShape(4.dp)
                ) { Text("Anterior") }
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(
                    onClick = { updateDate(1) },
                    shape = RoundedCornerShape(4.dp)
                ) { Text("Siguiente") }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                val cal = Calendar.getInstance()
                cal.set(Calendar.MONTH, displayMonth)
                val monthName =
                    cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale("es", "ES")) ?: ""
                Text(
                    "$monthName $displayYear",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            if (isMonthView) {
                CalendarGridLegacy(displayMonth, displayYear) { clickedDayItem ->
                    selectedDateString = clickedDayItem.fullDate
                    showNoteDialog = true
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No hay eventos",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
fun CalendarGridLegacy(month: Int, year: Int, onDayClick: (CalendarDayItem) -> Unit) {
    val daysOfWeek = listOf("Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb")
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
            Row(modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)) {
                week.forEach { dayItem ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { if (dayItem.isCurrentMonth) onDayClick(dayItem) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (dayItem.dayString.isNotEmpty()) {
                            Text(
                                text = dayItem.dayString,
                                fontWeight = if (dayItem.isToday) FontWeight.Bold else FontWeight.Normal,
                                color = if (dayItem.isToday) OrangePrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                // Rellenar espacios vacíos si la semana está incompleta
                for (i in 0 until (7 - week.size)) {
                    Spacer(modifier = Modifier.weight(1f))
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

fun getStatusColor(status: String?): Color {
    return when (status?.lowercase()) {
        "confirmada" -> StatusGreen
        "pendiente" -> Color(0xFFFBC02D)
        "cancelada" -> Color(0xFFD32F2F)
        "check-in" -> Color(0xFF1976D2)
        "check-out" -> Color(0xFF7B1FA2)
        else -> Color.Gray
    }
}