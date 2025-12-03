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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.keyli.plazatrujillo.ui.theme.*
import com.keyli.plazatrujillo.ui.viewmodel.DashboardViewModel
import com.keyli.plazatrujillo.data.model.*
import java.text.DecimalFormat

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
    onToggleTheme: () -> Unit,
    showHeaderActions: Boolean // nuevo parámetro: controla si se muestran los iconos del header
) {
    // ViewModel para obtener datos de la API
    val viewModel: DashboardViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Cargar datos cuando se inicia la pantalla
    LaunchedEffect(Unit) {
        viewModel.loadDashboardData()
    }

    // Manejar errores
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(
                message = error,
                duration = SnackbarDuration.Long
            )
            viewModel.clearError()
        }
    }

    val scrollState = rememberScrollState()

    // Estados para los menús
    var showProfileMenu by remember { mutableStateOf(false) }
    var showNotifications by remember { mutableStateOf(false) }

    val backgroundColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onBackground
    val subTextColor = if (isDarkTheme) Color.Gray else Color.DarkGray
    val menuBgColor = if (isDarkTheme) Color(0xFF2C2C2C) else Color.White
    val cardBgColor = if (isDarkTheme) Color(0xFF1E1E1E) else Color.White

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (!uiState.isLoading) {
                FloatingActionButton(
                    onClick = { viewModel.refresh() },
                    containerColor = OrangePrimary
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refrescar")
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            // 1. CONTENIDO PRINCIPAL
            if (uiState.isLoading && uiState.metrics == null) {
                // Mostrar indicador de carga inicial
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = OrangePrimary
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(backgroundColor)
                        .padding()
                        .padding(16.dp)
                        .verticalScroll(scrollState)
                ) {
                    // CABECERA
                    // Paso el flag showHeaderActions a DashboardHeader para ocultar/mostrar los iconos
                    DashboardHeader(
                        isDarkTheme = isDarkTheme,
                        onToggleTheme = onToggleTheme,
                        textColor = textColor,
                        subTextColor = subTextColor,
                        onProfileClick = { showProfileMenu = true },
                        onNotificationClick = { showNotifications = true },
                        showActions = showHeaderActions
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // KPIs con datos reales
                    KpiSection(
                        isDarkTheme = isDarkTheme,
                        metrics = uiState.metrics
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Gráfico de ingresos mensuales con datos reales
                    MonthlyIncomeChart(
                        cardBgColor,
                        textColor,
                        monthlyRevenue = uiState.monthlyRevenue
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Gráfico de ingresos anuales con datos reales
                    IncomeVsExpensesChart(
                        cardBgColor,
                        textColor,
                        statistics = uiState.statistics
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Métodos de pago con datos reales
                    PaymentMethodChart(
                        cardBgColor,
                        textColor,
                        paymentMethods = uiState.paymentMethods
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Gráfico de ocupación semanal con datos reales
                    OccupancyChart(
                        cardBgColor,
                        textColor,
                        occupancyData = uiState.occupancyWeekly
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Check-ins de hoy con datos reales
                    TodayCheckIns(
                        cardBgColor,
                        textColor,
                        isDarkTheme,
                        checkins = uiState.todayCheckinsCheckouts?.checkins
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Check-outs de hoy con datos reales
                    TodayCheckOuts(
                        cardBgColor,
                        textColor,
                        isDarkTheme,
                        checkouts = uiState.todayCheckinsCheckouts?.checkouts
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Reservas recientes con datos reales
                    RecentReservations(
                        cardBgColor,
                        textColor,
                        isDarkTheme,
                        reservations = uiState.recentReservations
                    )

                    Spacer(modifier = Modifier.height(80.dp))
                }
            }

            // Indicador de carga cuando se está refrescando
            if (uiState.isLoading && uiState.metrics != null) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter),
                    color = OrangePrimary
                )
            }
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
fun OccupancyChart(
    cardBg: Color,
    text: Color,
    occupancyData: List<Double> = emptyList()
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Tasa de Ocupación Semanal", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = text)
            Text("Ocupación diaria de la última semana", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp, bottom = 20.dp))

            // Contenedor principal del gráfico (define la altura total)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp) // Aumentamos la altura para más espacio vertical
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom // Todo se alinea a la base
            ) {
                // Usar datos reales si están disponibles
                val days = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
                val data = if (occupancyData.isNotEmpty()) {
                    occupancyData.take(7).mapIndexed { index, value ->
                        Pair(days.getOrNull(index) ?: "", value.toInt())
                    }
                } else {
                    listOf(
                        Pair("Lun", 65), Pair("Mar", 70), Pair("Mié", 68),
                        Pair("Jue", 75), Pair("Vie", 80), Pair("Sáb", 85),
                        Pair("Dom", 72)
                    )
                }
                val maxVal = 100f // Escala sobre 100%

                data.forEach { (day, value) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f) // Distribuye el espacio horizontalmente
                            .fillMaxHeight() // Permite que el contenido interno (la barra) use la altura completa
                            .clickable { /* Opcional: para interactividad */ }
                    ) {
                        // 1. Etiqueta de valor (ej: 65%)
                        Text(
                            text = "$value%",
                            fontSize = 10.sp,
                            color = text,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        // 2. Separador entre etiqueta y barra
                        Spacer(modifier = Modifier.height(2.dp))

                        // Contenedor para la barra, forzado a ocupar el espacio restante
                        Box(
                            modifier = Modifier
                                .weight(1f) // Ocupa todo el espacio vertical disponible
                                .fillMaxWidth(),
                            contentAlignment = Alignment.BottomCenter // Alinea la barra en la parte inferior de su espacio
                        ) {
                            // Línea base gris (eje Y 0, simulado)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(Color.LightGray.copy(alpha = 0.3f))
                            )

                            // Barra Naranja Delgada
                            Box(
                                modifier = Modifier
                                    .width(16.dp) // Barra un poco más ancha
                                    // NO usamos weight(1f), SÍ usamos fillMaxHeight(value/maxVal)
                                    .fillMaxHeight(value / maxVal) // Altura de la barra según el porcentaje
                                    .background(Color(0xFFFF7043), RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            )
                        }


                        // 3. Espaciador entre barra y día
                        Spacer(modifier = Modifier.height(8.dp))

                        // 4. Día
                        Text(day, fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}

// 2. CHECK-IN DE HOY (Lista con iconos azules)
@Composable
fun TodayCheckIns(
    cardBg: Color,
    text: Color,
    dark: Boolean,
    checkins: List<CheckinCheckoutItem>? = null
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Check-in de Hoy", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = text)
            Text("Huéspedes programados para llegar hoy", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp, bottom = 20.dp))

            if (checkins.isNullOrEmpty()) {
                CheckInItem("14:00", "Juan Pérez", "Hab. 101", text)
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 12.dp))
                CheckInItem("15:30", "María López", "Hab. 205", text)
            } else {
                checkins.forEachIndexed { index, checkin ->
                    if (index > 0) {
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 12.dp))
                    }
                    CheckInItem(
                        checkin.time ?: "",
                        checkin.name ?: "",
                        checkin.room ?: "",
                        text
                    )
                }
            }
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

// --- AGREGADO: CHECK-OUT DE HOY ---
@Composable
fun TodayCheckOuts(
    cardBg: Color,
    text: Color,
    dark: Boolean,
    checkouts: List<CheckinCheckoutItem>? = null
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Check-out de Hoy", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = text)
            Text("Huéspedes programados para salir hoy", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp, bottom = 20.dp))

            if (checkouts.isNullOrEmpty()) {
                CheckOutItem("11:00", "Carlos Ruiz", "Hab. 302", text)
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 12.dp))
                CheckOutItem("12:00", "Elena Gomez", "Hab. 104", text)
            } else {
                checkouts.forEachIndexed { index, checkout ->
                    if (index > 0) {
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 12.dp))
                    }
                    CheckOutItem(
                        checkout.time ?: "",
                        checkout.name ?: "",
                        checkout.room ?: "",
                        text
                    )
                }
            }
        }
    }
}

