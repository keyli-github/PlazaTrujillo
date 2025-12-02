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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.keyli.plazatrujillo.ui.theme.OrangePrimary
import com.keyli.plazatrujillo.ui.theme.StatusGreen
import com.keyli.plazatrujillo.ui.theme.StatusRed

// Nota: OrangePrimary, StatusGreen y StatusRed vienen de tu archivo Color.kt
// Si no los tienes importados, descomenta las siguientes líneas:
// val OrangePrimary = Color(0xFFFF6B00)
// val StatusGreen = Color(0xFF10B981)
// val StatusRed = Color(0xFFEF4444)

@Composable
fun MantenimientoScreen(navController: NavHostController) {
    // --- COLORES DINÁMICOS DEL TEMA ---
    val bgColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onBackground
    val subTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
    val dividerColor = MaterialTheme.colorScheme.outlineVariant

    // Estado para controlar la pestaña seleccionada
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Sistema Agua Caliente", "Historial Briquetas", "Incidencias", "Habitaciones Bloqueadas")

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = bgColor // Fondo global dinámico
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // --- HEADER ---
            Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 8.dp)) {
                Text(
                    text = "Mantenimiento Técnico",
                    color = textColor,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Gestión del sistema de agua caliente y mantenimiento de habitaciones",
                    color = subTextColor,
                    fontSize = 14.sp
                )
            }

            // --- TABS ---
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = bgColor, // Fondo de tabs dinámico
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
                divider = { HorizontalDivider(color = dividerColor) }
            ) {
                tabs.forEachIndexed { index, title ->
                    val isSelected = selectedTabIndex == index
                    Tab(
                        selected = isSelected,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) OrangePrimary else subTextColor
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
// TAB 1: SISTEMA AGUA CALIENTE
// ------------------------------------------------------------------------
@Composable
fun TabAguaCaliente(navController: NavHostController) {
    val textColor = MaterialTheme.colorScheme.onBackground

    Column(modifier = Modifier.fillMaxSize()) {

        // --- FILA 1 ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatusCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.CheckCircle,
                iconBgColor = Color(0xFFDCFCE7), // Mantenemos colores pastel en iconos
                iconColor = Color(0xFF16A34A),
                title = "Estado Operativo",
                value = "Operativo"
            )

            StatusCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.DateRange,
                iconBgColor = Color(0xFFFFEDD5),
                iconColor = Color(0xFFEA580C),
                title = "Briquetas Este Mes",
                value = "0 Unid."
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- FILA 2 ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatusCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.AccessTime,
                iconBgColor = Color(0xFFDBEAFE),
                iconColor = Color(0xFF2563EB),
                title = "Último Cambio",
                value = "No registrado"
            )

            StatusCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Schedule,
                iconBgColor = Color(0xFFF3E8FF),
                iconColor = Color(0xFF9333EA),
                title = "Próximo Cambio",
                value = "No programado"
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- BOTONES DE ACCIÓN ---
        Text(text = "Acciones Rápidas", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = textColor)
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
            Text("Registrar Cambio Briquetas", fontSize = 16.sp, color = Color.White)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = { navController.navigate("bloq_habitacion") },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text("Bloquear Hab.", fontSize = 14.sp, color = Color.White)
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
                Text("Reportar Incidencia", fontSize = 12.sp, color = Color.White)
            }
        }
    }
}

// ------------------------------------------------------------------------
// STATUS CARD (DINÁMICO)
// ------------------------------------------------------------------------
@Composable
fun StatusCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconBgColor: Color,
    iconColor: Color,
    title: String,
    value: String
) {
    // Colores de la tarjeta adaptables
    val cardColor = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onSurface
    val subTextColor = MaterialTheme.colorScheme.onSurfaceVariant
    val borderColor = MaterialTheme.colorScheme.outlineVariant

    Card(
        modifier = modifier.height(145.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = BorderStroke(1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            // Icono con fondo cuadrado redondeado
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
                    color = subTextColor,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor, // Se vuelve blanco en modo oscuro
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// ------------------------------------------------------------------------
// TABS AUXILIARES (TABLAS)
// ------------------------------------------------------------------------
@Composable
fun TabHistorialBriquetas() {
    val headers = listOf("Fecha" to 0.4f, "Hora" to 0.3f, "Cantidad" to 0.3f)

    Column(modifier = Modifier.fillMaxSize()) {
        TableHeader(headers)
        EmptyStateMessage("No hay historial de cambios de briquetas")
    }
}

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
// COMPONENTES AUXILIARES ADAPTABLES
// ------------------------------------------------------------------------

@Composable
fun TableHeader(headers: List<Pair<String, Float>>) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant
    val dividerColor = MaterialTheme.colorScheme.outlineVariant

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(surfaceColor)
            .padding(vertical = 12.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        headers.forEach { (title, weight) ->
            Text(
                text = title,
                modifier = Modifier.weight(weight),
                color = textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
    HorizontalDivider(color = dividerColor)
}

@Composable
fun EmptyStateMessage(message: String) {
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            color = textColor,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}