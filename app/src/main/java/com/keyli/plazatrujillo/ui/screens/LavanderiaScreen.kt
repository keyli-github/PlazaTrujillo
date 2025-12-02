package com.keyli.plazatrujillo.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.keyli.plazatrujillo.ui.theme.*

// --- DATA CLASS ---
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

    // --- COLORES DINÁMICOS ---
    val bgColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onBackground
    val subTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
    val cardBgColor = MaterialTheme.colorScheme.surface

    // --- ESTADO MUTABLE (Para que se pueda editar) ---
    // Usamos remember { mutableStateListOf(...) } para que los cambios se reflejen en la UI
    val inventoryList = remember {
        mutableStateListOf(
            LaundryItem("Toalla grande", 150, 100, 45, 5),
            LaundryItem("Toalla mediana", 120, 80, 35, 5),
            LaundryItem("Toalla chica", 200, 150, 40, 10),
            LaundryItem("Sábana 1/2 plaza", 80, 60, 15, 5),
            LaundryItem("Sábana 1 plaza", 90, 70, 15, 5),
            LaundryItem("Cubrecama 1/2 plaza", 40, 30, 8, 2),
            LaundryItem("Cubrecama 1 plaza", 45, 35, 8, 2),
            LaundryItem("Funda de almohada", 300, 200, 90, 10)
        )
    }

    // Cálculos automáticos para las tarjetas de arriba
    val totalGlobal = inventoryList.sumOf { it.total }
    val totalDisponibles = inventoryList.sumOf { it.disponibles }
    val totalSucias = inventoryList.sumOf { it.sucias }
    val totalDanados = inventoryList.sumOf { it.danados }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // --- CABECERA ---
        Text(
            text = "Lavandería",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Administra el inventario de lavandería",
            fontSize = 14.sp,
            color = subTextColor
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- TARJETAS DE RESUMEN (TOP) ---
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryCard(
                    title = "Total Inventario",
                    count = totalGlobal,
                    icon = Icons.Default.Inventory2,
                    iconColor = Color(0xFF9C27B0),
                    bgColor = Color(0xFFF3E5F5),
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    title = "Total Disponibles",
                    count = totalDisponibles,
                    icon = Icons.Default.CheckCircleOutline,
                    iconColor = Color(0xFF4CAF50),
                    bgColor = Color(0xFFE8F5E9),
                    modifier = Modifier.weight(1f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryCard(
                    title = "En Lavandería",
                    count = totalSucias,
                    icon = Icons.Default.DeleteOutline,
                    iconColor = Color(0xFF2196F3),
                    bgColor = Color(0xFFE3F2FD),
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    title = "Dañados",
                    count = totalDanados,
                    icon = Icons.Default.WarningAmber,
                    iconColor = Color(0xFFF44336),
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
            color = textColor
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Toca los recuadros para editar las cantidades",
            fontSize = 13.sp,
            color = subTextColor
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- TABLA ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = cardBgColor),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                // Encabezados Tabla
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Artículo", modifier = Modifier.weight(2.5f), color = subTextColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("Total", modifier = Modifier.weight(1f), color = subTextColor, fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Text("Disp.", modifier = Modifier.weight(1f), color = subTextColor, fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Text("Sucias", modifier = Modifier.weight(1f), color = subTextColor, fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Text("Dañados", modifier = Modifier.weight(1f), color = subTextColor, fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                // Filas Editables
                inventoryList.forEachIndexed { index, item ->
                    InventoryTableItem(
                        item = item,
                        // Callbacks para actualizar la lista mutable
                        onTotalChange = { newVal -> inventoryList[index] = item.copy(total = newVal) },
                        onDispChange = { newVal -> inventoryList[index] = item.copy(disponibles = newVal) },
                        onSuciasChange = { newVal -> inventoryList[index] = item.copy(sucias = newVal) },
                        onDanadosChange = { newVal -> inventoryList[index] = item.copy(danados = newVal) }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
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
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = count.toString(),
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun InventoryTableItem(
    item: LaundryItem,
    onTotalChange: (Int) -> Unit,
    onDispChange: (Int) -> Unit,
    onSuciasChange: (Int) -> Unit,
    onDanadosChange: (Int) -> Unit
) {
    val textColor = MaterialTheme.colorScheme.onSurface

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
            color = textColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )

        // Cajas editables
        EditableNumberBox(value = item.total, onValueChange = onTotalChange, modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.width(4.dp))
        EditableNumberBox(value = item.disponibles, onValueChange = onDispChange, modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.width(4.dp))
        EditableNumberBox(value = item.sucias, onValueChange = onSuciasChange, modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.width(4.dp))
        EditableNumberBox(value = item.danados, onValueChange = onDanadosChange, modifier = Modifier.weight(1f))
    }
}

@Composable
fun EditableNumberBox(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    val boxBg = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onBackground
    val cursorColor = MaterialTheme.colorScheme.primary

    // Estado local para el texto del input
    var text by remember(value) { mutableStateOf(value.toString()) }

    BasicTextField(
        value = text,
        onValueChange = { newText ->
            // Solo permitir números
            if (newText.all { it.isDigit() }) {
                text = newText
                // Si está vacío, lo tratamos como 0, si no, convertimos a Int
                val number = newText.toIntOrNull() ?: 0
                onValueChange(number)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        textStyle = TextStyle(
            color = textColor,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        ),
        cursorBrush = SolidColor(cursorColor),
        singleLine = true,
        modifier = modifier
            .height(32.dp)
            .border(BorderStroke(1.dp, borderColor), RoundedCornerShape(6.dp))
            .background(boxBg, RoundedCornerShape(6.dp)),
        decorationBox = { innerTextField ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                // Si el texto está vacío, mostrar "0" en gris (opcional)
                if (text.isEmpty()) {
                    Text("0", color = textColor.copy(alpha = 0.5f), fontSize = 12.sp)
                }
                innerTextField()
            }
        }
    )
}