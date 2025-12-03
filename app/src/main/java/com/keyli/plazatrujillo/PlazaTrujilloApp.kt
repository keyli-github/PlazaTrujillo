package com.keyli.plazatrujillo

import android.app.Application
import com.google.firebase.FirebaseApp
import com.keyli.plazatrujillo.service.MessageNotificationManager
import com.keyli.plazatrujillo.util.NotificationHelper

class PlazaTrujilloApp : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Inicializar Firebase
        FirebaseApp.initializeApp(this)
        
        // Crear canal de notificaciones
        NotificationHelper.createNotificationChannel(this)
        
        // Inicializar el manager de notificaciones de mensajes
        MessageNotificationManager.initialize(this)
    }
}