@Composable
fun CheckOutItem(time: String, name: String, room: String, textColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icono de Salida (Naranja/Rojo)
        Icon(
            Icons.AutoMirrored.Filled.ExitToApp,
            contentDescription = null,
            tint = PieOrange, // Naranja para diferenciar de check-in
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
fun RecentReservations(
    cardBg: Color,
    text: Color,
    dark: Boolean,
    reservations: List<RecentReservationItem> = emptyList()
) {
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
                Icon(Icons.Default.MoreVert, contentDescription = null, tint = if (dark) Color.White else Color.Black)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Lista de Reservas
            if (reservations.isEmpty()) {
                ReservationItem("Ana Torres", "Reserva #1056", "20/11/2025", "Confirmada", StatusGreen, text)
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 12.dp))
                ReservationItem("Luis Vidal", "Reserva #1055", "19/11/2025", "Confirmada", StatusGreen, text)
            } else {
                reservations.take(5).forEachIndexed { index, reservation ->
                    if (index > 0) {
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 12.dp))
                    }
                    val statusColor = when (reservation.status?.lowercase()) {
                        "confirmada", "check-in" -> StatusGreen
                        "pendiente" -> Color(0xFFFFC107)
                        "cancelada", "check-out" -> StatusRed
                        else -> Color.Gray
                    }
                    ReservationItem(
                        reservation.guestName ?: "",
                        "Reserva #${reservation.id ?: ""}",
                        reservation.checkIn ?: "",
                        reservation.status ?: "",
                        statusColor,
                        text
                    )
                }
            }
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

