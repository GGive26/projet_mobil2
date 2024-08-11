package com.example.projet_mobil2

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationHelper (private val context: Context){

    companion object {
        // Constantes pour le canal de notification
        const val CHANNEL_ID = "TACHE_NOTIFICATION_CHANNEL"
        const val CHANNEL_NAME = "Tache Notifications"
        const val CHANNEL_DESCRIPTION = "Notifications pour les tâches"
        const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001

        /**
         * Crée un canal de notification pour les appareils exécutant Android Oreo (API 26) ou version ultérieure.
         * Un canal de notification est nécessaire pour afficher des notifications sur ces versions d'Android.
         */
        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                    description = CHANNEL_DESCRIPTION
                }
                val notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }

        /**
         * Vérifie et demande la permission de poster des notifications si nécessaire.
         * Retourne true si la permission est déjà accordée, sinon demande la permission et retourne false.
         */
        fun checkAndRequestNotificationPermission(activity: Activity): Boolean {
            return if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_REQUEST_CODE
                )
                false
            } else {
                true
            }
        }
    }

    /**
     * Envoie une notification avec le titre et le message spécifiés.
     * Vérifie que la permission de poster des notifications est accordée avant d'envoyer la notification.
     */
    fun sendNotification(title: String, message: String) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_temp)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

            with(NotificationManagerCompat.from(context)) {
                notify(System.currentTimeMillis().toInt(), builder.build())
            }
        }
    }


}