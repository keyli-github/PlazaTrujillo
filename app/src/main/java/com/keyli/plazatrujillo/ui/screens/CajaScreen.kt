package com.keyli.plazatrujillo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.keyli.plazatrujillo.ui.theme.*

data class Movimiento(
    val id: String,
    val concepto: String,
    val metodo: String
)

@Composable
fun CajaScreen(navController: androidx.navigation.NavHostController) {

    val movimientos = remember {
        listOf(
            Movimiento("TX-9921", "Reserva Hab. 202", "Yape"),
            Movimiento("TX-9922", "Consumo Restaurante", "Efectivo"),
            Movimiento("TX-9923", "Lavandería", "Tarjeta"),
            Movimiento("TX-9924", "Late Check-out", "Yape"),
            Movimiento("TX-9925", "Frigobar", "Efectivo")
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = LightBackground
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())   // ← SCROLL GENERAL
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.Top
        ) {

            Text(
                text = "Caja y Cobros",
                color = TextBlack,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Gestión de ingresos y movimientos del día",
                color = TextGray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "arqueo", tint = TextBlack)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Arqueo Caja", color = TextBlack, fontWeight = FontWeight.SemiBold)
                    }
                }

                Button(
                    onClick = { /* nuevo cobro */ },
                    modifier = Modifier.height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "nuevo", tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Nuevo Cobro", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                FinancialCard(label = "Total Yape", amount = "S/ 1,695.00", iconBg = Color(0xFFEDE7F6))
                FinancialCard(label = "Total Efectivo", amount = "S/ 815.00", iconBg = Color(0xFFE8F5E9))
                FinancialCard(label = "Total Tarjeta", amount = "S/ 150.00", iconBg = Color(0xFFE3F2FD))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 72.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(OrangePrimary.copy(alpha = 0.08f))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "RECAUDACIÓN TOTAL", color = OrangeSecondary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Cierre estimado de hoy", color = TextGray, fontSize = 12.sp)
                    }

                    Text(text = "S/ 2,660.00", color = OrangePrimary, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth(),   // ← QUITADO EL weight(1f)
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = LightSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(text = "Movimientos Hoy", color = TextBlack, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Últimas 5 transacciones", color = TextGray, fontSize = 13.sp)
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF5F5F5),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 12.dp)
                                    .clickableNoRipple { },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Outlined.FilterList, contentDescription = "Filtrar", tint = TextGray, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Filtrar", color = TextGray, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ID",
                            color = TextGray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(90.dp)
                        )
                        Text(
                            text = "CONCEPTO",
                            color = TextGray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "MÉTODO",
                            color = TextGray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(100.dp),
                            textAlign = TextAlign.End
                        )
                    }

                    Divider(color = TextGray.copy(alpha = 0.12f), thickness = 1.dp)

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier.heightIn(max = 500.dp),  // ← Limita y deja scroll
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(movimientos) { item ->
                            MovimientoItemRow(item)
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        TextButton(onClick = { }) {
                            Text(
                                text = "Ver historial completo",
                                color = OrangePrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(26.dp))
        }
    }
}

@Composable
private fun FinancialCard(label: String, amount: String, iconBg: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .clip(RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(LightSurface)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconBg)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(text = label, color = TextGray, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = amount, color = TextBlack, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun MovimientoItemRow(movimiento: Movimiento) {

    val (bgColor, textColor) = when (movimiento.metodo) {
        "Yape" -> Pair(Color(0xFFEDE7F6), Color(0xFF673AB7))
        "Efectivo" -> Pair(Color(0xFFE8F5E9), Color(0xFF2E7D32))
        "Tarjeta" -> Pair(Color(0xFFE3F2FD), Color(0xFF1565C0))
        else -> Pair(Color.LightGray, Color.Black)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = movimiento.id,
            color = TextBlack,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(90.dp)
        )

        Text(
            text = movimiento.concepto,
            color = TextBlack,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
        )

        Box(
            modifier = Modifier.width(100.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Surface(
                color = bgColor,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = movimiento.metodo,
                    color = textColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier = composed {
    this.clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onClick
    )
}