private fun formatCurrency(value: Int): String {
    val df = DecimalFormat("#,###")
    return "S/ ${df.format(value)}"
}

// Helper to format percentage like "42%"
private fun formatPct(value: Float): String {
    val v = (value * 100).toInt()
    return "$v%"
}

// --- RESTO DE COMPONENTES BASE (DashboardHeader, KpiSection, Gráficos anteriores) ---
// Mantén el código de estas funciones que ya tenías en el archivo anterior.
// (KpiSection, MonthlyIncomeChart, IncomeVsExpensesChart, PaymentMethodChart, IconBox, NotificationItem...)
// Si las borraste, avísame y te pego el archivo completo de 500 líneas.
@Composable
fun DashboardHeader(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    textColor: Color,
    subTextColor: Color,
    onProfileClick: () -> Unit,
    onNotificationClick: () -> Unit,
    showActions: Boolean // nuevo parámetro
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Hola, Marco",
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

        // Mantener siempre el mismo lugar/espacio para los iconos.
        // Si showActions == true mostramos los IconBox interactivos,
        // si es false mostramos placeholders invisibles del mismo tamaño para que la posición no cambie.
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (showActions) {
                IconBox(if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode, onToggleTheme, isDarkTheme)
                IconBox(Icons.Default.Notifications, onNotificationClick, isDarkTheme)
                IconBox(Icons.Default.Person, onProfileClick, isDarkTheme)
            } else {
                // Placeholders con el mismo tamaño para conservar layout sin interacción
                Box(modifier = Modifier.size(40.dp).clip(CircleShape))
                Box(modifier = Modifier.size(40.dp).clip(CircleShape))
                Box(modifier = Modifier.size(40.dp).clip(CircleShape))
            }
        }
    }
}

@Composable
fun IconBox(icon: ImageVector, onClick: () -> Unit, isDarkTheme: Boolean) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(if (isDarkTheme) Color(0xFF333333) else Color.White)
            .clickable(onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = if (isDarkTheme) Color.White else Color.Black)
    }
}

@Composable
fun NotificationItem(name: String, time: String, highlightColor: Color, textColor: Color) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(highlightColor.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, null, tint = highlightColor, modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            val text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = textColor)) {
                    append(name)
                }
                withStyle(style = SpanStyle(color = Color.Gray, fontSize = 12.sp)) {
                    append(" requests permission to change ")
                }
                withStyle(style = SpanStyle(color = OrangePrimary, fontWeight = FontWeight.Medium, fontSize = 12.sp)) {
                    append("Project - Nganter App")
                }
            }

            Text(text = text, fontSize = 13.sp, lineHeight = 18.sp)
            Text(time, fontSize = 11.sp, color = Color.LightGray)
        }
    }
}

