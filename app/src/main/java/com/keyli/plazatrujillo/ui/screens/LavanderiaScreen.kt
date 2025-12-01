package com.keyli.plazatrujillo.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.keyli.plazatrujillo.ui.theme.*

// --- DATA CLASS ACTUALIZADA SEGÚN LA IMAGEN ---
data class LaundryItem(
    val name: String,
    val total: Int = 0,
    val disponibles: Int = 0,
    val sucias: Int = 0, // "En Lavandería"
    val danados: Int = 0
)

@Composable
fun LavanderiaScreen(navController: NavHostController) {
    val scrollState = rememberScrollState()

    // Datos simulados (Artículos de la imagen)
    val inventoryList = listOf(
        LaundryItem("Toalla grande", 0, 0, 0, 0),
        LaundryItem("Toalla mediana", 0, 0, 0, 0),
        LaundryItem("Toalla chica", 0, 0, 0, 0),
        LaundryItem("Sábana 1/2 plaza", 0, 0, 0, 0),
        LaundryItem("Sábana 1 plaza", 0, 0, 0, 0),
        LaundryItem("Cubrecama 1/2 plaza", 0, 0, 0, 0),
        LaundryItem("Cubrecama 1 plaza", 0, 0, 0, 0),
        LaundryItem("Funda de almohada", 0, 0, 0, 0)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground) // Usando tu color de fondo
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // --- CABECERA ---
        Text(
            text = "Lavandería",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextBlack
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Administra el inventario de lavandería",
            fontSize = 14.sp,
            color = TextGray
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- TARJETAS DE RESUMEN (TOP) ---
        // Usamos Column o Row dependiendo del espacio.
        // Para simular la imagen (4 horizontal), usaremos una Row con weights,
        // pero dividida en 2 filas de 2 para móviles para que no se vea aplastado.

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryCard(
                    title = "Total Inventario",
                    count = 0,
                    icon = Icons.Default.Inventory2,
                    iconColor = Color(0xFF9C27B0), // Morado
                    bgColor = Color(0xFFF3E5F5),
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    title = "Total Disponibles",
                    count = 0,
                    icon = Icons.Default.CheckCircleOutline,
                    iconColor = Color(0xFF4CAF50), // Verde
                    bgColor = Color(0xFFE8F5E9),
                    modifier = Modifier.weight(1f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryCard(
                    title = "En Lavandería",
                    count = 0,
                    icon = Icons.Default.DeleteOutline, // Icono estilo bote (según imagen)
                    iconColor = Color(0xFF2196F3), // Azul
                    bgColor = Color(0xFFE3F2FD),
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    title = "Dañados",
                    count = 0,
                    icon = Icons.Default.WarningAmber,
                    iconColor = Color(0xFFF44336), // Rojo
                    bgColor = Color(0xFFFFEBEE),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- SECCIÓN INVENTARIO DE STOCK ---
        Text(
            text = "Inventario de Stock",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextBlack
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Gestión de disponibles, sucias y dañadas por artículo",
            fontSize = 13.sp,
            color = TextGray
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- TABLA ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                // Encabezados Tabla
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Artículo", modifier = Modifier.weight(2.5f), color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("Total", modifier = Modifier.weight(1f), color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Text("Disp.", modifier = Modifier.weight(1f), color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Text("Sucias", modifier = Modifier.weight(1f), color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Text("Dañados", modifier = Modifier.weight(1f), color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                }

                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = Color(0xFFF0F0F0))

                // Filas
                inventoryList.forEach { item ->
                    InventoryTableItem(item)
                    Divider(color = Color(0xFFFAFAFA))
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

// --- COMPONENTES AUXILIARES ---

@Composable
fun SummaryCard(
    title: String,
    count: Int,
    icon: ImageVector,
    iconColor: Color,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            // Icono con fondo de color suave
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(bgColor, shape = RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                fontSize = 12.sp,
                color = TextGray,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = count.toString(),
                fontSize = 20.sp,
                color = TextBlack,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun InventoryTableItem(item: LaundryItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Nombre del Artículo
        Text(
            text = item.name,
            modifier = Modifier.weight(2.5f),
            color = TextBlack,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )

        // Cajas de números (Simulando los inputs de la imagen)
        NumberBox(value = item.total, modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.width(4.dp))
        NumberBox(value = item.disponibles, modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.width(4.dp))
        NumberBox(value = item.sucias, modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.width(4.dp))
        NumberBox(value = item.danados, modifier = Modifier.weight(1f))
    }
}

@Composable
fun NumberBox(value: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(32.dp)
            .border(BorderStroke(1.dp, Color(0xFFE0E0E0)), RoundedCornerShape(6.dp))
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = value.toString(),
            color = TextBlack,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal
        )
    }
}