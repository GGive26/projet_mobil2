package com.example.projet_mobil2

import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import java.util.Locale
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    private lateinit var adapter: EntrepotAdapter

    // Variables pour la gestion des notifications
    private lateinit var notificationHelper: NotificationHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialisation de notificationHelper pour gérer les notifications
        notificationHelper = NotificationHelper(this)
        NotificationHelper.createNotificationChannel(this)
        NotificationHelper.checkAndRequestNotificationPermission(this)

        // Boutons pour changer la langue de l'application
        val btnEnglish = findViewById<Button>(R.id.btnEnglish)
        val btnFrench = findViewById<Button>(R.id.btnFrench)

        // Définir les listeners pour changer la langue en fonction du bouton cliqué
        btnEnglish.setOnClickListener { setLocale("en") }
        btnFrench.setOnClickListener { setLocale("fr") }

        dbHelper = DatabaseHelper(this)
        val Btn_demarrer=findViewById<Button>(R.id.btnDemarrer);

        val switch=Intent(this,Activity2::class.java);

        Btn_demarrer.setOnClickListener { startActivity(switch) }
    }

    // Méthode pour changer la langue de l'application
    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        // Recharger l'activité pour appliquer la nouvelle langue
        recreate()
    }

    // Sauvegarde de l'état de l'adapter lors de la rotation de l'écran
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Ajoutez ici des sauvegardes spécifiques si nécessaire, comme l'état d'une liste par exemple
        // outState.putParcelableArrayList("entrepot_list", ArrayList(adapter.entrepotList))
    }

    // Gère les résultats des demandes de permissions
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NotificationHelper.NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission de notification acceptée
                Toast.makeText(this, "Permission de notification acceptée", Toast.LENGTH_SHORT).show()
            } else {
                // Permission de notification refusée
                Toast.makeText(this, "Permission de notification refusée", Toast.LENGTH_SHORT).show()
            }
        }
    }
}