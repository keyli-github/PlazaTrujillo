package com.keyli.plazatrujillo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.keyli.plazatrujillo.ui.theme.OrangePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mi Perfil", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5)) // Fondo gris claro
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- TARJETA 1: DATOS PERSONALES ---
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar Circular
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(OrangePrimary.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = OrangePrimary,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    TextButton(onClick = { /* Acción cambiar foto */ }) {
                        Text("Cambiar Foto", color = OrangePrimary, fontWeight = FontWeight.Bold)
                    }

                    Text("Mi Perfil", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(20.dp))

                    // Campos
                    ProfileField("Nombre Completo", "Marco Antonio Castro Pared", Icons.Default.Person)
                    ProfileField("Teléfono", "+51 987 654 321", Icons.Default.Phone)
                    ProfileField("Rol", "Administrador", Icons.Default.VerifiedUser)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- TARJETA 2: INFORMACIÓN DEL HOTEL ---
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Información del Hotel", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(20.dp))

                    ProfileField("Razón Social", "Plaza Trujillo SAC", Icons.Default.Business)
                    ProfileField("RUC", "20456789012", Icons.Default.Badge)
                    ProfileField("Dirección", "Av. San Martín 123, Trujillo", Icons.Default.LocationOn)
                    ProfileField("Teléfono", "+51 44 123 456", Icons.Default.Call)
                    ProfileField("Correo Electrónico", "reservas@plazatrujillo.com", Icons.Default.Email)
                    ProfileField("Página Web", "www.plazatrujillo.com", Icons.Default.Language)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- BOTÓN GUARDAR ---
            Button(
                onClick = { /* Acción guardar */ },
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Save, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("GUARDAR CAMBIOS", fontWeight = FontWeight.Bold, color = Color.White)
            }

            // Espacio extra al final
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// Componente reutilizable para cada campo
@Composable
fun ProfileField(label: String, value: String, icon: ImageVector) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = value,
                fontSize = 14.sp,
                color = Color(0xFF333333),
                fontWeight = FontWeight.Medium
            )
        }
    }
}