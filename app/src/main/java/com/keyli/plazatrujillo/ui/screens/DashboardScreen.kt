//DashboardScreen.kt

package com.keyli.plazatrujillo.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.keyli.plazatrujillo.ui.theme.*

// --- COLORES ESPECÍFICOS ---
val ChartBarOrange = Color(0xFFFF9800)
val PiePurple = Color(0xFF673AB7)
val PieGreen = Color(0xFF4CAF50)
val PieBlue = Color(0xFF2196F3)
val PieOrange = Color(0xFFFF5722)

@Composable
fun DashboardScreen(
    navController: NavController,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val scrollState = rememberScrollState()

    // Estados para los menús
    var showProfileMenu by remember { mutableStateOf(false) }
    var showNotifications by remember { mutableStateOf(false) }

    val backgroundColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onBackground
    val subTextColor = if (isDarkTheme) Color.Gray else Color.DarkGray
    val menuBgColor = if (isDarkTheme) Color(0xFF2C2C2C) else Color.White
    val cardBgColor = if (isDarkTheme) Color(0xFF1E1E1E) else Color.White

    Box(modifier = Modifier.fillMaxSize()) {

        // 1. CONTENIDO PRINCIPAL
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            // CABECERA
            DashboardHeader(
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme,
                textColor = textColor,
                subTextColor = subTextColor,
                onProfileClick = { showProfileMenu = true },
                onNotificationClick = { showNotifications = true }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // KPIs
            KpiSection(isDarkTheme)

            Spacer(modifier = Modifier.height(24.dp))
            MonthlyIncomeChart(cardBgColor, textColor)

            Spacer(modifier = Modifier.height(24.dp))
            IncomeVsExpensesChart(cardBgColor, textColor)

            Spacer(modifier = Modifier.height(24.dp))
            PaymentMethodChart(cardBgColor, textColor)

            Spacer(modifier = Modifier.height(24.dp))

            // --- NUEVO: GRÁFICO OCUPACIÓN SEMANAL ---
            OccupancyChart(cardBgColor, textColor)

            Spacer(modifier = Modifier.height(24.dp))

            // --- NUEVO: CHECK-IN DE HOY ---
            TodayCheckIns(cardBgColor, textColor, isDarkTheme)

            Spacer(modifier = Modifier.height(24.dp))

            // --- NUEVO: RESERVAS RECIENTES ---
            RecentReservations(cardBgColor, textColor, isDarkTheme)

            Spacer(modifier = Modifier.height(80.dp))
        }

        // 2. MENÚS FLOTANTES (Perfil y Notificaciones)
        // (Mismo código de menús flotantes que ya funcionaba)
        if (showProfileMenu) {
            Box(modifier = Modifier.fillMaxSize().clickable { showProfileMenu = false }) {
                Box(modifier = Modifier.align(Alignment.TopEnd).padding(top = 50.dp, end = 8.dp)) {
                    MaterialTheme(shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(12.dp))) {
                        DropdownMenu(expanded = true, onDismissRequest = { showProfileMenu = false }, modifier = Modifier.background(menuBgColor).width(240.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Administrador", fontSize = 12.sp, color = Color.Gray)
                                Text("mcastro@gmail.com", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = textColor)
                            }
                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                            DropdownMenuItem(text = { Text("Editar Perfil", color = textColor) }, leadingIcon = { Icon(Icons.Default.Edit, null, tint = textColor) }, onClick = { showProfileMenu = false; navController.navigate("profile") })
                            DropdownMenuItem(text = { Text("Configuración", color = textColor) }, leadingIcon = { Icon(Icons.Default.Settings, null, tint = textColor) }, onClick = { showProfileMenu = false })
                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                            DropdownMenuItem(text = { Text("Cerrar Sesión", color = StatusRed, fontWeight = FontWeight.Bold) }, leadingIcon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, null, tint = StatusRed) }, onClick = { showProfileMenu = false; navController.navigate("login") { popUpTo(0) } })
                        }
                    }
                }
            }
        }
        if (showNotifications) {
            Box(modifier = Modifier.fillMaxSize().clickable { showNotifications = false }) {
                Box(modifier = Modifier.align(Alignment.TopEnd).padding(top = 50.dp, end = 50.dp)) {
                    MaterialTheme(shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(12.dp))) {
                        DropdownMenu(expanded = true, onDismissRequest = { showNotifications = false }, modifier = Modifier.width(320.dp).background(menuBgColor)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Notificaciones", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textColor)
                                Spacer(modifier = Modifier.height(12.dp))
                                val itemTextColor = if(isDarkTheme) Color.LightGray else Color.DarkGray
                                NotificationItem("Terry Francis", "5 min ago", OrangePrimary, itemTextColor)
                                NotificationItem("Alena Francis", "8 min ago", OrangePrimary, itemTextColor)
                                NotificationItem("Jocelyn Kenter", "15 min ago", OrangePrimary, itemTextColor)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Ver todas las notificaciones", color = OrangePrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable{ showNotifications = false }.align(Alignment.CenterHorizontally))
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- COMPONENTES DE GRÁFICOS Y LISTAS ---

// 1. TASA DE OCUPACIÓN SEMANAL (Diseño exacto de imagen)
@Composable
fun OccupancyChart(cardBg: Color, text: Color) {
    Card(
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Tasa de Ocupación Semanal", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = text)
            Text("Ocupación diaria de la última semana", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp, bottom = 20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                // Datos exactos de la imagen (Porcentaje)
                val data = listOf(
                    Pair("Lun", 65), Pair("Mar", 70), Pair("Mié", 68),
                    Pair("Jue", 75), Pair("Vie", 80), Pair("Sáb", 85)
                )
                val maxVal = 100f // Escala sobre 100%

                data.forEach { (day, value) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Etiqueta de valor encima (ej: 65%)
                        Text(
                            text = "$value%",
                            fontSize = 10.sp,
                            color = text,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        // Barra Naranja Delgada
                        Box(
                            modifier = Modifier
                                .width(12.dp)
                                .weight(1f, false)
                                .fillMaxHeight(value / maxVal)
                                .background(Color(0xFFFF7043), RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                        )

                        // Línea base gris
                        HorizontalDivider(
                            modifier = Modifier.width(24.dp).padding(top = 4.dp),
                            thickness = 1.dp,
                            color = Color.LightGray.copy(alpha = 0.5f)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Día
                        Text(day, fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}

// 2. CHECK-IN DE HOY (Lista con iconos azules)
@Composable
fun TodayCheckIns(cardBg: Color, text: Color, dark: Boolean) {
    Card(
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Check-in de Hoy", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = text)
            Text("Huéspedes que ingresan hoy", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp, bottom = 20.dp))

            CheckInItem("14:00", "Juan Pérez", "Hab. 101", text)
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 12.dp))
            CheckInItem("15:30", "María López", "Hab. 205", text)
        }
    }
}

@Composable
fun CheckInItem(time: String, name: String, room: String, textColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icono de Cama Azul
        Icon(
            Icons.Default.Hotel,
            contentDescription = null,
            tint = Color(0xFF2196F3), // Azul claro
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))

        // Hora y Nombre
        Column(modifier = Modifier.weight(1f)) {
            Text(time, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = textColor)
            Text(name, color = Color.Gray, fontSize = 14.sp)
        }

        // Habitación a la derecha
        Text(room, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = textColor)
    }
}

// 3. RESERVAS RECIENTES (Lista con estados de color)
@Composable
fun RecentReservations(cardBg: Color, text: Color, dark: Boolean) {
    Card(
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Reservas Recientes", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = text)
                    Text("Últimas 5 reservas confirmadas", color = Color.Gray, fontSize = 12.sp)
                }
                Icon(Icons.Default.MoreVert, contentDescription = null, tint = Color.Black)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Lista de Reservas
            ReservationItem("Ana Torres", "Reserva #1056", "20/11/2025", "Confirmada", StatusGreen, text)
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 12.dp))

            ReservationItem("Luis Vidal", "Reserva #1055", "19/11/2025", "Confirmada", StatusGreen, text)
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 12.dp))

            ReservationItem("Carla Soto", "Reserva #1054", "19/11/2025", "Pendiente", Color(0xFFFFC107), text)
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 12.dp))

            ReservationItem("Pedro Díaz", "Reserva #1053", "18/11/2025", "Cancelada", StatusRed, text)
        }
    }
}

