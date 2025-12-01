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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.keyli.plazatrujillo.ui.theme.*

@Composable
fun ComandaScreen(navController: NavHostController) {
    val scrollState = rememberScrollState()

    // Estados del formulario
    var numero by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var habitacion by remember { mutableStateOf("") }

    // Estados para la fila de ítems
    var cantidad by remember { mutableStateOf("1") }
    var detalle by remember { mutableStateOf("") }
    var importe by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
            .verticalScroll(scrollState)
    ) {
        // --- CABECERA ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Comanda",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlack
            )
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = TextGray)
            }
        }

        Divider(color = Color(0xFFEEEEEE), thickness = 1.dp)
        Spacer(modifier = Modifier.height(24.dp))

        // --- FILA 1: Nº y Fecha ---
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            // N°
            Column(modifier = Modifier.weight(1f)) {
                LabelTextComanda("Nº")
                SimpleInput(value = numero, onValueChange = { numero = it }, height = 56.dp)
            }
            // Fecha
            Column(modifier = Modifier.weight(1f)) {
                LabelTextComanda("Fecha")
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .border(1.dp, Color(0xFF9E9E9E), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Icon(Icons.Default.DateRange, contentDescription = null, tint = OrangePrimary)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- FILA 2: Habitación y Huésped ---
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            // Habitación (más pequeño)
            Column(modifier = Modifier.weight(0.6f)) {
                LabelTextComanda("Habitación")
                SimpleInput(value = habitacion, onValueChange = { habitacion = it }, height = 56.dp)
            }
            // Señor / Huésped (Dropdown)
            Column(modifier = Modifier.weight(1.4f)) {
                LabelTextComanda("Señor / Huésped")
                DropdownInput(text = "Seleccionar...")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- CATEGORÍA ---
        LabelTextComanda("Categoría")
        DropdownInput(text = "Cafetería")

        Spacer(modifier = Modifier.height(24.dp))

        // --- DETALLE DE ITEMS ---
        Text(
            text = "Detalle de Items",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextBlack
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Cabeceras de la tabla
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("Cant.", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextBlack, modifier = Modifier.weight(0.8f))
            Text("Detalle", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextBlack, modifier = Modifier.weight(2.2f))
            Text("Importe", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextBlack, modifier = Modifier.weight(1.2f))
            Text("Acción", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextBlack, modifier = Modifier.weight(0.8f), textAlign = TextAlign.Center)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Fila de inputs
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Cantidad
            SimpleInput(value = cantidad, onValueChange = { cantidad = it }, modifier = Modifier.weight(0.8f))

            // Detalle
            SimpleInput(value = detalle, onValueChange = { detalle = it }, modifier = Modifier.weight(2.2f))

            // Importe con prefijo S/
            Box(
                modifier = Modifier
                    .weight(1.2f)
                    .height(50.dp)
                    .border(1.dp, Color(0xFF9E9E9E), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("S/", color = TextGray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    BasicTextField(value = importe, onValueChange = { importe = it }, textStyle = TextStyle(fontSize = 14.sp))
                }
            }

            // Botón X roja
            Box(modifier = Modifier.weight(0.8f), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Close, contentDescription = "Eliminar", tint = StatusRed, modifier = Modifier.clickable {})
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Botón Añadir Item
        OutlinedButton(
            onClick = { },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, OrangePrimary)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Añadir ítem", color = OrangePrimary, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Divider(color = Color(0xFFEEEEEE))
        Spacer(modifier = Modifier.height(16.dp))

        // --- FOOTER / TOTALES ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Total S/.", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextBlack)
            Text("0,00", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botones de Acción
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            // Cancelar
            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, TextGray)
            ) {
                Text("Cancelar", color = TextGray, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            // Guardar Comanda
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
            ) {
                Text("Guardar\nComanda", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, lineHeight = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

// --- Componentes auxiliares para esta pantalla ---

@Composable
fun LabelTextComanda(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = TextGray,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

@Composable
fun SimpleInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    height: androidx.compose.ui.unit.Dp = 50.dp
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .border(1.dp, Color(0xFF9E9E9E), RoundedCornerShape(8.dp)) // Borde gris más oscuro como la imagen
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(fontSize = 16.sp, color = TextBlack),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun DropdownInput(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(1.dp, Color(0xFF9E9E9E), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text, fontSize = 16.sp, color = TextBlack)
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = TextBlack)
        }
    }
}