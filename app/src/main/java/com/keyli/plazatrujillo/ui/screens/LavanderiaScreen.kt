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
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Refresh
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.keyli.plazatrujillo.ui.viewmodel.LavanderiaViewModel
import com.keyli.plazatrujillo.ui.viewmodel.LAUNDRY_CATEGORIES
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LavanderiaScreen(
    navController: NavHostController,
    viewModel: LavanderiaViewModel = viewModel()
) {
    val scrollState = rememberScrollState()
    val uiState by viewModel.uiState.collectAsState()

    // Colores dinámicos
    val bgColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onBackground
    val subTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
    val cardBgColor = MaterialTheme.colorScheme.surface

    // Snackbar para errores y mensajes
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Mostrar errores
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }
    
    // Mostrar mensajes de éxito
    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            delay(1500)
            viewModel.clearSuccessMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = bgColor
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                //.padding(paddingValues)
        ) {
            if (uiState.isLoading && uiState.stock.isEmpty()) {
                // Loading inicial
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(16.dp)
                ) {
                    // --- CABECERA ---
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Lavandería",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                            Text(
                                text = "Administra el inventario de lavandería",
                                fontSize = 14.sp,
                                color = subTextColor
                            )
                        }
                        
                        // Botón refrescar
                        IconButton(
                            onClick = { viewModel.refresh() },
                            enabled = !uiState.isLoading
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    Icons.Default.Refresh,
                                    contentDescription = "Refrescar",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- TARJETAS DE RESUMEN ---
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            SummaryCard(
                                title = "Total Inventario",
                                count = uiState.totalInventario,
                                icon = Icons.Default.Inventory2,
                                iconColor = Color(0xFF9C27B0),
                                bgColor = Color(0xFFF3E5F5),
                                modifier = Modifier.weight(1f)
                            )
                            SummaryCard(
                                title = "Total Disponibles",
                                count = uiState.totalDisponible,
                                icon = Icons.Default.CheckCircleOutline,
                                iconColor = Color(0xFF4CAF50),
                                bgColor = Color(0xFFE8F5E9),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            SummaryCard(
                                title = "En Lavandería",
                                count = uiState.totalLavanderia,
                                icon = Icons.Default.LocalLaundryService,
                                iconColor = Color(0xFF2196F3),
                                bgColor = Color(0xFFE3F2FD),
                                modifier = Modifier.weight(1f)
                            )
                            SummaryCard(
                                title = "Dañados",
                                count = uiState.totalDanado,
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
                                Text(
                                    "Artículo",
                                    modifier = Modifier.weight(2.5f),
                                    color = subTextColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Total",
                                    modifier = Modifier.weight(1f),
                                    color = subTextColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    "Disp.",
                                    modifier = Modifier.weight(1f),
                                    color = subTextColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    "Sucias",
                                    modifier = Modifier.weight(1f),
                                    color = subTextColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    "Dañados",
                                    modifier = Modifier.weight(1f),
                                    color = subTextColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                            // Filas - Iterar por categorías definidas
                            LAUNDRY_CATEGORIES.forEach { category ->
                                val stockItem = uiState.stockMap[category.key]
                                
                                LavanderiaInventoryRow(
                                    itemName = category.label,
                                    total = stockItem?.total ?: 0,
                                    disponible = stockItem?.disponible ?: 0,
                                    lavanderia = stockItem?.lavanderia ?: 0,
                                    danado = stockItem?.danado ?: 0,
                                    isSaving = uiState.isSaving,
                                    onTotalChange = { newVal ->
                                        viewModel.updateStockField(category.key, "total", newVal)
                                    },
                                    onDisponibleChange = { newVal ->
                                        viewModel.updateStockField(category.key, "disponible", newVal)
                                    },
                                    onLavanderiaChange = { newVal ->
                                        viewModel.updateStockField(category.key, "lavanderia", newVal)
                                    },
                                    onDanadoChange = { newVal ->
                                        viewModel.updateStockField(category.key, "danado", newVal)
                                    }
                                )
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
            
            // Indicador de guardado
            if (uiState.isSaving) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                )
            }
        }
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
fun LavanderiaInventoryRow(
    itemName: String,
    total: Int,
    disponible: Int,
    lavanderia: Int,
    danado: Int,
    isSaving: Boolean,
    onTotalChange: (Int) -> Unit,
    onDisponibleChange: (Int) -> Unit,
    onLavanderiaChange: (Int) -> Unit,
    onDanadoChange: (Int) -> Unit
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
            text = itemName,
            modifier = Modifier.weight(2.5f),
            color = textColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )

        // Cajas editables
        EditableNumberBox(
            value = total,
            onValueChange = onTotalChange,
            enabled = !isSaving,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(4.dp))
        EditableNumberBox(
            value = disponible,
            onValueChange = onDisponibleChange,
            enabled = !isSaving,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(4.dp))
        EditableNumberBox(
            value = lavanderia,
            onValueChange = onLavanderiaChange,
            enabled = !isSaving,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(4.dp))
        EditableNumberBox(
            value = danado,
            onValueChange = onDanadoChange,
            enabled = !isSaving,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun EditableNumberBox(
    value: Int,
    onValueChange: (Int) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    val boxBg = if (enabled) {
        MaterialTheme.colorScheme.background
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    }
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
        enabled = enabled,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        textStyle = TextStyle(
            color = if (enabled) textColor else textColor.copy(alpha = 0.6f),
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
                if (text.isEmpty()) {
                    Text("0", color = textColor.copy(alpha = 0.5f), fontSize = 12.sp)
                }
                innerTextField()
            }
        }
    )
}