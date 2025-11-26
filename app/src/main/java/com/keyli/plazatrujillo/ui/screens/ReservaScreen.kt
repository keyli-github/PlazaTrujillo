package com.keyli.plazatrujillo.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.keyli.plazatrujillo.ui.theme.*

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

@Composable
fun ReservaScreen(navController: NavHostController) {
    val scrollState = rememberScrollState()
    // 0 = Habitaciones, 1 = Reservas, 2 = Estado de Cuenta
    var selectedTab by remember { mutableStateOf(2) } // Empieza en 2 para que veas lo nuevo

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {

        // --- TÍTULO ---
        Text(
            text = "Gestión de Reservas",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextBlack
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Administra todas las reservas y habitaciones del hotel",
            fontSize = 14.sp,
            color = TextGray
        )

        Spacer(modifier = Modifier.height(20.dp))

        // --- PESTAÑAS (Ahora son 3) ---
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
                horizontalArrangement = Arrangement.SpaceBetween, // Espacio igual entre las 3
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tab 1: Habitaciones
                TabItemCompact(
                    icon = Icons.Default.Home,
                    label = "Habitaciones",
                    isActive = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )

                // Tab 2: Reservas
                TabItemCompact(
                    icon = Icons.Default.List,
                    label = "Reservas",
                    isActive = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )

                // Tab 3: Estado de Cuenta (NUEVA)
                TabItemCompact(
                    icon = Icons.Default.Description, // Icono de documento/factura
                    label = "Estado Cta.",
                    isActive = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- CONTENIDO CAMBIANTE ---
        when (selectedTab) {
            0 -> RoomStatusView()
            1 -> ReservasListView(navController)
            2 -> AccountStatusView(navController) // <--- Llamamos a la nueva vista
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

// ==========================================================
// VISTA 1: ESTADO DE HABITACIONES (La que hicimos antes)
// ==========================================================
@Composable
fun RoomStatusView() {
    val roomList = listOf(
        RoomItem("101", "Individual", "Disponible", StatusGreen),
        RoomItem("102", "Individual", "Ocupada", OrangePrimary),
        RoomItem("201", "Doble", "Reservada", OrangeSecondary),
        RoomItem("202", "Matrimonial", "Mantenimiento", StatusRed),
        RoomItem("301", "Suite", "Disponible", StatusGreen)
    )

    Column {
        // ... (El código de tus cards de resumen sigue igual aquí) ...
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatusCard(Modifier.weight(1f), Icons.Default.Check, "Disponibles", "45 Hab.", StatusGreen)
            StatusCard(Modifier.weight(1f), Icons.Default.DateRange, "Reservadas", "32 Hab.", OrangeSecondary)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatusCard(Modifier.weight(1f), Icons.Default.Person, "Ocupadas", "68 Hab.", Color(0xFFFFD700))
            StatusCard(Modifier.weight(1f), Icons.Default.Warning, "Mantenimiento", "15 Hab.", StatusRed)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Tabla Habitaciones
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // ... (Cabeceras y filas de la tabla siguen igual) ...
                Text("Habitaciones", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("Habitación", modifier = Modifier.weight(1.2f), color = TextGray, fontSize = 13.sp)
                    Text("Tipo", modifier = Modifier.weight(1.2f), color = TextGray, fontSize = 13.sp)
                    Text("Estado", modifier = Modifier.weight(1.2f), color = TextGray, fontSize = 13.sp)
                    Text("Acciones", modifier = Modifier.weight(0.8f), color = TextGray, fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = Color(0xFFEEEEEE))

                roomList.forEach { room ->
                    RoomRow(item = room)
                    Divider(color = Color(0xFFF9F9F9))
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- AQUÍ ESTÁ LO NUEVO: EL CALENDARIO ---
        CalendarSection()
        Spacer(modifier = Modifier.height(24.dp))

        // --- AGREGA ESTA LÍNEA AQUÍ ---
        MovimientosSection()
    }
}

// ==========================================================
// VISTA 2: LISTADO DE RESERVAS (La NUEVA de la imagen)
// ==========================================================
// VISTA 2: LISTADO DE RESERVAS
@Composable
fun ReservasListView(navController: NavHostController) {
    val reservas = listOf(
        ReservaItem("RES-001", "Carlos\nMendoza", "201", "Suite", "S/ 960", "Confirmada", StatusGreen),
        ReservaItem("RES-002", "Ana Garcia\nLópez", "105", "Hab.", "S/ 735", "Check-in", OrangeSecondary),
        ReservaItem("RES-003", "Roberto\nSilva", "302", "Suite", "S/ 960", "Confirmada", StatusGreen),
        ReservaItem("RES-004", "Luis\nRamírez", "110", "Hab.", "S/ 735", "Confirmada", StatusGreen)
    )

    Column {
        // --- CARD LISTADO ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                // Cabecera con Botón Naranja
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Listado de Reservas", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                    Button(
                        onClick = { navController.navigate("new_reservation") },
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("+ Nueva", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Encabezados Tabla
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("ID", modifier = Modifier.weight(0.7f), color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("Huésped", modifier = Modifier.weight(1.3f), color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("Hab.", modifier = Modifier.weight(0.7f), color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("Total", modifier = Modifier.weight(0.9f), color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("Estado", modifier = Modifier.weight(1.2f), color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = Color(0xFFEEEEEE))

                // Filas de Reservas
                reservas.forEach { reserva ->
                    ReservaRow(item = reserva)
                    Divider(color = Color(0xFFF9F9F9))
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- AQUI AGREGAMOS EL CALENDARIO (Reutilizamos el que ya existe) ---
        CalendarSection()
    }
}

// --- COMPONENTES AUXILIARES ---

@Composable
fun TabItem(text: String, icon: ImageVector, isActive: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isActive) OrangePrimary else TextGray
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                color = if (isActive) OrangePrimary else TextGray,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        if (isActive) {
            Box(
                modifier = Modifier
                    .width(80.dp) // Ancho dinámico de la línea
                    .height(3.dp)
                    .background(OrangePrimary, RoundedCornerShape(2.dp))
            )
        } else {
            Box(modifier = Modifier.height(3.dp)) // Espacio invisible para que no salte
        }
    }
}

@Composable
fun ReservaRow(item: ReservaItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ID
        Column(modifier = Modifier.weight(0.7f)) {
            Text(text = item.id.split("-")[0], color = TextGray, fontSize = 11.sp)
            Text(text = item.id.split("-")[1], color = TextBlack, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        // Huésped
        Text(
            text = item.nombre,
            color = TextBlack,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1.3f),
            lineHeight = 16.sp
        )

        // Habitación
        Column(modifier = Modifier.weight(0.7f)) {
            Text(text = item.tipoHab, color = TextGray, fontSize = 11.sp)
            Text(text = item.habitacion, color = TextBlack, fontSize = 12.sp)
        }

        // Total
        Text(
            text = item.precio,
            color = TextBlack,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            modifier = Modifier.weight(0.9f)
        )

        // Estado y Editar
        Row(
            modifier = Modifier.weight(1.2f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Badge de estado
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
                    fontWeight = FontWeight.Bold
                )
            }

            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Editar",
                tint = OrangePrimary,
                modifier = Modifier.size(16.dp).clickable { }
            )
        }
    }
}

// Reutilizamos StatusCard y RoomRow del código anterior
@Composable
fun StatusCard(modifier: Modifier = Modifier, icon: ImageVector, label: String, count: String, color: Color) {
    Card(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(color.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = label, fontSize = 12.sp, color = TextGray) // Texto un poco más pequeño para que quepa
            }
            Text(text = count, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextBlack)
        }
    }
}

@Composable
fun RoomRow(item: RoomItem) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = item.number, fontWeight = FontWeight.Bold, color = TextBlack, modifier = Modifier.weight(1.2f))
        Text(text = item.type, color = TextGray, fontSize = 14.sp, modifier = Modifier.weight(1.2f))
        Text(text = item.status, color = item.statusColor, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.weight(1.2f))
        Box(modifier = Modifier.weight(0.8f)) {
            Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar", tint = OrangePrimary, modifier = Modifier.size(20.dp))
        }
    }
}

// --- PEGA ESTO AL FINAL DE TU ARCHIVO ---

@Composable
fun CalendarSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Titulo
            Text(
                text = "Calendario de reservas : visualiza todas las reservas por canal de reserva",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Toggle Semana / Mes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Box(
                    modifier = Modifier
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(50))
                        .padding(4.dp)
                ) {
                    Row {
                        // Botón Semana (Inactivo)
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .clickable { },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Semana", color = TextBlack, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        }

                        // Botón Mes (Activo - Naranja)
                        Box(
                            modifier = Modifier
                                .background(OrangePrimary, RoundedCornerShape(50))
                                .padding(horizontal = 24.dp, vertical = 8.dp)
                                .clickable { },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Mes", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Navegación del Mes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Anterior",
                    tint = OrangePrimary,
                    modifier = Modifier.clickable { }
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = TextBlack,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "NOVIEMBRE 2025",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextBlack
                    )
                }

                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Siguiente",
                    tint = OrangePrimary,
                    modifier = Modifier.clickable { }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Grid del Calendario
            CalendarGrid()
        }
    }
}

@Composable
fun CalendarGrid() {
    val daysOfWeek = listOf("DOM", "LUN", "MAR", "MIE", "JUE", "VIE", "SAB")

    // Datos simulados para Noviembre 2025
    val days = listOf(
        CalendarDay("26", isCurrentMonth = false), CalendarDay("27", isCurrentMonth = false), CalendarDay("28", isCurrentMonth = false),
        CalendarDay("29", isCurrentMonth = false), CalendarDay("30", isCurrentMonth = false), CalendarDay("31", isCurrentMonth = false),
        CalendarDay("1"), CalendarDay("2"), CalendarDay("3"), CalendarDay("4"), CalendarDay("5"), CalendarDay("6"), CalendarDay("7"),
        CalendarDay("8"), CalendarDay("9"), CalendarDay("10"), CalendarDay("11"), CalendarDay("12"), CalendarDay("13"), CalendarDay("14"),
        CalendarDay("15"), CalendarDay("16"), CalendarDay("17"), CalendarDay("18"), CalendarDay("19"), CalendarDay("20"), CalendarDay("21"),
        CalendarDay("22"), CalendarDay("23"), CalendarDay("24"),
        CalendarDay("25", isSelected = true), // EL DÍA SELECCIONADO
        CalendarDay("26"), CalendarDay("27"), CalendarDay("28"),
        CalendarDay("29"), CalendarDay("30"),
        CalendarDay("1", isCurrentMonth = false), CalendarDay("2", isCurrentMonth = false), CalendarDay("3", isCurrentMonth = false),
        CalendarDay("4", isCurrentMonth = false), CalendarDay("5", isCurrentMonth = false), CalendarDay("6", isCurrentMonth = false)
    )

    Column {
        // Cabecera de días
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    color = TextGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Días numéricos
        val weeks = days.chunked(7)

        weeks.forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                week.forEach { day ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(2.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (day.isSelected) OrangePrimary else if (day.isCurrentMonth) Color(0xFFF9F9F9) else Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day.number,
                            color = if (day.isSelected) Color.White else if (day.isCurrentMonth) TextBlack else TextGray.copy(alpha = 0.5f),
                            fontWeight = if (day.isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

data class CalendarDay(
    val number: String,
    val isCurrentMonth: Boolean = true,
    val isSelected: Boolean = false
)

// ==========================================
// SECCIÓN DE MOVIMIENTOS (Debajo del Calendario)
// ==========================================

@Composable
fun MovimientosSection(navController: NavHostController? = null) { // <--- Parámetro opcional
    Column(modifier = Modifier.fillMaxWidth()) {

        // Cabecera
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Movimientos", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                Text("Detalle de consumos", fontSize = 13.sp, color = TextGray)
            }

            Row {
                // BOTÓN + MOVIMIENTO CONECTADO
                Button(
                    onClick = { navController?.navigate("new_movement") }, // <--- AQUÍ LA CONEXIÓN
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    modifier = Modifier.height(40.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("+ Movimiento", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Botón Comanda
                Button(
                    onClick = { navController?.navigate("new_comanda") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEEEEE)),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    modifier = Modifier.height(40.dp)
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = TextBlack, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Comanda", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextBlack)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ... (El resto de la tabla de movimientos sigue igual, no necesitas cambiarlo) ...
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Encabezados
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("Concepto", modifier = Modifier.weight(1.8f), color = TextGray, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("Fecha", modifier = Modifier.weight(0.8f), color = TextGray, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("Monto", modifier = Modifier.weight(0.8f), color = TextGray, fontSize = 13.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
                    Spacer(modifier = Modifier.width(24.dp))
                }

                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = Color(0xFFEEEEEE))

                MovimientoRow("Alojamiento - Suite 2...", "15/01/25", "S/ 320.00")
                Divider(color = Color(0xFFF9F9F9))
                MovimientoRow("Alojamiento - Suite 2...", "16/01/25", "S/ 320.00")
                Divider(color = Color(0xFFF9F9F9))
                MovimientoRow("Lavandería - Planchado", "16/01/25", "S/ 25.00")
                Divider(color = Color(0xFFF9F9F9))
                MovimientoRow("Restaurante - Almuerzo", "16/01/25", "S/ 60.00")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Footer totales (copia lo que ya tenías o déjalo igual si no lo borraste)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Card(modifier = Modifier.weight(0.8f), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Total Día", color = TextGray, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("S/ 725.00", color = TextBlack, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }
            Card(modifier = Modifier.weight(1.2f), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column { Text("Acumulado", color = TextGray, fontSize = 12.sp); Spacer(modifier = Modifier.height(4.dp)); Text("S/ 725.00", color = TextBlack, fontSize = 16.sp, fontWeight = FontWeight.Bold) }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column { Text("Saldo\nPendiente", color = TextGray, fontSize = 11.sp, lineHeight = 14.sp); Spacer(modifier = Modifier.height(4.dp)); Text("S/ 725.00", color = OrangePrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold) }
                }
            }
        }
    }
}

@Composable
fun MovimientoRow(concepto: String, fecha: String, monto: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = concepto,
            modifier = Modifier.weight(1.8f),
            color = TextBlack,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )
        Text(
            text = fecha,
            modifier = Modifier.weight(0.8f),
            color = TextGray,
            fontSize = 12.sp
        )
        Text(
            text = monto,
            modifier = Modifier.weight(0.8f),
            color = TextBlack,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Icono Borrar Rojo
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Borrar",
            tint = StatusRed,
            modifier = Modifier.size(20.dp).clickable { }
        )
    }
}

// ==========================================
// VISTA 3: ESTADO DE CUENTA (NUEVA)
// ==========================================

@Composable
fun AccountStatusView(navController: NavHostController) {
    Column {

        // --- SECCIÓN SUPERIOR: DATOS DE RESERVA ---
        // (Los 3 cuadros grises de la imagen)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Dropdown: Reserva / Huésped (Ocupa más espacio)
            Column(modifier = Modifier.weight(1.4f)) {
                Text("Reserva / Huésped", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))

                // Caja gris con borde redondeado
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Carlos Men...", color = TextBlack, fontSize = 13.sp)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = TextGray)
                    }
                }
            }

            // Input: Pagos a Cta.
            InputAmountBox(label = "Pagos a Cta.", value = "S/ 0.00", modifier = Modifier.weight(0.8f))

            // Input: Saldo Ant.
            InputAmountBox(label = "Saldo Ant.", value = "S/ 0.00", modifier = Modifier.weight(0.8f))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- REUTILIZAMOS LA SECCIÓN DE MOVIMIENTOS ---
        // Como ya escribiste el código de la tabla de movimientos antes,
        // solo tenemos que llamarla aquí y aparecerá mágicamente.
        MovimientosSection(navController)
        Spacer(modifier = Modifier.height(24.dp))

        // --- AGREGAMOS ESTO AL FINAL ---
        RecaudacionSection()
    }
}

// Helper para los cuadritos de monto (Pagos y Saldo)
@Composable
fun InputAmountBox(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(label, color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp) // Altura fija igual a la imagen
                .background(Color.White, RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp)
                // Borde gris claro
                .then(Modifier.background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                ),
            contentAlignment = Alignment.Center
        ) {
            // Borde simulado (o simplemente fondo gris claro como en la imagen)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent)
            ) {
                Text(
                    text = value,
                    color = TextBlack,
                    fontSize = 13.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

// Helper para Tabs más pequeños (para que quepan 3)
@Composable
fun TabItemCompact(icon: ImageVector, label: String, isActive: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isActive) OrangePrimary else TextGray,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            // Ocultamos el texto en pantallas muy pequeñas si es necesario, pero aquí debería caber
            Text(
                text = label,
                color = if (isActive) OrangePrimary else TextGray,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                fontSize = 12.sp
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        if (isActive) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(3.dp)
                    .background(OrangePrimary, RoundedCornerShape(2.dp))
            )
        } else {
            Box(modifier = Modifier.height(3.dp))
        }
    }
}

// ==========================================
// SECCIÓN DE RECAUDACIÓN Y TRANSACCIONES
// ==========================================

@Composable
fun RecaudacionSection() {
    Column(modifier = Modifier.fillMaxWidth()) {

        // 1. Tarjeta de Recaudación Total (Naranja claro)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)), // Fondo durazno suave
            border = BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.3f)),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "RECAUDACIÓN TOTAL",
                        color = OrangePrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Cierre estimado de hoy",
                        color = OrangePrimary.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
                Text(
                    text = "S/ 2,660.00",
                    color = OrangePrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 2. Tarjeta Movimientos Hoy
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                // Cabecera con Filtro
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text("Movimientos Hoy", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Últimas 5 transacciones", fontSize = 13.sp, color = TextGray)
                    }
                    // Botón Filtrar (Gris claro)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF5F5F5),
                        modifier = Modifier.clickable { }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.List, contentDescription = null, tint = TextGray, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Filtrar", fontSize = 12.sp, color = TextGray, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Encabezados Tabla (Fondo gris muy suave)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFAFAFA))
                        .padding(vertical = 10.dp, horizontal = 4.dp)
                ) {
                    Text("ID", modifier = Modifier.weight(1f), color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("CONCEPTO", modifier = Modifier.weight(2f), color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("MÉTODO", modifier = Modifier.weight(1f), color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Filas de transacciones
                // Colores para las etiquetas: Fondo y Texto
                val purpleBg = Color(0xFFF3E5F5); val purpleText = Color(0xFF7B1FA2) // Yape
                val greenBg = Color(0xFFE8F5E9); val greenText = Color(0xFF2E7D32)  // Efectivo
                val blueBg = Color(0xFFE3F2FD); val blueText = Color(0xFF1565C0)   // Tarjeta

                TransactionRow("TX-9921", "Reserva Hab. 202", "Yape", purpleBg, purpleText)
                TransactionRow("TX-9922", "Consumo Restaurante", "Efectivo", greenBg, greenText)
                TransactionRow("TX-9923", "Lavandería", "Tarjeta", blueBg, blueText)
                TransactionRow("TX-9924", "Late Check-out", "Yape", purpleBg, purpleText)
                TransactionRow("TX-9925", "Frigobar", "Efectivo", greenBg, greenText)

                Spacer(modifier = Modifier.height(20.dp))

                // Footer "Ver historial completo"
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                    Text(
                        text = "Ver historial completo",
                        color = OrangePrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { }
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionRow(id: String, concepto: String, metodo: String, bgMethod: Color, textMethod: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(id, modifier = Modifier.weight(1f), color = TextBlack, fontSize = 13.sp)
        Text(concepto, modifier = Modifier.weight(2f), color = TextBlack, fontSize = 13.sp)

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterEnd
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(bgMethod)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(metodo, color = textMethod, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}