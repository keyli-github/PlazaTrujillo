package com.keyli.plazatrujillo.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.keyli.plazatrujillo.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: String,
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBotScreen(navController: NavHostController) {
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val messages = remember { mutableStateListOf<ChatMessage>() }
    var isBotTyping by remember { mutableStateOf(false) }
    var input by remember { mutableStateOf("") }

    val suggestedQuestions = listOf(
        "¿Cuáles son las ganancias del mes?",
        "¿Cuál es la tasa de ocupación actual?",
        "¿Cuántas reservas tenemos hoy?",
        "Muéstrame los ingresos de esta semana",
        "¿Qué habitaciones están disponibles?",
        "Resumen de check-ins de hoy"
    )

    LaunchedEffect(Unit) {
        if (messages.isEmpty()) {
            isBotTyping = true
            delay(800)
            messages.add(
                ChatMessage(
                    id = "welcome",
                    text = "¡Hola! Soy tu asistente virtual de Plaza Trujillo. 🏨✨\n\nPuedes preguntarme sobre finanzas, ocupación o estado del hotel. Selecciona una opción abajo o escribe tu consulta.",
                    isUser = false
                )
            )
            isBotTyping = false
        }
    }

    suspend fun processMessage(userText: String) {
        messages.add(ChatMessage(id = System.nanoTime().toString(), text = userText, isUser = true))
        input = ""
        scope.launch {
            delay(100)
            listState.animateScrollToItem(messages.size)
        }

        isBotTyping = true
        delay(1500)

        val lowerText = userText.lowercase()
        val replyText = when {
            "ganancias" in lowerText && "mes" in lowerText -> "💰 **Ganancias de Diciembre:**\n\nHasta el momento, las ganancias netas son de **S/ 45,230.00**.\nEsto representa un aumento del 12% respecto al mes anterior."
            "ocupación" in lowerText || "tasa" in lowerText -> "📈 **Tasa de Ocupación:**\n\nActualmente el hotel está al **78% de su capacidad**.\nHay 35 habitaciones ocupadas de 45 disponibles."
            "reservas" in lowerText && "hoy" in lowerText -> "📅 **Reservas para Hoy:**\n\nTenemos **8 nuevas reservas** confirmadas para ingresar el día de hoy.\n3 de ellas son VIP (Suite)."
            "ingresos" in lowerText && "semana" in lowerText -> "📊 **Ingresos de la Semana:**\n\n• Lunes: S/ 3,200\n• Martes: S/ 4,100\n• Miércoles: S/ 3,800\n\n**Total acumulado:** S/ 11,100."
            "disponibles" in lowerText -> "🛏️ **Habitaciones Disponibles:**\n\nQuedan **10 habitaciones** libres ahora mismo:\n• 4 Simples\n• 4 Dobles\n• 2 Matrimoniales"
            "check-in" in lowerText || "resumen" in lowerText -> "🛎️ **Resumen de Check-ins:**\n\n• Realizados: 5\n• Pendientes: 3\n• No-shows: 0\n\nEl próximo check-in está programado para las 14:30 hrs."
            else -> "Entiendo tu consulta sobre '$userText', pero necesito consultar la base de datos central. ¿Podrías intentar con una de las opciones sugeridas?"
        }

        isBotTyping = false
        messages.add(ChatMessage(id = System.nanoTime().toString(), text = replyText, isUser = false))
        scope.launch {
            delay(100)
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    Scaffold(
        containerColor = LightBackground,
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LightBackground)
                    .imePadding()
            ) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(suggestedQuestions) { question ->
                        Surface(
                            onClick = { scope.launch { processMessage(question) } },
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White,
                            border = BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = question,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                color = TextBlack,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Surface(
                    color = Color.White,
                    shadowElevation = 12.dp,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = input,
                            onValueChange = { input = it },
                            placeholder = { Text("Escribe una consulta...", color = TextGray) },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            shape = RoundedCornerShape(26.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = OrangePrimary,
                                unfocusedBorderColor = TextGray.copy(alpha = 0.3f),
                                focusedContainerColor = LightBackground,
                                unfocusedContainerColor = LightBackground
                            ),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        FloatingActionButton(
                            onClick = {
                                if (input.isNotBlank()) { scope.launch { processMessage(input) } }
                            },
                            containerColor = OrangePrimary,
                            contentColor = Color.White,
                            shape = CircleShape,
                            modifier = Modifier.size(50.dp)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Enviar")
                        }
                    }
                }
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
            // AQUI ESTABA EL ERROR: NO AGREGAMOS PADDING ARRIBA
        ) {
            // HEADER DEL ASISTENTE (PEGADO ARRIBA)
            // Le quité el 'statusBarsPadding' y le puse un borde inferior sutil
            Surface(
                color = Color.White,
                shadowElevation = 2.dp, // Sombra pequeña para separar del contenido
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, Color(0xFFEEEEEE)) // Borde sutil abajo
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(OrangePrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Asistente IA", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextBlack)
                        Text(
                            text = if (isBotTyping) "Escribiendo..." else "En línea",
                            color = if (isBotTyping) OrangePrimary else StatusGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // LISTA DE MENSAJES
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(messages) { _, msg ->
                    ChatBubble(message = msg)
                }
                if (isBotTyping) {
                    item { TypingIndicator() }
                }
            }
        }
    }
}

// --- COMPONENTES VISUALES ---
@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.isUser
    val bubbleColor = if (isUser) OrangePrimary else Color.White
    val textColor = if (isUser) Color.White else TextBlack
    val align = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    val shape = if (isUser) RoundedCornerShape(16.dp, 4.dp, 16.dp, 16.dp) else RoundedCornerShape(4.dp, 16.dp, 16.dp, 16.dp)

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = align) {
        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.widthIn(max = 300.dp)) {
            if (!isUser) {
                Icon(Icons.Default.AutoAwesome, null, tint = OrangePrimary, modifier = Modifier.size(24.dp).padding(bottom = 4.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Surface(color = bubbleColor, shape = shape, shadowElevation = 2.dp, border = if(!isUser) BorderStroke(1.dp, Color(0xFFEEEEEE)) else null) {
                Text(text = message.text, color = textColor, modifier = Modifier.padding(14.dp), fontSize = 15.sp, lineHeight = 22.sp)
            }
            if (isUser) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.Person, null, tint = TextGray, modifier = Modifier.size(24.dp).padding(bottom = 4.dp))
            }
        }
    }
}

