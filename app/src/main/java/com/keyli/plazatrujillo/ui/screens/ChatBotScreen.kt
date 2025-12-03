package com.keyli.plazatrujillo.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Refresh
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.keyli.plazatrujillo.ui.theme.*
import com.keyli.plazatrujillo.ui.viewmodel.ChatbotViewModel
import com.keyli.plazatrujillo.ui.viewmodel.ChatMessage
import com.keyli.plazatrujillo.ui.viewmodel.MessageType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBotScreen(
    navController: NavHostController,
    chatbotViewModel: ChatbotViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    
    val uiState by chatbotViewModel.uiState.collectAsState()
    var input by remember { mutableStateOf("") }

    // --- COLORES DINÁMICOS ---
    val bgColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onSurface
    val borderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    
    // Auto-scroll cuando hay nuevos mensajes
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            delay(100)
            listState.animateScrollToItem(uiState.messages.lastIndex)
        }
    }
    
    // Mostrar error si existe
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // El error se muestra como mensaje del bot, limpiar después de mostrar
            delay(3000)
            chatbotViewModel.clearError()
        }
    }

    fun sendMessage(text: String) {
        if (text.isNotBlank()) {
            chatbotViewModel.sendMessage(text)
            input = ""
        }
    }

    Scaffold(
        containerColor = bgColor,
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(bgColor)
                    .imePadding()
            ) {
                // SUGERENCIAS
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(chatbotViewModel.suggestedQuestions) { question ->
                        Surface(
                            onClick = { sendMessage(question) },
                            shape = RoundedCornerShape(16.dp),
                            color = surfaceColor,
                            border = BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = question,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                color = textColor,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                // INPUT DE TEXTO
                Surface(
                    color = surfaceColor,
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
                            placeholder = { Text("Escribe una consulta...", color = textColor.copy(alpha = 0.5f)) },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            shape = RoundedCornerShape(26.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = OrangePrimary,
                                unfocusedBorderColor = textColor.copy(alpha = 0.3f),
                                focusedContainerColor = bgColor,
                                unfocusedContainerColor = bgColor,
                                focusedTextColor = textColor,
                                unfocusedTextColor = textColor
                            ),
                            singleLine = true,
                            enabled = !uiState.isLoading
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        FloatingActionButton(
                            onClick = { sendMessage(input) },
                            containerColor = if (uiState.isLoading) OrangePrimary.copy(alpha = 0.5f) else OrangePrimary,
                            contentColor = Color.White,
                            shape = CircleShape,
                            modifier = Modifier.size(50.dp)
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(Icons.Default.Send, contentDescription = "Enviar")
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding()
                )
        ) {
            // HEADER DEL ASISTENTE
            Surface(
                color = surfaceColor,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, borderColor)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
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
                            Text("Asistente IA", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textColor)
                            Text(
                                text = if (uiState.isTyping) "Escribiendo..." else "En línea",
                                color = if (uiState.isTyping) OrangePrimary else StatusGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    // Botón para nueva sesión
                    IconButton(
                        onClick = { chatbotViewModel.endSession() }
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Nueva conversación",
                            tint = textColor.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            // LISTA DE MENSAJES
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(uiState.messages) { _, msg ->
                    ChatBubble(message = msg)
                }
                if (uiState.isTyping) {
                    item { TypingIndicator() }
                }
            }
        }
    }
}

// --- COMPONENTES VISUALES ---
@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.type == MessageType.USER

    val bubbleColor = if (isUser) OrangePrimary else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface

    val align = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    val shape = if (isUser) RoundedCornerShape(16.dp, 4.dp, 16.dp, 16.dp) else RoundedCornerShape(4.dp, 16.dp, 16.dp, 16.dp)

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = align) {
        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.widthIn(max = 300.dp)) {
            if (!isUser) {
                Icon(Icons.Default.AutoAwesome, null, tint = OrangePrimary, modifier = Modifier.size(24.dp).padding(bottom = 4.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Surface(
                color = bubbleColor,
                shape = shape,
                shadowElevation = 1.dp,
                border = null
            ) {
                Text(text = message.text, color = textColor, modifier = Modifier.padding(14.dp), fontSize = 15.sp, lineHeight = 22.sp)
            }
            if (isUser) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.Person, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(24.dp).padding(bottom = 4.dp))
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

    val bubbleColor = MaterialTheme.colorScheme.surfaceVariant
    val dotColor = MaterialTheme.colorScheme.onSurface

    Row(verticalAlignment = Alignment.Bottom) {
        Icon(Icons.Default.AutoAwesome, null, tint = OrangePrimary, modifier = Modifier.size(24.dp).padding(bottom = 4.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Surface(color = bubbleColor, shape = RoundedCornerShape(4.dp, 16.dp, 16.dp, 16.dp), shadowElevation = 2.dp, modifier = Modifier.height(40.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp)) {
                Box(Modifier.size(8.dp).clip(CircleShape).background(dotColor.copy(alpha = alpha1)))
                Spacer(Modifier.width(4.dp))
                Box(Modifier.size(8.dp).clip(CircleShape).background(dotColor.copy(alpha = alpha2)))
                Spacer(Modifier.width(4.dp))
                Box(Modifier.size(8.dp).clip(CircleShape).background(dotColor.copy(alpha = alpha3)))
            }
        }
    }
}