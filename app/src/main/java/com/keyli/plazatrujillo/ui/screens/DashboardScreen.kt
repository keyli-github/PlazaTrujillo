//DashboardScreen.kt

package com.keyli.plazatrujillo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.keyli.plazatrujillo.ui.theme.OrangePrimary

// Datos para las tarjetas del Dashboard
data class DashboardItem(
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val route: String
)

@Composable
fun DashboardScreen(navController: NavController) {

    // Lista de opciones
    val items = listOf(
        DashboardItem("Usuarios", Icons.Default.Person, Color(0xFF4CAF50), "usuarios"),
        DashboardItem("Reservas", Icons.Default.DateRange, Color(0xFF2196F3), "reservas"),
        DashboardItem("Caja", Icons.Default.AttachMoney, Color(0xFFFF9800), "caja"),
        DashboardItem("Lavandería", Icons.Default.LocalLaundryService, Color(0xFF9C27B0), "lavanderia"),
        DashboardItem("Mantenimiento", Icons.Default.Bolt, Color(0xFF607D8B), "mantenimiento"),
        DashboardItem("Mensajes", Icons.Default.Email, Color(0xFFE91E63), "mensajes"),
        DashboardItem("ChatBot", Icons.Default.Face, OrangePrimary, "chatbot")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        Text(
            text = "Panel de Control",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF333333)
        )
        Text(
            text = "Bienvenido, Marco Gutierrez",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(items) { item ->
                DashboardCard(item) {
                    navController.navigate(item.route)
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun DashboardCard(item: DashboardItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(item.color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(item.icon, contentDescription = null, tint = item.color, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = item.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color(0xFF333333))
        }
    }
}