@Composable
fun ReservationItem(name: String, id: String, date: String, status: String, statusColor: Color, textColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(id, fontSize = 12.sp, color = Color.Gray)
            Text(name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = textColor)
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(date, fontSize = 12.sp, color = Color.Gray)
            Text(
                text = status,
                color = statusColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// --- RESTO DE COMPONENTES BASE (DashboardHeader, KpiSection, Gráficos anteriores) ---
// Mantén el código de estas funciones que ya tenías en el archivo anterior.
// (KpiSection, MonthlyIncomeChart, IncomeVsExpensesChart, PaymentMethodChart, IconBox, NotificationItem...)
// Si las borraste, avísame y te pego el archivo completo de 500 líneas.

@Composable fun DashboardHeader(isDarkTheme: Boolean, onToggleTheme: () -> Unit, textColor: Color, subTextColor: Color, onProfileClick: () -> Unit, onNotificationClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) { Text(text = "Hola, Marco", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = textColor); Text(text = "Aquí tienes el resumen de hoy", fontSize = 14.sp, color = subTextColor) }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { IconBox(if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode, onToggleTheme, isDarkTheme); IconBox(Icons.Default.Notifications, onNotificationClick, isDarkTheme); IconBox(Icons.Default.Person, onProfileClick, isDarkTheme) }
    }
}
@Composable fun IconBox(icon: ImageVector, onClick: () -> Unit, isDarkTheme: Boolean) { Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(if (isDarkTheme) Color(0xFF333333) else Color.White).clickable(onClick = onClick).padding(8.dp), contentAlignment = Alignment.Center) { Icon(icon, contentDescription = null, tint = if (isDarkTheme) Color.White else Color.Black) } }
@Composable fun NotificationItem(name: String, time: String, highlightColor: Color, textColor: Color){ Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) { Box(modifier = Modifier.size(36.dp).background(highlightColor.copy(alpha = 0.2f), CircleShape), contentAlignment = Alignment.Center) { Icon(Icons.Default.Person, null, tint = highlightColor, modifier = Modifier.size(20.dp)) } ; Spacer(modifier = Modifier.width(12.dp)) ; Column { val text = buildAnnotatedString { withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = textColor)) { append(name) } ; withStyle(style = SpanStyle(color = Color.Gray, fontSize = 12.sp)) { append(" requests permission to change ") } ; withStyle(style = SpanStyle(color = OrangePrimary, fontWeight = FontWeight.Medium, fontSize = 12.sp)) { append("Project - Nganter App") } } ; Text(text = text, fontSize = 13.sp, lineHeight = 18.sp) ; Text(time, fontSize = 11.sp, color = Color.LightGray) } } }
@Composable fun KpiSection(isDarkTheme: Boolean) { val cardBg = if (isDarkTheme) Color(0xFF1E1E1E) else Color.White; val textColor = if (isDarkTheme) Color.White else Color.Black; Column(verticalArrangement = Arrangement.spacedBy(16.dp)) { KpiCard("Ingresos Mensuales", "S/ 45,280", "12.5%", true, Icons.Default.AttachMoney, Color(0xFFFFF3E0), OrangePrimary, cardBg, textColor); KpiCard("Ingresos Totales", "S/ 385,290", "8.3%", true, Icons.Default.GridView, Color(0xFFFFF8E1), Color(0xFFFFC107), cardBg, textColor); KpiCard("Tasa de Ocupación", "78.5%", "5.2%", true, Icons.Default.Home, Color(0xFFE8F5E9), StatusGreen, cardBg, textColor); KpiCard("ADR Promedio", "S/ 245", "4.8%", true, Icons.Default.Calculate, Color(0xFFEFEBE9), Color(0xFF795548), cardBg, textColor) } }
@Composable fun KpiCard(title: String, value: String, pct: String, isUp: Boolean, icon: ImageVector, bgIcon: Color, colorIcon: Color, cardBg: Color, textColor: Color) { Card(colors = CardDefaults.cardColors(containerColor = cardBg), shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(2.dp), modifier = Modifier.fillMaxWidth()) { Row(modifier = Modifier.padding(20.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) { Row(verticalAlignment = Alignment.CenterVertically) { Box(modifier = Modifier.size(48.dp).background(bgIcon, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) { Icon(icon, null, tint = colorIcon, modifier = Modifier.size(24.dp)) }; Spacer(modifier = Modifier.width(16.dp)); Column { Text(title, color = Color.Gray, fontSize = 12.sp); Text(value, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = textColor) } }; Box(modifier = Modifier.background(if (isUp) StatusGreen.copy(0.1f) else StatusRed.copy(0.1f), RoundedCornerShape(4.dp)).padding(6.dp, 2.dp)) { Text((if (isUp) "↑ " else "↓ ") + pct, color = if (isUp) StatusGreen else StatusRed, fontSize = 12.sp, fontWeight = FontWeight.Bold) } } } }
@Composable fun MonthlyIncomeChart(cardBg: Color, text: Color) { Card(colors = CardDefaults.cardColors(containerColor = cardBg), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) { Column(modifier = Modifier.padding(20.dp)) { Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Ingresos Mensuales", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = text); Icon(Icons.Default.MoreVert, null, tint = Color.Gray) }; Spacer(modifier = Modifier.height(20.dp)); Row(modifier = Modifier.fillMaxWidth().height(200.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) { val data = listOf(Triple("Ene",34000,"34000"), Triple("Feb",41000,"41000"), Triple("Mar",39000,"39000"), Triple("Abr",43000,"43000"), Triple("May",40000,"40000"), Triple("Jun",42000,"42000")); data.forEach { (m,v,l) -> Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) { Text(l, fontSize = 10.sp, color = text, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom=4.dp)); Box(modifier = Modifier.width(12.dp).weight(1f, false).fillMaxHeight(v/45000f).background(Color(0xFFFF7043), RoundedCornerShape(topStart=4.dp, topEnd=4.dp))); Spacer(modifier = Modifier.height(8.dp)); Text(m, fontSize = 12.sp, color = Color.Gray); HorizontalDivider(modifier = Modifier.width(24.dp).padding(top=4.dp), thickness = 2.dp, color = Color.LightGray.copy(0.3f)) } } } } } }
@Composable fun IncomeVsExpensesChart(cardBg: Color, text: Color) { Card(colors = CardDefaults.cardColors(containerColor = cardBg), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) { Column(modifier = Modifier.padding(20.dp)) { Text("Ingresos vs Gastos", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = text); Text("Comparación mensual de ingresos y gastos operativos", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)); Row(modifier = Modifier.fillMaxWidth().height(44.dp).background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp)).padding(4.dp), horizontalArrangement = Arrangement.SpaceBetween) { Box(modifier = Modifier.weight(1f).fillMaxHeight().background(Color(0xFFFF7043), RoundedCornerShape(6.dp)), contentAlignment = Alignment.Center) { Text("Mensual", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp) }; Box(modifier = Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) { Text("Quincenal", color = Color.Black, fontWeight = FontWeight.Medium, fontSize = 12.sp) }; Box(modifier = Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) { Text("Anual", color = Color.Black, fontWeight = FontWeight.Medium, fontSize = 12.sp) } }; Spacer(modifier = Modifier.height(16.dp)); Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) { LegendItem("Ingresos", Color(0xFFFF7043)); Spacer(modifier = Modifier.width(24.dp)); LegendItem("Gastos", StatusRed) }; Spacer(modifier = Modifier.height(20.dp)); Box(modifier = Modifier.fillMaxWidth().height(150.dp)) { Canvas(modifier = Modifier.fillMaxSize()) { val width = size.width; val height = size.height; val gridColor = Color.LightGray.copy(alpha = 0.3f); drawLine(gridColor, start = Offset(0f, 0f), end = Offset(width, 0f), strokeWidth = 2f); drawLine(gridColor, start = Offset(0f, height * 0.5f), end = Offset(width, height * 0.5f), strokeWidth = 2f); drawLine(gridColor, start = Offset(0f, height), end = Offset(width, height), strokeWidth = 2f); val path1 = Path().apply { moveTo(0f, height * 0.7f); cubicTo(width * 0.15f, height * 0.7f, width * 0.15f, height * 0.5f, width * 0.25f, height * 0.5f); cubicTo(width * 0.35f, height * 0.5f, width * 0.35f, height * 0.65f, width * 0.5f, height * 0.65f); cubicTo(width * 0.65f, height * 0.65f, width * 0.65f, height * 0.35f, width * 0.75f, height * 0.35f); cubicTo(width * 0.85f, height * 0.35f, width * 0.85f, height * 0.55f, width, height * 0.55f) }; val path2 = Path().apply { moveTo(0f, height * 0.9f); cubicTo(width * 0.15f, height * 0.9f, width * 0.15f, height * 0.85f, width * 0.25f, height * 0.85f); cubicTo(width * 0.35f, height * 0.85f, width * 0.35f, height * 0.95f, width * 0.5f, height * 0.95f); cubicTo(width * 0.65f, height * 0.95f, width * 0.65f, height * 0.8f, width * 0.75f, height * 0.8f); cubicTo(width * 0.85f, height * 0.8f, width * 0.85f, height * 0.9f, width, height * 0.9f) }; drawPath(path1, Color(0xFFFF7043), style = Stroke(width = 6f, cap = StrokeCap.Round)); drawPath(path2, StatusRed, style = Stroke(width = 6f, cap = StrokeCap.Round)); val p1 = listOf(Offset(0f, 0.7f), Offset(0.25f, 0.5f), Offset(0.5f, 0.65f), Offset(0.75f, 0.35f), Offset(1f, 0.55f)); val p2 = listOf(Offset(0f, 0.9f), Offset(0.25f, 0.85f), Offset(0.5f, 0.95f), Offset(0.75f, 0.8f), Offset(1f, 0.9f)); p1.forEach { drawCircle(Color(0xFFFF7043), 8f, Offset(it.x * width, it.y * height)) }; p2.forEach { drawCircle(StatusRed, 8f, Offset(it.x * width, it.y * height)) } }; Spacer(modifier = Modifier.height(8.dp)); Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { listOf("Ene", "Feb", "Mar", "Abr", "May").forEach { Text(it, fontSize = 12.sp, color = Color.Gray) } } } } } }
@Composable fun LegendItem(text: String, color: Color) { Row(verticalAlignment = Alignment.CenterVertically) { Box(modifier = Modifier.size(8.dp).background(color, CircleShape)); Spacer(modifier = Modifier.width(4.dp)); Text(text, fontSize = 12.sp, color = Color.Gray) } }
@Composable fun PaymentMethodChart(cardBg: Color, text: Color) { Card(colors = CardDefaults.cardColors(containerColor = cardBg), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) { Column(modifier = Modifier.padding(20.dp)) { Text("Ingresos por Método de Pago", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = text); Text("Distribución de pagos del último mes", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)); Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) { Box(modifier = Modifier.size(140.dp), contentAlignment = Alignment.Center) { Canvas(modifier = Modifier.size(140.dp)) { val strokeWidth = 35f; drawArc(color = PiePurple, startAngle = -90f, sweepAngle = 162f, useCenter = false, style = Stroke(width = strokeWidth)); drawArc(color = PieGreen, startAngle = 72f, sweepAngle = 108f, useCenter = false, style = Stroke(width = strokeWidth)); drawArc(color = PieBlue, startAngle = 180f, sweepAngle = 54f, useCenter = false, style = Stroke(width = strokeWidth)); drawArc(color = PieOrange, startAngle = 234f, sweepAngle = 36f, useCenter = false, style = Stroke(width = strokeWidth)) }; Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("S/ 43.000", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = text); Text("Total", fontSize = 12.sp, color = Color.Gray) } }; Spacer(modifier = Modifier.width(16.dp)); Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) { PaymentLegend("Efectivo", "S/ 20,376", PiePurple, text); PaymentLegend("Tarjeta", "S/ 13,584", PieGreen, text); PaymentLegend("Yape", "S/ 4,792", PieBlue, text); PaymentLegend("Transferencia", "S/ 4,248", PieOrange, text) } } } } }
@Composable fun PaymentLegend(method: String, amount: String, color: Color, textColor: Color) { Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) { Box(modifier = Modifier.size(12.dp).padding(top = 2.dp).background(color, RoundedCornerShape(2.dp))); Spacer(modifier = Modifier.width(8.dp)); Column { Text(text = method, fontSize = 12.sp, color = Color.Gray); Text(text = amount, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textColor) } } }