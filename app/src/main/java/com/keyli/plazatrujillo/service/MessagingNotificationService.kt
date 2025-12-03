package com.keyli.plazatrujillo.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.keyli.plazatrujillo.MainActivity
import com.keyli.plazatrujillo.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MessagingNotificationService : FirebaseMessagingService() {
    
    companion object {
        private const val TAG = "FCMService"
        private const val CHANNEL_ID = "messages_channel"
        private const val CHANNEL_NAME = "Mensajes"
        
        // Singleton para acceso al token desde otras partes de la app
        var fcmToken: String? = null
            private set
    }
    
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "FCM Token refreshed: $token")
        fcmToken = token
        
        // Enviar el nuevo token al servidor si el usuario está autenticado
        sendTokenToServer(token)
    }
    
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        Log.d(TAG, "Message received from: ${remoteMessage.from}")
        
        // Verificar si hay datos en el mensaje
        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "Message data: ${remoteMessage.data}")
            handleDataMessage(remoteMessage.data)
        }
        
        // Verificar si hay notificación
        remoteMessage.notification?.let {
            Log.d(TAG, "Notification: ${it.title} - ${it.body}")
            showNotification(it.title ?: "Nuevo mensaje", it.body ?: "")
        }
    }
    
    private fun handleDataMessage(data: Map<String, String>) {
        val type = data["type"] ?: return
        
        when (type) {
            "new_message" -> {
                val senderName = data["sender_name"] ?: "Usuario"
                val senderUid = data["sender_uid"] ?: ""
                val messageText = data["message"] ?: ""
                val messageType = data["message_type"] ?: "text"
                
                // No mostrar notificación si el mensaje es del usuario actual
                val currentUid = FirebaseAuth.getInstance().currentUser?.uid
                if (senderUid == currentUid) return
                
                val notificationText = when (messageType) {
                    "image" -> "📷 Te envió una imagen"
                    "file" -> "📎 Te envió un archivo"
                    else -> messageText
                }
                
                showNotification(
                    title = "Mensaje de $senderName",
                    body = notificationText,
                    data = data
                )
            }
        }
    }
    
    private fun showNotification(title: String, body: String, data: Map<String, String>? = null) {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            data?.let { d ->
                putExtra("navigate_to", "messages")
                putExtra("sender_uid", d["sender_uid"])
            }
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setVibrate(longArrayOf(0, 250, 250, 250))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
        
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Crear canal de notificación para Android O+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones de mensajes"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 250, 250)
            }
            notificationManager.createNotificationChannel(channel)
        }
        
        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }
    
    private fun sendTokenToServer(token: String) {
        // Enviar token al backend cuando el usuario esté autenticado
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // TODO: Implementar endpoint en el backend para guardar el token FCM
                    // ApiClient.create().updateFcmToken(token)
                    Log.d(TAG, "Token sent to server for user: ${currentUser.uid}")
                } catch (e: Exception) {
                    Log.e(TAG, "Error sending token to server", e)
                }
            }
        }
    }
}
