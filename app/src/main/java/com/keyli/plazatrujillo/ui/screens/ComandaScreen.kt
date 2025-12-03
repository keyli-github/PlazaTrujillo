package com.keyli.plazatrujillo.ui.screens

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.navigation.NavHostController
import com.keyli.plazatrujillo.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

// --- DATA CLASS ---
data class BreakfastRowData(
    val id: Long,
    var habitacion: String = "",
    var nombres: String = "",
    var americano: String = "0",
    var continental: String = "0",
    var adicional: String = "0"
)

// Estados de la UI
enum class ReportState {
    IDLE, SAVING, SUCCESS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComandaScreen(navController: NavHostController) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Estado para la animación
    var reportState by remember { mutableStateOf(ReportState.IDLE) }

    // Datos del formulario
    var empleado by remember { mutableStateOf("Marco Castro") }
    var fecha by remember { mutableStateOf("02/12/2025") }
    val breakfastRows = remember { mutableStateListOf(BreakfastRowData(System.currentTimeMillis())) }

    // --- UI PRINCIPAL ---
    Box(modifier = Modifier.fillMaxSize()) {

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            // REMOVIDO: contentWindowInsets = WindowInsets(0.dp)
            topBar = {
                CenterAlignedTopAppBar(
                    // AJUSTE: Usar Modifier.statusBarsPadding() para evitar solapamiento con la barra de estado
                    modifier = Modifier.statusBarsPadding(),
                    title = {
                        Text(
                            "Reporte de Desayunos",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 20.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = MaterialTheme.colorScheme.onSurface)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                    ),
                    // AJUSTE: Usar WindowInsets.safeDrawing para el top bar
                    windowInsets = TopAppBarDefaults.windowInsets.exclude(WindowInsets.statusBars).exclude(WindowInsets.navigationBars)
                )
            },
            bottomBar = {
                // AJUSTE: El componente BottomActionButtons ya maneja padding,
                // pero añadimos navigationBarsPadding() en su interior o en el Surface si lo contiene
                BottomActionButtons(
                    onCancel = { navController.popBackStack() },
                    onSave = {
                        if (reportState == ReportState.IDLE) {
                            scope.launch {
                                reportState = ReportState.SAVING

                                // 1. Generar PDF
                                val generatedFile = withContext(Dispatchers.IO) {
                                    generateAndSavePdf(context, empleado, fecha, breakfastRows)
                                }

                                if (generatedFile != null) {
                                    // 2. Enviar Notificación (ALTA PRIORIDAD)
                                    showDownloadNotification(context, generatedFile)

                                    // 3. Mostrar animación éxito
                                    reportState = ReportState.SUCCESS
                                    delay(2500)
                                    navController.popBackStack()
                                } else {
                                    reportState = ReportState.IDLE
                                    Toast.makeText(context, "Error al guardar PDF", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                )
            }
        ) { paddingValues ->

            // AJUSTE: El paddingValues ya incluye las barras del sistema gracias a los ajustes anteriores
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    //.padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {

                Text(
                    text = "Información del Servicio",
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // --- CAMPOS ---
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    SoftInputData(label = "Empleado encargado", value = empleado, onValueChange = { empleado = it }, icon = Icons.Default.Person)
                    SoftInputData(label = "Fecha del reporte", value = fecha, onValueChange = { fecha = it }, icon = Icons.Default.CalendarToday)
                }

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
                Spacer(modifier = Modifier.height(24.dp))

                // --- TABLA HEADER ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Detalle por Habitación",
                        style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    )

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

                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)) {
                    TableHeader("Hab", 0.6f)
                    TableHeader("Huésped", 1.5f)
                    TableHeader("AME", 0.6f, TextAlign.Center)
                    TableHeader("CON", 0.6f, TextAlign.Center)
                    TableHeader("ADI", 0.6f, TextAlign.Center)
                    Spacer(modifier = Modifier.width(28.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))

                breakfastRows.forEachIndexed { index, row ->
                    BreakfastRowItem(
                        row = row,
                        onUpdate = { updatedRow -> breakfastRows[index] = updatedRow },
                        onDelete = {
                            if (breakfastRows.size > 1) breakfastRows.removeAt(index)
                            else breakfastRows[index] = BreakfastRowData(System.currentTimeMillis())
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // --- OVERLAY DE ÉXITO ---
        ReportSuccessOverlay(state = reportState)
    }
}

// ------------------------------------------------------------------------
// FUNCIONES PRIVADAS (sin cambios funcionales)
// ------------------------------------------------------------------------

private fun generateAndSavePdf(
    context: Context,
    empleado: String,
    fecha: String,
    rows: List<BreakfastRowData>
): File? {
    // ... (Tu función de generación de PDF se mantiene sin cambios)
    val pdfDocument = PdfDocument()
    val paint = Paint()

    val pageWidth = 600
    val pageHeight = 900
    val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas = page.canvas

    val colorOrange = android.graphics.Color.rgb(255, 87, 34)
    val colorWhite = android.graphics.Color.WHITE
    val colorBlack = android.graphics.Color.BLACK
    val colorGray = android.graphics.Color.DKGRAY

    var y = 50f

    // Título
    paint.color = colorBlack
    paint.textSize = 24f
    paint.isFakeBoldText = true
    canvas.drawText("Reporte de Desayunos", 30f, y, paint)

    y += 40f

    // Info
    paint.textSize = 14f
    paint.isFakeBoldText = false
    canvas.drawText("Empleado: $empleado", 30f, y, paint)
    paint.textAlign = Paint.Align.RIGHT
    canvas.drawText("Fecha: $fecha", (pageWidth - 30).toFloat(), y, paint)
    paint.textAlign = Paint.Align.LEFT

    y += 30f

    // Totales
    val totalAme = rows.sumOf { it.americano.toIntOrNull() ?: 0 }
    val totalCon = rows.sumOf { it.continental.toIntOrNull() ?: 0 }
    val totalAdi = rows.sumOf { it.adicional.toIntOrNull() ?: 0 }
    paint.color = colorGray
    canvas.drawText("Americ.: $totalAme   Contin.: $totalCon   Adici.: $totalAdi", 30f, y, paint)

    y += 30f

    // Header Tabla
    paint.color = colorOrange
    canvas.drawRect(20f, y, (pageWidth - 20).toFloat(), y + 30f, paint)

    paint.color = colorWhite
    paint.textSize = 12f
    paint.isFakeBoldText = true

    val xHab = 30f; val xNom = 100f; val xAme = 380f; val xCon = 450f; val xAdi = 520f
    val textY = y + 20f

    canvas.drawText("Hab", xHab, textY, paint)
    canvas.drawText("Nombres y Apellidos", xNom, textY, paint)
    canvas.drawText("Americ.", xAme, textY, paint)
    canvas.drawText("Contin.", xCon, textY, paint)
    canvas.drawText("Adici.", xAdi, textY, paint)

    y += 30f

    // Filas
    paint.color = colorBlack
    paint.isFakeBoldText = false
    paint.textSize = 12f

    var index = 0
    for (row in rows) {
        index++
        if (index % 2 == 0) {
            paint.color = android.graphics.Color.rgb(245, 245, 245)
            canvas.drawRect(20f, y, (pageWidth - 20).toFloat(), y + 30f, paint)
            paint.color = colorBlack
        }

        val rowY = y + 20f
        canvas.drawText(row.habitacion, xHab, rowY, paint)
        var cleanName = row.nombres
        if(cleanName.length > 35) cleanName = cleanName.substring(0, 32) + "..."
        canvas.drawText(cleanName, xNom, rowY, paint)
        canvas.drawText(row.americano, xAme + 10, rowY, paint)
        canvas.drawText(row.continental, xCon + 10, rowY, paint)
        canvas.drawText(row.adicional, xAdi + 10, rowY, paint)

        y += 30f
        if (y > pageHeight - 50) break
    }

    pdfDocument.finishPage(page)

    val fileName = "Reporte_Desayuno_${System.currentTimeMillis()}.pdf"
    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val file = File(downloadsDir, fileName)

    return try {
        pdfDocument.writeTo(FileOutputStream(file))
        pdfDocument.close()
        file
    } catch (e: IOException) {
        e.printStackTrace()
        pdfDocument.close()
        null
    }
}

private fun showDownloadNotification(context: Context, file: File) {
    // ... (Tu función de notificación se mantiene sin cambios)
    val channelId = "downloads_high_priority"
    val notificationId = (System.currentTimeMillis() % 10000).toInt()
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val intent = Intent(DownloadManager.ACTION_VIEW_DOWNLOADS).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent = PendingIntent.getActivity(
        context, 0, intent,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
    )

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "Descargas Importantes",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifica cuando se genera un reporte PDF"
            enableVibration(true)
            setShowBadge(true)
        }
        notificationManager.createNotificationChannel(channel)
    }

    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.stat_sys_download_done)
        .setContentTitle("Reporte PDF Guardado")
        .setContentText("Toca para ver tus descargas: ${file.name}")
        .setPriority(NotificationCompat.PRIORITY_MAX)
        .setDefaults(NotificationCompat.DEFAULT_ALL)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .build()

    notificationManager.notify(notificationId, notification)
}

// ------------------------------------------------------------------------
// UI AUXILIAR (sin cambios funcionales)
// ------------------------------------------------------------------------

@Composable
private fun isComandaDarkTheme(): Boolean {
    // Si el color de la superficie es oscuro, entonces estamos en Dark Mode
    return MaterialTheme.colorScheme.surface.luminance() < 0.5f
}

@Composable
private fun SoftInputData(label: String, value: String, onValueChange: (String) -> Unit, icon: ImageVector) {
    // AQUI USAMOS EL NUEVO NOMBRE
    val isDark = isComandaDarkTheme()
    val inputBgColor = if (isDark) Color(0xFF333333) else Color(0xFFF5F6F9)

    Column {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(inputBgColor)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(12.dp))
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
    }
}

@Composable
private fun RowScope.TableInputCell(
    value: String,
    onValueChange: (String) -> Unit,
    weight: Float,
    isNumber: Boolean = false,
    centerText: Boolean = false
) {
    // AQUI USAMOS EL NUEVO NOMBRE
    val isDark = isComandaDarkTheme()
    val cellBgColor = if (isDark) Color(0xFF333333) else Color(0xFFF5F6F9)

    Box(
        modifier = Modifier
            .weight(weight)
            .height(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(cellBgColor)
            .padding(horizontal = 8.dp),
        contentAlignment = if (centerText) Alignment.Center else Alignment.CenterStart
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = if (centerText) TextAlign.Center else TextAlign.Start
            ),
            keyboardOptions = if (isNumber) KeyboardOptions(keyboardType = KeyboardType.Number) else KeyboardOptions.Default,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun RowScope.TableHeader(text: String, weight: Float, textAlign: TextAlign = TextAlign.Start) {
    Text(
        text = text,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        modifier = Modifier.weight(weight),
        textAlign = textAlign
    )
}

@Composable
private fun BreakfastRowItem(row: BreakfastRowData, onUpdate: (BreakfastRowData) -> Unit, onDelete: () -> Unit) {
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
                .background(StatusRed.copy(alpha = 0.1f))
                .clickable { onDelete() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = StatusRed, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun BottomActionButtons(onCancel: () -> Unit, onSave: () -> Unit) {
    Surface(
        shadowElevation = 12.dp,
        color = MaterialTheme.colorScheme.surface,
        // AJUSTE: Usar navigationBarsPadding() para evitar solapamiento con la barra de navegación inferior
        modifier = Modifier.navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            ) {
                Text("Cancelar", fontWeight = FontWeight.SemiBold)
            }

            Button(
                onClick = onSave,
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary, contentColor = Color.White),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Text("Guardar", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun ReportSuccessOverlay(state: ReportState) {
    // ... (Tu overlay de éxito se mantiene sin cambios)
    AnimatedVisibility(
        visible = state != ReportState.IDLE,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
                .clickable(enabled = false) {},
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 15.dp,
                modifier = Modifier
                    .padding(32.dp)
                    .widthIn(min = 280.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(vertical = 40.dp, horizontal = 24.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        androidx.compose.animation.AnimatedVisibility(
                            visible = state == ReportState.SAVING,
                            exit = fadeOut(animationSpec = tween(200))
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(60.dp),
                                color = OrangePrimary,
                                strokeWidth = 5.dp
                            )
                        }

                        androidx.compose.animation.AnimatedVisibility(
                            visible = state == ReportState.SUCCESS,
                            enter = scaleIn(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(OrangePrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(45.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    val titleText = if(state == ReportState.SAVING) "Generando PDF..." else "¡Guardado!"
                    val subText = if(state == ReportState.SAVING) "Por favor espera..." else "Guardado/Descargado."

                    Text(
                        text = titleText,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = subText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}