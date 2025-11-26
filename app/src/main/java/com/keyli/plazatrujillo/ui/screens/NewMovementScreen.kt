package com.keyli.plazatrujillo.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.keyli.plazatrujillo.ui.theme.*

@Composable
fun NewMovementScreen(navController: NavHostController) {
    var monto by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
    ) {
        // --- CABECERA CON "X" ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Text(
                    text = "Nuevo Movimiento",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Introduce los detalles de la transacción.",
                    fontSize = 14.sp,
                    color = TextGray
                )
            }
            // Botón X para regresar
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = TextBlack)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- FORMULARIO ---

        // 1. Concepto (Dropdown)
        LabelText("Concepto")
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Alojamiento", fontSize = 16.sp, color = TextBlack)
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = TextGray)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 2. Fecha y FACT/BV (Lado a lado)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            // Fecha
            Column(modifier = Modifier.weight(1f)) {
                LabelText("Fecha")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(verticalArrangement = Arrangement.Center) {
                        Text("dd/mm/", fontSize = 16.sp, color = TextGray)
                        Text("aaaa", fontSize = 16.sp, color = TextGray)
                    }
                    Icon(Icons.Default.DateRange, contentDescription = null, tint = OrangePrimary)
                }
            }

            // FACT/BV
            Column(modifier = Modifier.weight(1f)) {
                LabelText("FACT/BV")
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text("000-000", fontSize = 16.sp, color = TextGray)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 3. Monto
        LabelText("Monto")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("S/", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)
            Spacer(modifier = Modifier.width(8.dp))
            BasicTextField(
                value = monto,
                onValueChange = { monto = it },
                textStyle = TextStyle(fontSize = 16.sp, color = TextBlack),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

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

            // Agregar (Gris según imagen, o puedes ponerlo Naranja si quieres que resalte)
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0)) // Gris claro
            ) {
                Text("Agregar", color = TextGray, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}