package com.keyli.plazatrujillo.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

// --- DATA CLASSES PARA ESTA PANTALLA ---
data class InventoryItem(
    val name: String,
    val total: Int,
    val disp: Int,
    val status: String, // "Normal" o "Bajo Stock"
)

data class LaundryMovement(
    val date: String,
    val time: String,
    val room: String,
    val count: Int,
    val status: String // "En Lavandería" o "Retornado"
)

@Composable
fun LavanderiaScreen(navController: NavHostController) {
    val scrollState = rememberScrollState()

    // Datos simulados (Inventario)
    val inventoryList = listOf(
        InventoryItem("Toallas", 450, 320, "Normal"),
        InventoryItem("Sábanas", 380, 250, "Normal"),
        InventoryItem("Fundas Almohada", 520, 380, "Normal"),
        InventoryItem("Cobertores", 180, 140, "Normal"),
        InventoryItem("Mantas Polares", 150, 20, "Bajo Stock")
    )

    // Datos simulados (Movimientos)
    val movementList = listOf(
        LaundryMovement("15 Ene", "10:30", "101", 4, "En Lavandería"),
        LaundryMovement("15 Ene", "09:15", "205", 6, "En Lavandería"),
        LaundryMovement("14 Ene", "18:20", "302", 4, "Retornado"),
        LaundryMovement("14 Ene", "14:00", "110", 4, "Retornado"),
        LaundryMovement("13 Ene", "11:45", "201", 6, "Retornado"),
        LaundryMovement("13 Ene", "08:30", "105", 4, "Retornado")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // --- CABECERA ---
        Text(
            text = "Control de Lavandería",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextBlack
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Gestión de inventario y rotación",
            fontSize = 14.sp,
            color = TextGray
        )

        Spacer(modifier = Modifier.height(20.dp))

        // --- BOTONES DE ACCIÓN ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Botón Registrar Retorno (Gris claro)
            Button(
                onClick = { /* Acción */ },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0F0F0)),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFE0E0E0))
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = TextBlack, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Registrar", color = TextBlack, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("Retorno", color = TextBlack, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Botón Enviar Lote (Naranja)
            Button(
                onClick = { /* Acción */ },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.LocalLaundryService, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Enviar Lote", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- TARJETA 1: INVENTARIO ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                // Header Inventario
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(OrangePrimary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Inventory2, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Inventario de Textiles", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                        Text("Stock en tiempo real por categoría", fontSize = 12.sp, color = TextGray)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Encabezados Tabla
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("ARTÍCULO", modifier = Modifier.weight(2f), color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("TOTAL", modifier = Modifier.weight(0.8f), color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Text("DISP.", modifier = Modifier.weight(0.8f), color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Text("ESTADO", modifier = Modifier.weight(1.2f), color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                }

                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = Color(0xFFEEEEEE))

                // Filas Inventario
                inventoryList.forEach { item ->
                    InventoryRow(item)
                    Divider(color = Color(0xFFFAFAFA))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- TARJETA 2: ÚLTIMOS MOVIMIENTOS ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                // Header Movimientos
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(OrangePrimary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.LocalLaundryService, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Últimos Movimientos", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                        Text("Registro de entradas y salidas", fontSize = 12.sp, color = TextGray)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Encabezados Tabla
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("FECHA/HORA", modifier = Modifier.weight(1.5f), color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("HAB.", modifier = Modifier.weight(0.8f), color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Text("CANT.", modifier = Modifier.weight(0.8f), color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Text("ESTADO", modifier = Modifier.weight(1.5f), color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                }

                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = Color(0xFFEEEEEE))

                // Filas Movimientos
                movementList.forEach { move ->
                    MovementRow(move)
                    Divider(color = Color(0xFFFAFAFA))
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

// --- COMPONENTES AUXILIARES ---

@Composable
fun InventoryRow(item: InventoryItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Nombre
        Text(
            text = item.name,
            modifier = Modifier.weight(2f),
            color = TextBlack,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
        // Total
        Text(
            text = item.total.toString(),
            modifier = Modifier.weight(0.8f),
            color = TextGray,
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )
        // Disponible (Verde si es normal, Naranja si es bajo)
        Text(
            text = item.disp.toString(),
            modifier = Modifier.weight(0.8f),
            color = if (item.status == "Normal") StatusGreen else OrangePrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        // Estado Badge
        Box(
            modifier = Modifier.weight(1.2f),
            contentAlignment = Alignment.Center
        ) {
            val bgColor = if (item.status == "Normal") StatusGreen.copy(alpha = 0.15f) else Color(0xFFFFCCBC) // Fondo Naranja claro
            val textColor = if (item.status == "Normal") StatusGreen else Color(0xFFD84315) // Texto Naranja oscuro

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(bgColor)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (item.status == "Normal") "Normal" else "Bajo\nStock",
                    color = textColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    lineHeight = 12.sp
                )
            }
        }
    }
}

@Composable
fun MovementRow(move: LaundryMovement) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Fecha y Hora
        Column(modifier = Modifier.weight(1.5f)) {
            Text(text = move.date, color = TextBlack, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = move.time, color = TextGray, fontSize = 12.sp)
        }

        // Habitación
        Text(
            text = move.room,
            modifier = Modifier.weight(0.8f),
            color = TextBlack,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        // Cantidad
        Text(
            text = move.count.toString(),
            modifier = Modifier.weight(0.8f),
            color = TextBlack,
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )

        // Estado
        Box(
            modifier = Modifier.weight(1.5f),
            contentAlignment = Alignment.CenterEnd
        ) {
            val isWashing = move.status == "En Lavandería"
            // Colores lavandería: Naranja suave / Retornado: Verde suave
            val bgColor = if (isWashing) Color(0xFFFFE0B2).copy(alpha = 0.5f) else StatusGreen.copy(alpha = 0.15f)
            val textColor = if (isWashing) OrangePrimary else StatusGreen

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(bgColor)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .widthIn(min = 80.dp), // Ancho mínimo para que se vean parejos
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isWashing) "En\nLavandería" else "Retornado",
                    color = textColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    lineHeight = 12.sp
                )
            }
        }
    }
}