/* --- KPI SECTION --- */
@Composable
fun KpiSection(
    isDarkTheme: Boolean,
    metrics: DashboardMetricsResponse? = null
) {
    val cardBg = if (isDarkTheme) DarkSurface else LightSurface
    val textColor = if (isDarkTheme) TextWhite else TextBlack

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        KpiCard(
            "Ingresos Mensuales",
            metrics?.monthlyRevenue?.amount?.let { formatCurrency(it.toInt()) } ?: "S/ 0",
            metrics?.monthlyRevenue?.changePercent?.let { "${if (it >= 0) "+" else ""}${String.format("%.1f", it)}%" } ?: "0%",
            (metrics?.monthlyRevenue?.changePercent ?: 0.0) >= 0,
            Icons.Default.AttachMoney,
            Color(0xFFFFF3E0),
            OrangePrimary,
            cardBg,
            textColor
        )
        KpiCard(
            "Ingresos Totales",
            metrics?.totalRevenue?.amount?.let { formatCurrency(it.toInt()) } ?: "S/ 0",
            metrics?.totalRevenue?.changePercent?.let { "${if (it >= 0) "+" else ""}${String.format("%.1f", it)}%" } ?: "0%",
            (metrics?.totalRevenue?.changePercent ?: 0.0) >= 0,
            Icons.Default.GridView,
            Color(0xFFFFF8E1),
            Color(0xFFFFC107),
            cardBg,
            textColor
        )
        KpiCard(
            "Tasa de Ocupación",
            metrics?.occupancyRate?.rate?.let { "${it.toInt()}%" } ?: "0%",
            metrics?.occupancyRate?.changePercent?.let { "${if (it >= 0) "+" else ""}${String.format("%.1f", it)}%" } ?: "0%",
            (metrics?.occupancyRate?.changePercent ?: 0.0) >= 0,
            Icons.Default.Home,
            Color(0xFFE8F5E9),
            StatusGreen,
            cardBg,
            textColor
        )
        KpiCard(
            "ADR Promedio",
            metrics?.adr?.amount?.let { formatCurrency(it.toInt()) } ?: "S/ 0",
            metrics?.adr?.changePercent?.let { "${if (it >= 0) "+" else ""}${String.format("%.1f", it)}%" } ?: "0%",
            (metrics?.adr?.changePercent ?: 0.0) >= 0,
            Icons.Default.Calculate,
            Color(0xFFEFEBE9),
            Color(0xFF795548),
            cardBg,
            textColor
        )
    }
}

