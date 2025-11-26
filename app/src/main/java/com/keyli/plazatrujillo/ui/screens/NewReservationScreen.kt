package com.keyli.plazatrujillo.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.keyli.plazatrujillo.ui.theme.*

@Composable
fun NewReservationScreen(navController: NavHostController) {
    var guestName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
    ) {
        // --- CABECERA ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Text(
                    text = "Nueva Reserva",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Complete la información del huésped",
                    fontSize = 14.sp,
                    color = TextGray
                )
            }
            // Botón cerrar (X)
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = TextGray)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- FORMULARIO ---

        // 1. Canal de Reserva
        LabelText("Canal de Reserva")
        CustomInputBox(
            icon = Icons.Default.Public, // Icono mundo
            text = "Booking.com",
            isDropdown = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Huésped
        LabelText("Huésped")
        // Campo de texto editable personalizado
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Person, contentDescription = null, tint = OrangePrimary)
            Spacer(modifier = Modifier.width(12.dp))
            Box(modifier = Modifier.weight(1f)) {
                if (guestName.isEmpty()) {
                    Text("Nombre completo", color = TextGray)
                }
                BasicTextField(
                    value = guestName,
                    onValueChange = { guestName = it },
                    textStyle = TextStyle(fontSize = 16.sp, color = TextBlack),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Habitación
        LabelText("Habitación")
        CustomInputBox(
            icon = Icons.Default.MeetingRoom, // Icono puerta
            text = "Suite 201",
            isDropdown = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Fechas (Check-in y Check-out)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                LabelText("Check-in")
                DateInputBox(day = "25", month = "nov", year = "2025")
            }
            Column(modifier = Modifier.weight(1f)) {
                LabelText("Check-out")
                DateInputBox(day = "26", month = "nov", year = "2025")
            }
        }

        Spacer(modifier = Modifier.weight(1f)) // Empuja los botones al final

        // --- BOTONES ---
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            // Cancelar
            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, TextGray)
            ) {
                Text("Cancelar", color = TextGray, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            // Crear Reserva
            Button(
                onClick = { navController.popBackStack() }, // Aquí iría la lógica de guardar
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
            ) {
                Text("Crear Reserva", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// --- COMPONENTES AUXILIARES PARA ESTA PANTALLA ---

@Composable
fun LabelText(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = TextBlack,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun CustomInputBox(icon: ImageVector, text: String, isDropdown: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
            .clickable { }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = OrangePrimary)
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, fontSize = 16.sp, color = TextBlack, modifier = Modifier.weight(1f))
        if (isDropdown) {
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = TextGray)
        }
    }
}

@Composable
fun DateInputBox(day: String, month: String, year: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp) // Altura para que quepan las 3 líneas
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.DateRange, contentDescription = null, tint = OrangePrimary)
        Spacer(modifier = Modifier.width(12.dp))
        Column(verticalArrangement = Arrangement.Center) {
            Text(day, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextBlack)
            Text(month, fontSize = 14.sp, color = TextBlack)
            Text(year, fontSize = 14.sp, color = TextBlack)
        }
        Spacer(modifier = Modifier.weight(1f))
        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = TextGray)
    }
}