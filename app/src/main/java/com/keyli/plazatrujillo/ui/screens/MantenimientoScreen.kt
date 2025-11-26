package com.keyli.plazatrujillo.ui.screens

import androidx.navigation.NavHostController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.widthIn as _widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import com.keyli.plazatrujillo.ui.theme.LightBackground
import com.keyli.plazatrujillo.ui.theme.LightSurface
import com.keyli.plazatrujillo.ui.theme.OrangePrimary
import com.keyli.plazatrujillo.ui.theme.OrangeSecondary
import com.keyli.plazatrujillo.ui.theme.StatusGreen
import com.keyli.plazatrujillo.ui.theme.StatusRed
import com.keyli.plazatrujillo.ui.theme.TextBlack
import com.keyli.plazatrujillo.ui.theme.TextGray

data class Incidencia(
    val area: String,
    val detalle: String,
    val prioridad: String,
    val hora: String,
    val estado: String,
    val estadoColor: Color,
    val prioridadColor: Color
)

@Composable
fun MantenimientoScreen(navController: NavHostController) {
    // Datos de ejemplo (reemplazar por ViewModel)
    val incidencias = remember {
        listOf(
            Incidencia(
                "205",
                "Aire acondicionado no enfría correctamente en la habitación 205",
                "Prioridad Alta",
                "Hoy, 10:30",
                "Pendiente",
                StatusRed.copy(alpha = 0.12f),
                StatusRed
            ),
            Incidencia(
                "104",
                "Foco del baño fundido",
                "Prioridad Baja",
                "Hoy, 09:15",
                "En Progreso",
                OrangeSecondary.copy(alpha = 0.12f),
                StatusGreen
            ),
            Incidencia(
                "301",
                "TV sin señal en la habitación",
                "Prioridad Media",
                "Ayer, 18:20",
                "Pendiente",
                StatusRed.copy(alpha = 0.12f),
                OrangeSecondary
            ),
            Incidencia(
                "Lobby",
                "Puerta principal atascada al abrir",
                "Prioridad Alta",
                "Ayer, 14:00",
                "En Progreso",
                OrangeSecondary.copy(alpha = 0.12f),
                StatusRed
            )
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = LightBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.Top
        ) {
            // TITULO
            Text(
                text = "Mantenimiento",
                color = TextBlack,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = "Gestión de reparaciones e infraestructura",
                color = TextGray,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 14.dp)
            )

            // BOTON REPORTAR
            Button(
                onClick = { /* navegar a reporte */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Reportar", tint = Color.White, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "Reportar Incidencia", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // TARJETAS RESUMEN: calculamos ancho disponible y fijamos anchos iguales sin usar Modifier.weight
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val totalSpacing = 12.dp * 2 // dos espacios entre 3 tarjetas
                val cardWidth = (maxWidth - totalSpacing) / 3

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SummaryCard(number = "3", label = "Pendientes", tint = StatusRed, modifier = Modifier.width(cardWidth))
                    SummaryCard(number = "2", label = "En Proceso", tint = OrangeSecondary, modifier = Modifier.width(cardWidth))
                    SummaryCard(number = "15", label = "Resueltos", tint = StatusGreen, modifier = Modifier.width(cardWidth))
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // INCIDENCIAS RECIENTES - Card que ocupa aproximadamente el 60% de la altura disponible
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.60f),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(LightSurface)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Incidencias Recientes",
                        color = TextBlack,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Reportes de averías y solicitudes técnicas",
                        color = TextGray,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Divider(color = TextGray.copy(alpha = 0.12f), thickness = 1.dp)

                    Spacer(modifier = Modifier.height(8.dp))

                    // Lista: altura flexible dentro de la card (usa LazyColumn con heightIn para evitar weights)
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 120.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(incidencias) { item ->
                            IncidenciaRow(incidencia = item)
                            Divider(color = TextGray.copy(alpha = 0.08f), thickness = 1.dp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(number: String, label: String, tint: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .heightIn(min = 88.dp)
            .clip(RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(LightSurface)
                .padding(vertical = 12.dp, horizontal = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon placeholder con espacio suficiente
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(tint.copy(alpha = 0.12f))
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(text = number, color = TextBlack, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = label, color = TextGray, fontSize = 13.sp)
        }
    }
}

@Composable
private fun IncidenciaRow(incidencia: Incidencia) {
    // Evitamos Modifier.weight por problemas de versión/import. Calculamos el ancho disponible y asignamos
    BoxWithConstraints(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        val statusMinWidth = 84.dp
        val spacing = 12.dp
        val mainWidth = maxWidth - statusMinWidth - spacing

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .width(mainWidth)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = incidencia.area,
                    color = TextBlack,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = incidencia.detalle,
                    color = TextBlack,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(end = 4.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = incidencia.hora, color = TextGray, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = incidencia.prioridad, color = incidencia.prioridadColor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            // Estado: ancho mínimo fijado para que no comprima el texto principal
            Box(
                modifier = Modifier
                    .widthIn(min = statusMinWidth)
                    .clip(RoundedCornerShape(12.dp))
                    .background(incidencia.estadoColor)
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                // Alineamos el texto manualmente dentro del Box usando Modifier.align
                Text(
                    text = incidencia.estado,
                    color = when (incidencia.estado) {
                        "Pendiente" -> StatusRed
                        "En Progreso" -> OrangeSecondary
                        "Resuelto" -> StatusGreen
                        else -> TextBlack
                    },
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            }
        }
    }
}