package com.keyli.plazatrujillo.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.keyli.plazatrujillo.ui.theme.*
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
    val fullDate: String, // Para saber qué fecha exacta es (YYYY-MM-DD)
    val isCurrentMonth: Boolean,
    val isToday: Boolean
)

@Composable
fun ReservaScreen(navController: NavHostController) {
    val scrollState = rememberScrollState()

    // 0 = Estado de Habitaciones
    // 1 = Reservas/Ventas Activas
    // 2 = Historial de Clientes
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {

        // --- CABECERA CON EL BOTÓN NUEVO ---
        // Usamos un Row para alinear Título a la izquierda y Botón a la derecha
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Gestión Hotelera",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack
            )

            // --- BOTÓN NUEVA RESERVA ---
            Button(
                onClick = {
                    // CORREGIDO: La ruta correcta es "new_reservation" (tal como está en tu NavigationWrapper)
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
            color = TextGray
        )

        Spacer(modifier = Modifier.height(20.dp))

        // --- PESTAÑAS PRINCIPALES ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
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
            0 -> RoomStatusView(navController)
            1 -> ReservasListView(navController)
            2 -> AccountStatusView(navController)
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}
// ==========================================================
// VISTA 1: ESTADO DE HABITACIONES
// ==========================================================
@Composable
fun RoomStatusView(navController: NavHostController) {
    // Estado para el piso seleccionado: 1, 2 o 3
    var selectedFloor by remember { mutableIntStateOf(1) }

    // Generamos la lista de habitaciones
    val allRooms = remember {
        listOf(
            // Piso 1
            RoomItem("111", "-", "Disponible", StatusGreen),
            RoomItem("112", "-", "Disponible", StatusGreen),
            RoomItem("113", "-", "Disponible", StatusGreen),
            // Piso 2
            RoomItem("210", "-", "Disponible", StatusGreen),
            RoomItem("211", "-", "Disponible", StatusGreen),
            RoomItem("212", "-", "Disponible", StatusGreen),
            RoomItem("213", "-", "Disponible", StatusGreen),
            RoomItem("214", "-", "Disponible", StatusGreen),
            RoomItem("215", "-", "Disponible", StatusGreen),
            // Piso 3
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

        // --- SECCIÓN DE RESUMEN ---
        val greenBg = Color(0xFFE8F5E9); val greenIcon = Color(0xFF2E7D32)
        val blueBg = Color(0xFFE3F2FD); val blueIcon = Color(0xFF1565C0)
        val purpleBg = Color(0xFFF3E5F5); val purpleIcon = Color(0xFF7B1FA2)
        val greyBg = Color(0xFFF5F5F5); val greyIcon = Color(0xFF616161)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SummaryCard(Modifier.weight(1f), Icons.Outlined.CheckCircle, "Disponibles", "15 Hab.", greenBg, greenIcon)
            SummaryCard(Modifier.weight(1f), Icons.Default.Group, "Ocupadas", "0 Hab.", blueBg, blueIcon)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SummaryCard(Modifier.weight(1f), Icons.Default.Assignment, "Reservadas", "0 Hab.", purpleBg, purpleIcon)
            SummaryCard(Modifier.weight(1f), Icons.Default.Settings, "Mantenimiento", "0 Hab.", greyBg, greyIcon)
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
                    color = TextBlack
                )
                Text(
                    text = "Listado actual de ocupación",
                    fontSize = 12.sp,
                    color = TextGray,
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
                border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                color = Color.White,
                modifier = Modifier.height(40.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.FreeBreakfast,
                        contentDescription = null,
                        tint = TextBlack,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Reporte de\nDesayunos",
                        fontSize = 10.sp,
                        color = TextBlack,
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
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("Habitación", modifier = Modifier.weight(1f), color = TextGray, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("Tipo", modifier = Modifier.weight(1f), color = TextGray, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("Estado", modifier = Modifier.weight(1f), color = TextGray, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = Color(0xFFEEEEEE))

                if (filteredRooms.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center){
                        Text("No hay habitaciones en este piso", color = TextGray)
                    }
                } else {
                    filteredRooms.forEach { room ->
                        RoomRowSimple(item = room)
                        Divider(color = Color(0xFFF9F9F9))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Calendario
        DynamicCalendarSection()
    }
}

// ==========================================================
// VISTA 2: RESERVAS / VENTAS ACTIVAS
// ==========================================================
@Composable
fun ReservasListView(navController: NavHostController) {
    Column {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 50.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay reservas registradas",
                    color = Color(0xFF5F6368),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        DynamicCalendarSection()
    }
}

// ==========================================================
// VISTA 3: HISTORIAL DE CLIENTES
// ==========================================================
@Composable
fun AccountStatusView(navController: NavHostController) {
    Column {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                Text("Buscar", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                Spacer(modifier = Modifier.height(8.dp))

                var searchText by remember { mutableStateOf("") }
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = { Text("Nombre o DNI", color = TextGray.copy(alpha = 0.7f)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedBorderColor = OrangePrimary,
                        cursorColor = OrangePrimary,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text("Historial de Clientes", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                Text("Clientes con estado Check-out", fontSize = 13.sp, color = TextGray)

                Spacer(modifier = Modifier.height(20.dp))
                Divider(color = Color(0xFFEEEEEE))
                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    val headerStyle = Modifier.weight(1f)
                    val textStyle = MaterialTheme.typography.bodySmall.copy(
                        color = TextGray, fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = TextAlign.Center
                    )
                    Text("Cliente", modifier = headerStyle, style = textStyle, textAlign = TextAlign.Start)
                    Text("Documento", modifier = headerStyle, style = textStyle)
                    Text("Habitación", modifier = headerStyle, style = textStyle)
                    Text("Tipo", modifier = headerStyle, style = textStyle)
                    Text("Check-out", modifier = headerStyle, style = textStyle)
                    Text("Canal", modifier = headerStyle, style = textStyle)
                    Text("Total", modifier = headerStyle, style = textStyle, textAlign = TextAlign.End)
                }

                Spacer(modifier = Modifier.height(10.dp))
                Divider(color = Color(0xFFEEEEEE))

                Spacer(modifier = Modifier.height(40.dp))
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Outlined.Description, contentDescription = null, tint = Color(0xFF9E9E9E), modifier = Modifier.size(60.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No hay registros disponibles", fontWeight = FontWeight.Bold, color = TextGray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("No se encontraron clientes con estado Check-out", color = TextGray.copy(alpha = 0.7f), fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DynamicCalendarSection()
    }
}

// ==========================================================
// COMPONENTE CALENDARIO
// ==========================================================
@Composable
fun DynamicCalendarSection() {
    val currentCalendar = remember { Calendar.getInstance() }

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

    fun setToday() {
        val cal = Calendar.getInstance()
        displayMonth = cal.get(Calendar.MONTH)
        displayYear = cal.get(Calendar.YEAR)
    }

    if (showNoteDialog) {
        NoteDialog(
            date = selectedDateString,
            onDismiss = { showNoteDialog = false },
            onSave = { showNoteDialog = false }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            Text(
                text = "Calendario de Reservas",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Visualiza todas las reservas por canal de reserva",
                fontSize = 13.sp,
                color = TextGray
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Controles Superiores
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                OutlinedButton(
                    onClick = { setToday() },
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                    modifier = Modifier.height(32.dp)
                ) { Text("Hoy", color = TextGray, fontSize = 12.sp) }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedButton(
                    onClick = { updateDate(-1) },
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                    modifier = Modifier.height(32.dp)
                ) { Text("Anterior", color = TextBlack, fontSize = 12.sp) }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedButton(
                    onClick = { updateDate(1) },
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                    modifier = Modifier.height(32.dp)
                ) { Text("Siguiente", color = TextBlack, fontSize = 12.sp) }
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
                    color = TextBlack
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Toggle Mes/Agenda
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Row(
                    modifier = Modifier
                        .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(4.dp))
                        .height(32.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(if (isMonthView) Color(0xFFE0E0E0) else Color.White)
                            .padding(horizontal = 16.dp)
                            .fillMaxHeight()
                            .clickable { isMonthView = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Mes", fontSize = 12.sp, color = if(isMonthView) TextBlack else TextGray, fontWeight = if(isMonthView) FontWeight.Bold else FontWeight.Normal)
                    }
                    VerticalDivider(color = Color(0xFFE0E0E0))
                    Box(
                        modifier = Modifier
                            .background(if (!isMonthView) Color(0xFFE0E0E0) else Color.White)
                            .padding(horizontal = 16.dp)
                            .fillMaxHeight()
                            .clickable { isMonthView = false },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Agenda", fontSize = 12.sp, color = if(!isMonthView) TextBlack else TextGray, fontWeight = if(!isMonthView) FontWeight.Bold else FontWeight.Normal)
                    }
                }
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
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "There are no events in this range.",
                        color = TextGray,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

// Ventana Emergente "Nota del día"
@Composable
fun NoteDialog(date: String, onDismiss: () -> Unit, onSave: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(text = "Nota del día", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = date, fontSize = 14.sp, color = TextGray)
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Outlined.Close, contentDescription = "Cerrar", tint = TextBlack)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                var text by remember { mutableStateOf("") }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    if (text.isEmpty()) {
                        Text("Escribe una nota breve", color = Color.Gray, fontSize = 14.sp)
                    }
                    BasicTextField(
                        value = text,
                        onValueChange = { text = it },
                        textStyle = TextStyle(color = TextBlack, fontSize = 14.sp),
                        modifier = Modifier.fillMaxSize()
                    )
                    Icon(
                        imageVector = Icons.Default.DragHandle,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.align(Alignment.BottomEnd).size(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = onSave,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4C6EF5)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Text("Guardar", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    OutlinedButton(
                        onClick = { /* Acción eliminar */ },
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Text("Eliminar", color = TextBlack)
                    }
                }
            }
        }
    }
}

// Lógica de grilla usando java.util.Calendar
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
                Text(text = day, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, color = TextBlack, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Divider(color = Color(0xFFEEEEEE))

        val weeks = calendarItems.chunked(7)
        weeks.forEach { week ->
            Row(modifier = Modifier.fillMaxWidth().height(50.dp)) {
                week.forEach { dayItem ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .border(0.5.dp, Color(0xFFF5F5F5))
                            .clickable {
                                onDayClick(dayItem)
                            },
                        contentAlignment = Alignment.TopStart
                    ) {
                        if (dayItem.isToday) {
                            Box(modifier = Modifier.fillMaxSize().background(Color(0xFF2C3E50)))
                        } else if (!dayItem.isCurrentMonth) {
                            Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF2F2F2)))
                        }

                        Text(
                            text = dayItem.dayString,
                            color = when {
                                dayItem.isToday -> Color.White
                                dayItem.isCurrentMonth -> TextBlack
                                else -> TextGray.copy(alpha = 0.5f)
                            },
                            fontSize = 13.sp,
                            fontWeight = if(dayItem.isToday) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(6.dp).align(Alignment.Center)
                        )
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.Start) {
            Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)).background(bg), contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = label, color = TextGray, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = count, color = TextBlack, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun FloorSelector(selectedFloor: Int, onFloorSelected: (Int) -> Unit) {
    Row(
        modifier = Modifier.height(40.dp).border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp)).clip(RoundedCornerShape(8.dp)).background(Color.White)
    ) {
        FloorOption("1ro", selectedFloor == 1) { onFloorSelected(1) }
        VerticalDivider(modifier = Modifier.width(1.dp).fillMaxHeight(), color = Color(0xFFE0E0E0))
        FloorOption("2do", selectedFloor == 2) { onFloorSelected(2) }
        VerticalDivider(modifier = Modifier.width(1.dp).fillMaxHeight(), color = Color(0xFFE0E0E0))
        FloorOption("3ro", selectedFloor == 3) { onFloorSelected(3) }
    }
}

@Composable
fun FloorOption(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxHeight().width(45.dp).background(if (isSelected) OrangePrimary else Color.White).clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, color = if (isSelected) Color.White else TextBlack, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, fontSize = 13.sp)
    }
}

@Composable
fun TabItemCompact(icon: ImageVector, label: String, isActive: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }.padding(horizontal = 4.dp).widthIn(min = 70.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = if (isActive) OrangePrimary else TextGray, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, color = if (isActive) OrangePrimary else TextGray, fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal, fontSize = 11.sp, textAlign = TextAlign.Center, lineHeight = 12.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Box(modifier = Modifier.width(40.dp).height(3.dp).background(if (isActive) OrangePrimary else Color.Transparent, RoundedCornerShape(2.dp)))
    }
}

@Composable
fun RoomRowSimple(item: RoomItem) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(text = item.number, fontWeight = FontWeight.Bold, color = TextBlack, fontSize = 15.sp, modifier = Modifier.weight(1f))
        Text(text = item.type, color = TextGray, fontSize = 14.sp, modifier = Modifier.weight(1f))
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
            Text(text = item.id.split("-")[0], color = TextGray, fontSize = 11.sp)
            Text(text = item.id.split("-")[1], color = TextBlack, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Text(text = item.nombre, color = TextBlack, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1.3f), lineHeight = 16.sp)
        Column(modifier = Modifier.weight(0.7f)) {
            Text(text = item.tipoHab, color = TextGray, fontSize = 11.sp)
            Text(text = item.habitacion, color = TextBlack, fontSize = 12.sp)
        }
        Text(text = item.precio, color = TextBlack, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(0.9f))
        Row(modifier = Modifier.weight(1.2f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(item.estadoColor.copy(alpha = 0.15f)).padding(horizontal = 6.dp, vertical = 4.dp)) {
                Text(text = item.estado, color = item.estadoColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar", tint = OrangePrimary, modifier = Modifier.size(16.dp).clickable { })
        }
    }
}