@Composable
fun TypingIndicator() {
    val transition = rememberInfiniteTransition(label = "typing")
    val alpha1 by transition.animateFloat(0.3f, 1f, infiniteRepeatable(tween(600), RepeatMode.Reverse), "dot1")
    val alpha2 by transition.animateFloat(0.3f, 1f, infiniteRepeatable(tween(600, 200), RepeatMode.Reverse), "dot2")
    val alpha3 by transition.animateFloat(0.3f, 1f, infiniteRepeatable(tween(600, 400), RepeatMode.Reverse), "dot3")

    Row(verticalAlignment = Alignment.Bottom) {
        Icon(Icons.Default.AutoAwesome, null, tint = OrangePrimary, modifier = Modifier.size(24.dp).padding(bottom = 4.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Surface(color = Color.White, shape = RoundedCornerShape(4.dp, 16.dp, 16.dp, 16.dp), shadowElevation = 2.dp, modifier = Modifier.height(40.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp)) {
                Box(Modifier.size(8.dp).clip(CircleShape).background(TextGray.copy(alpha = alpha1)))
                Spacer(Modifier.width(4.dp))
                Box(Modifier.size(8.dp).clip(CircleShape).background(TextGray.copy(alpha = alpha2)))
                Spacer(Modifier.width(4.dp))
                Box(Modifier.size(8.dp).clip(CircleShape).background(TextGray.copy(alpha = alpha3)))
            }
        }
    }
}