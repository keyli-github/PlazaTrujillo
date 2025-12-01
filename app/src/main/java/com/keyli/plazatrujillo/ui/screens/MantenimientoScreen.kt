package com.keyli.plazatrujillo.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange // Usado como alternativa a calculadora
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

// --- COLORES DEFINIDOS LOCALMENTE ---
val LightBackground = Color(0xFFF8F9FA)
val LightSurface = Color(0xFFFFFFFF)
val TextBlack = Color(0xFF1F2937)
val TextGray = Color(0xFF6B7280)
val OrangePrimary = Color(0xFFFF6B00)
val OrangeSecondary = Color(0xFFFFA04D)
val StatusGreen = Color(0xFF10B981)
val StatusRed = Color(0xFFEF4444)

@Composable
fun MantenimientoScreen(navController: NavHostController) {
    // Estado para controlar la pestaña seleccionada
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Sistema Agua Caliente", "Historial Briquetas", "Incidencias", "Habitaciones Bloqueadas")

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = LightBackground
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // --- HEADER ---
            Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 8.dp)) {
                Text(
                    text = "Mantenimiento Técnico",
                    color = TextBlack,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Gestión del sistema de agua caliente y mantenimiento de habitaciones",
                    color = TextGray,
                    fontSize = 14.sp
                )
            }

            // --- TABS ---
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = LightBackground,
                contentColor = OrangePrimary,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    if (selectedTabIndex < tabPositions.size) {
                        TabRowDefaults.Indicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = OrangePrimary,
                            height = 3.dp
                        )
                    }
                },
                divider = { Divider(color = TextGray.copy(alpha = 0.1f)) }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTabIndex == index) OrangePrimary else TextGray
                            )
                        }
                    )
                }
            }

            // --- CONTENIDO ---
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                when (selectedTabIndex) {
                    0 -> TabAguaCaliente(navController)
                    1 -> TabHistorialBriquetas()
                    2 -> TabIncidencias()
                    3 -> TabHabitacionesBloqueadas()
                }
            }
        }
    }
}

// ------------------------------------------------------------------------
// TAB 1: SISTEMA AGUA CALIENTE (DISEÑO CUADRÍCULA 2x2)
// ------------------------------------------------------------------------
@Composable
fun TabAguaCaliente(navController: NavHostController) {
    Column(modifier = Modifier.fillMaxSize()) {

        // --- FILA 1 (Verde y Naranja) ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Tarjeta 1: Estado (Verde)
            StatusCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.CheckCircle,
                iconBgColor = Color(0xFFDCFCE7), // Verde pastel
                iconColor = Color(0xFF16A34A),   // Verde fuerte
                title = "Estado Operativo",
                value = "Operativo"
            )

            // Tarjeta 2: Briquetas (Naranja)
            StatusCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.DateRange, // Icono calendario/calculadora
                iconBgColor = Color(0xFFFFEDD5), // Naranja pastel
                iconColor = Color(0xFFEA580C),   // Naranja fuerte
                title = "Briquetas Este Mes",
                value = "0 Unid."
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- FILA 2 (Azul y Morado) ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Tarjeta 3: Último Cambio (Azul)
            StatusCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.AccessTime,
                iconBgColor = Color(0xFFDBEAFE), // Azul pastel
                iconColor = Color(0xFF2563EB),   // Azul fuerte
                title = "Último Cambio",
                value = "No registrado"
            )

            // Tarjeta 4: Próximo Cambio (Morado)
            StatusCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Schedule,
                iconBgColor = Color(0xFFF3E8FF), // Morado pastel
                iconColor = Color(0xFF9333EA),   // Morado fuerte
                title = "Próximo Cambio",
                value = "No programado"
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- BOTONES DE ACCIÓN ---
        Text(text = "Acciones Rápidas", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextBlack)
        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { navController.navigate("register_briquetas") },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White)
            Spacer(Modifier.width(8.dp))
            Text("Registrar Cambio Briquetas", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = { navController.navigate("bloq_habitacion") },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF475569)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text("Bloquear Hab.", fontSize = 14.sp)
            }

            Button(
                onClick = { navController.navigate("report_incidencias") },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StatusRed),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text("Reportar Incidencia", fontSize = 12.sp)
            }
        }
    }
}

// ------------------------------------------------------------------------
// STATUS CARD (COMPONENTE REUTILIZABLE CON ESTILO PROFESIONAL)
// ------------------------------------------------------------------------
@Composable
fun StatusCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconBgColor: Color, // Color de fondo pastel
    iconColor: Color,   // Color fuerte del icono
    title: String,
    value: String
) {
    Card(
        modifier = modifier.height(145.dp), // Altura fija uniforme
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = LightSurface),
        border = BorderStroke(1.dp, TextGray.copy(alpha = 0.15f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start // Alineado a la izquierda
        ) {
            // Icono con fondo cuadrado redondeado (Pastel)
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(iconBgColor, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Textos
            Column {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    color = TextGray,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827), // Negro oscuro
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// ------------------------------------------------------------------------
// TAB 2: HISTORIAL BRIQUETAS (TABLA)
// ------------------------------------------------------------------------
@Composable
fun TabHistorialBriquetas() {
    val headers = listOf("Fecha" to 0.4f, "Hora" to 0.3f, "Cantidad" to 0.3f)

    Column(modifier = Modifier.fillMaxSize()) {
        TableHeader(headers)
        EmptyStateMessage("No hay historial de cambios de briquetas")
    }
}

// ------------------------------------------------------------------------
// TAB 3: INCIDENCIAS (TABLA)
// ------------------------------------------------------------------------
@Composable
fun TabIncidencias() {
    val headers = listOf(
        "Hab." to 0.15f,
        "Problema" to 0.4f,
        "Prio." to 0.2f,
        "Fecha" to 0.25f
    )

    Column(modifier = Modifier.fillMaxSize()) {
        TableHeader(headers)
        EmptyStateMessage("No hay incidencias registradas")
    }
}

// ------------------------------------------------------------------------
// TAB 4: HABITACIONES BLOQUEADAS (TABLA)
// ------------------------------------------------------------------------
@Composable
fun TabHabitacionesBloqueadas() {
    val headers = listOf(
        "Hab." to 0.15f,
        "Razón Bloqueo" to 0.35f,
        "Hasta" to 0.25f,
        "Por" to 0.25f
    )

    Column(modifier = Modifier.fillMaxSize()) {
        TableHeader(headers)
        EmptyStateMessage("No hay habitaciones bloqueadas")
    }
}

// ------------------------------------------------------------------------
// COMPONENTES AUXILIARES
// ------------------------------------------------------------------------

@Composable
fun TableHeader(headers: List<Pair<String, Float>>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(LightSurface)
            .padding(vertical = 12.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        headers.forEach { (title, weight) ->
            Text(
                text = title,
                modifier = Modifier.weight(weight),
                color = TextGray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
    Divider(color = TextGray.copy(alpha = 0.1f))
}

@Composable
fun EmptyStateMessage(message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            color = TextGray,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}