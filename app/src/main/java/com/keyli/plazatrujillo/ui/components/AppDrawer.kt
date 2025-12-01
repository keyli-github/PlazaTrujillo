package com.keyli.plazatrujillo.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.keyli.plazatrujillo.ui.navigation.drawerOptions
import com.keyli.plazatrujillo.ui.theme.OrangePrimary

@Composable
fun AppDrawer(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val backgroundColor = MaterialTheme.colorScheme.surface
    val contentColor = MaterialTheme.colorScheme.onSurface
    val selectedBgColor = OrangePrimary.copy(alpha = 0.15f)

    ModalDrawerSheet(
        drawerContainerColor = backgroundColor,
        drawerContentColor = contentColor,
        modifier = Modifier.width(300.dp)
    ) {
        // Empujamos el contenido hacia abajo para que quede justo debajo del TopAppBar
        Spacer(modifier = Modifier.height(56.dp)) // Ajusta si tu TopAppBar tiene otra altura

        Text(
            text = "MENU",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 24.dp, bottom = 16.dp)
        )

        drawerOptions.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationDrawerItem(
                label = {
                    Text(
                        text = item.title,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = if (isSelected) OrangePrimary else contentColor.copy(alpha = 0.6f)
                    )
                },
                selected = isSelected,
                onClick = {
                    // Solo emitimos la navegación; el cierre lo hace NavigationWrapper
                    onNavigate(item.route)
                },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = selectedBgColor,
                    selectedTextColor = OrangePrimary,
                    unselectedContainerColor = Color.Transparent,
                    unselectedTextColor = contentColor
                ),
                shape = RoundedCornerShape(topEnd = 32.dp, bottomEnd = 32.dp),
                modifier = Modifier.padding(end = 16.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Footer
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Hotel Plaza Trujillo",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(

                    text = "Sistema de Gestión",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { onNavigate("dashboard") },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ir al Dashboard", color = Color.White)
                }
            }
        }
    }
}