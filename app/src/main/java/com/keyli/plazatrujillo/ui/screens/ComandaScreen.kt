package com.keyli.plazatrujillo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.keyli.plazatrujillo.ui.theme.OrangePrimary
import com.keyli.plazatrujillo.ui.theme.StatusRed
import com.keyli.plazatrujillo.ui.theme.TextBlack
import com.keyli.plazatrujillo.ui.theme.TextGray

// --- DATA CLASS ---
data class BreakfastRowData(
    val id: Long,
    var habitacion: String = "",
    var nombres: String = "",
    var americano: String = "0",
    var continental: String = "0",
    var adicional: String = "0"
)

// --- COLORES ---
private val BgInputColor = Color(0xFFF5F6F9)
private val BgScreenColor = Color.White
private val DividerColor = Color(0xFFEEEEEE)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComandaScreen(navController: NavHostController) {
    val scrollState = rememberScrollState()

    // Estados
    var empleado by remember { mutableStateOf("Marco Gutierrez") }
    var fecha by remember { mutableStateOf("30/11/2025") }
    val breakfastRows = remember { mutableStateListOf(BreakfastRowData(System.currentTimeMillis())) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BgScreenColor,
        // Al poner windowInsets(0) nos aseguramos que no agregue espacio extra arriba
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Reporte de Desayunos",
                        fontWeight = FontWeight.Bold,
                        color = TextBlack,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Atrás",
                            tint = TextBlack
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = BgScreenColor,
                    titleContentColor = TextBlack,
                    navigationIconContentColor = TextBlack
                ),
                // Esto es clave para que quede al ras si el NavigationWrapper lo permite
                windowInsets = WindowInsets(0.dp)
            )
        },
        bottomBar = {
            BottomActionButtons(
                onCancel = { navController.popBackStack() },
                onSave = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {

            // Subtítulo
            Text(
                text = "Información del Servicio",
                style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextGray),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // --- CAMPOS PRINCIPALES ---
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                SoftInputData(
                    label = "Empleado encargado",
                    value = empleado,
                    onValueChange = { empleado = it },
                    icon = Icons.Default.Person
                )

                SoftInputData(
                    label = "Fecha del reporte",
                    value = fecha,
                    onValueChange = { fecha = it },
                    icon = Icons.Default.CalendarToday
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Divider(color = DividerColor, thickness = 1.dp)
            Spacer(modifier = Modifier.height(24.dp))

            // --- SECCIÓN TABLA ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Detalle por Habitación",
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                )

                // Botón Añadir
                TextButton(
                    onClick = { breakfastRows.add(BreakfastRowData(System.currentTimeMillis())) },
                    colors = ButtonDefaults.textButtonColors(contentColor = OrangePrimary)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Agregar fila", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Cabeceras
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)) {
                TableHeader("Hab", 0.6f)
                TableHeader("Huésped", 1.5f)
                TableHeader("AME", 0.6f, TextAlign.Center)
                TableHeader("CON", 0.6f, TextAlign.Center)
                TableHeader("ADI", 0.6f, TextAlign.Center)
                Spacer(modifier = Modifier.width(28.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Filas
            breakfastRows.forEachIndexed { index, row ->
                BreakfastRowItem(
                    row = row,
                    onUpdate = { updatedRow -> breakfastRows[index] = updatedRow },
                    onDelete = {
                        if (breakfastRows.size > 1) {
                            breakfastRows.removeAt(index)
                        } else {
                            breakfastRows[index] = BreakfastRowData(System.currentTimeMillis())
                        }
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

// --- COMPONENTES UI PERSONALIZADOS ---

@Composable
fun BottomActionButtons(onCancel: () -> Unit, onSave: () -> Unit) {
    Surface(
        shadowElevation = 12.dp,
        color = BgScreenColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDDDDDD)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextGray)
            ) {
                Text("Cancelar", fontWeight = FontWeight.SemiBold)
            }

            Button(
                onClick = onSave,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = OrangePrimary,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text("Guardar", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun SoftInputData(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector
) {
    Column {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextBlack,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(BgInputColor)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = OrangePrimary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(
                    fontSize = 15.sp,
                    color = TextBlack,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
    }
}

@Composable
fun RowScope.TableHeader(text: String, weight: Float, textAlign: TextAlign = TextAlign.Start) {
    Text(
        text = text,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = TextGray,
        modifier = Modifier.weight(weight),
        textAlign = textAlign
    )
}

@Composable
fun BreakfastRowItem(
    row: BreakfastRowData,
    onUpdate: (BreakfastRowData) -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TableInputCell(row.habitacion, { onUpdate(row.copy(habitacion = it)) }, 0.6f, true)
        TableInputCell(row.nombres, { onUpdate(row.copy(nombres = it)) }, 1.5f, false)
        TableInputCell(row.americano, { onUpdate(row.copy(americano = it)) }, 0.6f, true, true)
        TableInputCell(row.continental, { onUpdate(row.copy(continental = it)) }, 0.6f, true, true)
        TableInputCell(row.adicional, { onUpdate(row.copy(adicional = it)) }, 0.6f, true, true)

        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFEBEE))
                .clickable { onDelete() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Eliminar",
                tint = StatusRed,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun RowScope.TableInputCell(
    value: String,
    onValueChange: (String) -> Unit,
    weight: Float,
    isNumber: Boolean = false,
    centerText: Boolean = false
) {
    Box(
        modifier = Modifier
            .weight(weight)
            .height(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(BgInputColor)
            .padding(horizontal = 8.dp),
        contentAlignment = if (centerText) Alignment.Center else Alignment.CenterStart
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                fontSize = 13.sp,
                color = TextBlack,
                textAlign = if (centerText) TextAlign.Center else TextAlign.Start
            ),
            keyboardOptions = if (isNumber) KeyboardOptions(keyboardType = KeyboardType.Number) else KeyboardOptions.Default,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}