@Composable
fun KpiCard(
    title: String,
    value: String,
    pct: String,
    isUp: Boolean,
    icon: ImageVector,
    bgIcon: Color,
    colorIcon: Color,
    cardBg: Color,
    textColor: Color
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Icono y Título/Valor
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(bgIcon, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = colorIcon, modifier = Modifier.size(32.dp))
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(title, color = Color.Gray, fontSize = 13.sp)
                    Text(
                        value,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp,
                        color = textColor
                    )
                }
            }

            // 2. Indicador de Porcentaje
            Box(
                modifier = Modifier
                    .background(
                        if (isUp) StatusGreen.copy(0.15f) else StatusRed.copy(0.15f),
                        RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = (if (isUp) "↑ " else "↓ ") + pct,
                    color = if (isUp) StatusGreen else StatusRed,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/* --- MonthlyIncomeChart --- */
@Composable
fun MonthlyIncomeChart(
    cardBg: Color,
    text: Color,
    monthlyRevenue: List<Double> = emptyList()
) {
    val months = listOf("Ene","Feb","Mar","Abr","May","Jun","Jul","Ago","Set","Oct","Nov","Dic")
    // Usar datos reales si están disponibles, sino usar datos por defecto
    val values = if (monthlyRevenue.isNotEmpty()) {
        monthlyRevenue.take(12).map { it.toInt() }
    } else {
        listOf(26000, 28000, 30000, 34000, 36000, 32000, 38000, 41000, 39000, 43000, 40000, 42000)
    }

    var selected by remember { mutableStateOf(-1) }
    val max = (values.maxOrNull() ?: 1).toFloat()

    Card(
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Ingresos mensuales", fontWeight = FontWeight.Bold, color = text)
            Text("Últimos 12 meses", color = Color.Gray, fontSize = 12.sp)

            Spacer(Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                // Y axis labels
                Column(
                    modifier = Modifier.width(48.dp).padding(end = 8.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    val ticks = 4
                    for (i in 0..ticks) {
                        val valueAtTick = ((max / ticks) * (ticks - i)).toInt()
                        Text(formatCurrency(valueAtTick), fontSize = 11.sp, color = Color.Gray)
                        if (i < ticks) Spacer(modifier = Modifier.height(36.dp))
                    }
                }

                // Chart area - Gráfico de barras
                Box(modifier = Modifier.weight(1f)) {
                    // Contenedor principal del gráfico de barras
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        values.forEachIndexed { index, value ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clickable { selected = if (selected == index) -1 else index }
                            ) {
                                // Etiqueta de valor (opcional, se muestra al seleccionar)
                                if (selected == index) {
                                    Text(
                                        text = formatCurrency(value),
                                        fontSize = 10.sp,
                                        color = text,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                }

                                // Contenedor para la barra
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth(),
                                    contentAlignment = Alignment.BottomCenter
                                ) {
                                    // Línea base gris
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(1.dp)
                                            .background(Color.LightGray.copy(alpha = 0.3f))
                                    )

                                    // Barra naranja
                                    Box(
                                        modifier = Modifier
                                            .width(24.dp)
                                            .fillMaxHeight((value / max).coerceIn(0f, 1f))
                                            .background(OrangePrimary, RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    )
                                }

                                // Espaciador entre barra y mes
                                Spacer(modifier = Modifier.height(8.dp))

                                // Etiqueta del mes
                                Text(
                                    months.getOrNull(index) ?: "",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }

                    // Líneas de cuadrícula horizontales
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val grid = Color.LightGray.copy(alpha = 0.18f)
                        for (i in 0..4) {
                            val y = size.height * (i / 4f)
                            drawLine(grid, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
                        }
                    }
                }
            }
        }
    }
}

/* --- INGRESOS ANUALES (Modificado: Antes Ingresos vs Gastos) --- */
@Composable
fun IncomeVsExpensesChart(
    cardBg: Color,
    text: Color,
    statistics: StatisticsResponse? = null
) {
    // Usar datos reales del endpoint de statistics si están disponibles
    val labels = statistics?.labels?.take(12) ?: listOf("Ene","Feb","Mar","Abr","May","Jun","Jul","Ago","Set","Oct","Nov","Dic")
    val values = if (statistics?.income != null && statistics.income.isNotEmpty()) {
        statistics.income.take(12).map { it.toInt() }
    } else {
        listOf(320000, 340000, 360000, 350000, 380000, 400000, 410000, 420000, 435000, 450000, 460000, 480000)
    }

    val max = (values.maxOrNull() ?: 1).toFloat()
    var selected by remember { mutableStateOf(-1) }

    Card(
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            // Título cambiado
            Text("Ingresos Anuales", fontWeight = FontWeight.Bold, color = text)
            Text("Tendencia últimos 12 meses", color = Color.Gray, fontSize = 12.sp)

            Spacer(Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                // Eje Y (Etiquetas de valor)
                Column(
                    modifier = Modifier.width(56.dp).padding(end = 8.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    val ticks = 4
                    for (i in 0..ticks) {
                        val valueAtTick = ((max / ticks) * (ticks - i)).toInt()
                        Text(formatCurrency(valueAtTick), fontSize = 10.sp, color = Color.Gray)
                        if (i < ticks) Spacer(modifier = Modifier.height(36.dp))
                    }
                }

                // Área del Gráfico
                Box(modifier = Modifier.weight(1f)) {
                    Canvas(modifier = Modifier.height(220.dp).fillMaxWidth()) {
                        val w = size.width
                        val h = size.height

                        // Líneas de cuadrícula
                        val gridColor = Color.LightGray.copy(alpha = 0.18f)
                        for (i in 0..4) {
                            val y = h * (i / 4f)
                            drawLine(gridColor, Offset(0f, y), Offset(w, y), strokeWidth = 1f)
                        }

                        val step = w / (values.size - 1).coerceAtLeast(1)

                        // 1. Dibujar Área Rellena (Gradiente)
                        val areaPath = Path()
                        values.forEachIndexed { i, v ->
                            val x = i * step
                            val y = h - (v.toFloat() / max) * h
                            if (i == 0) areaPath.moveTo(x, y) else areaPath.lineTo(x, y)
                        }
                        areaPath.lineTo(w, h)
                        areaPath.lineTo(0f, h)
                        areaPath.close()

                        // Usamos un gradiente ligeramente diferente (más verdoso/dorado) para diferenciarlo del mensual
                        val gradientBrush = Brush.verticalGradient(
                            listOf(Color(0xFFFFB74D).copy(alpha = 0.25f), Color(0xFFFFB74D).copy(alpha = 0.05f))
                        )
                        drawPath(areaPath, gradientBrush, style = Fill)

                        // 2. Dibujar Línea y Puntos
                        val linePath = Path()
                        val lineColor = Color(0xFFFF9800) // Naranja fuerte

                        values.forEachIndexed { i, v ->
                            val x = i * step
                            val y = h - (v.toFloat() / max) * h
                            if (i == 0) linePath.moveTo(x, y) else linePath.lineTo(x, y)

                            // Puntos en cada mes
                            drawCircle(cardBg, radius = 5f, center = Offset(x, y)) // Borde del punto (color fondo)
                            drawCircle(lineColor, radius = 3f, center = Offset(x, y)) // Centro del punto
                        }
                        drawPath(linePath, lineColor, style = Stroke(width = 3f, cap = StrokeCap.Round))
                    }

                    // Etiquetas Eje X (Meses)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        labels.forEachIndexed { i, m ->
                            // Mostramos todos los meses de las etiquetas recibidas
                            Text(m, fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

/* --- LegendItem (Ya no se usa en el gráfico anual, pero se mantiene por si acaso) --- */
@Composable
fun LegendItem(text: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text, fontSize = 12.sp, color = Color.Gray)
    }
}

/* --- PaymentMethodChart: anillo (donut) con porcentajes y leyenda con % --- */
@Composable
fun PaymentMethodChart(
    cardBg: Color,
    text: Color,
    paymentMethods: Map<String, Double> = emptyMap()
) {
    val labels = listOf("Efectivo","Tarjeta","Yape","Transferencia")
    // Usar datos reales si están disponibles
    val values = if (paymentMethods.isNotEmpty()) {
        listOf(
            (paymentMethods["Efectivo"] ?: 0.0).toFloat(),
            (paymentMethods["Tarjeta"] ?: 0.0).toFloat(),
            (paymentMethods["Yape"] ?: 0.0).toFloat(),
            (paymentMethods["Transferencia"] ?: 0.0).toFloat()
        )
    } else {
        listOf(20376f,13584f,4792f,4248f)
    }
    val colors = listOf(PiePurple, PieGreen, PieBlue, PieOrange)
    val total = values.sum()
    val pct = if (total > 0) values.map { (it / total * 100f) } else listOf(0f, 0f, 0f, 0f)

    var selected by remember { mutableStateOf(-1) }

    Card(
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(20.dp)) {
            Text("Ingresos por método", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = text)
            Text("Distribución de pagos", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp, bottom = 24.dp))

            Box(
                modifier = Modifier.fillMaxWidth().height(180.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(160.dp)) {
                    var start = -90f
                    val baseStrokeWidth = 34f

                    values.forEachIndexed { i, v ->
                        val sweep = (v / total) * 360f
                        val isCurrentSelected = selected == i
                        val stroke = if (isCurrentSelected) baseStrokeWidth + 8f else baseStrokeWidth
                        val colorToUse = if (isCurrentSelected) OrangePrimary else colors[i]

                        drawArc(
                            color = colorToUse,
                            startAngle = start,
                            sweepAngle = sweep,
                            useCenter = false,
                            style = Stroke(width = stroke, cap = StrokeCap.Butt)
                        )
                        start += sweep
                    }

                    drawContext.canvas.nativeCanvas.apply {
                        val paintText = android.graphics.Paint().apply {
                            color = android.graphics.Color.DKGRAY
                            textSize = 30f
                            textAlign = android.graphics.Paint.Align.CENTER
                            isFakeBoldText = false
                        }
                        drawText("Total", size.width / 2, size.height / 2 - 8f, paintText)
                        drawText(
                            formatCurrency(total.toInt()),
                            size.width / 2,
                            size.height / 2 + 22f,
                            paintText.apply {
                                textSize = 32f
                                color = android.graphics.Color.BLACK
                                isFakeBoldText = true
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                maxItemsInEachRow = 2
            ) {
                labels.forEachIndexed { i, l ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f).clickable { selected = i }
                    ) {
                        val isCurrentSelected = selected == i
                        Box(Modifier.size(12.dp).background(colors[i], CircleShape))
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(l, fontSize = 12.sp, color = Color.Gray)
                            Text(
                                text = "${formatCurrency(values[i].toInt())} • ${pct[i].toInt()}%",
                                fontWeight = FontWeight.Bold,
                                color = if (isCurrentSelected) OrangePrimary else text,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentLegend(method: String, amount: String, color: Color, textColor: Color) {
    Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.size(12.dp).padding(top = 2.dp).background(color, RoundedCornerShape(2.dp)))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = method, fontSize = 12.sp, color = Color.Gray)
            Text(text = amount, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textColor)
        }
    }
}