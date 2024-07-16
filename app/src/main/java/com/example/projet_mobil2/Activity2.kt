package com.example.projet_mobil2

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Activity2 : AppCompatActivity() {

    private lateinit var nom_entrepot:EditText
    private lateinit var type_entrepot:EditText
    private lateinit var temp_actuel: EditText
    private lateinit var humidite_actuel:EditText
    private lateinit var temperature_max:EditText
    private lateinit var temperature_min:EditText
    private lateinit var humidite_max:EditText
    private lateinit var humidite_min:EditText
    private lateinit var longitude:EditText
    private lateinit var latitude:EditText
    private lateinit var adresse:EditText
    private lateinit var date_stockage:EditText

    private var id=-1
    private val calendar = Calendar.getInstance()


    // base de donnees locale
    private lateinit var database:DatabaseHelper



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_2)


        // Initialisation des vues
        nom_entrepot = findViewById(R.id.edit_nom)
        type_entrepot = findViewById(R.id.edit_type)
        longitude = findViewById(R.id.edit_longitude)
        latitude = findViewById(R.id.edit_latitude)
        humidite_max = findViewById(R.id.edit_hum_max)
        humidite_min = findViewById(R.id.edit_hum_min)
        temperature_max = findViewById(R.id.edit_temp_max)
        temperature_min = findViewById(R.id.edit_temp_min)

        // Gestion du sélecteur de date
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateLabel()
        }
        date_stockage.setOnClickListener {
            DatePickerDialog(
                this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

    }
    private fun verifierFormulaire(nom: String, type: String, date_stockage: String, temp_max: String,temp_min:Int,hum_max:Int,hum_min:Int,long:String,lat:String): Boolean {
        // Vérification du titre
        if (nom.isEmpty()) {
            nom_entrepot.error = "Le nom de l'entrepot doit être non vide"
            return false
        }

        // Vérification de l'auteur
        if (type.isEmpty()) {
            type_entrepot.error = "le type  doit être non vide"
            return false
        }

        // Vérification de la date de publication
        if (date_stockage.isEmpty() || !date_stockage.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
            date_stockage.error = "Date de publication invalide"
            return false
        }

        // Vérification du numéro ISBN
        if (temp_max.isEmpty() || temp_max.length != 13) {
            temp_max.error = "La temperature max est invalide"
            return false
        }
        // Vérification du titre
        if (temp_min.isEmpty()) {
            temp_min.error = "La temperature min est invalide"
            return false
        }

        // Vérification de l'auteur
        if (type.isEmpty()) {
            type_entrepot.error = "le type  doit être non vide"
            return false
        }
        // Vérification du titre
        if (nom.isEmpty()) {
            nom_entrepot.error = "Le nom de l'entrepot doit être non vide"
            return false
        }

        // Vérification de l'auteur
        if (type.isEmpty()) {
            type_entrepot.error = "le type  doit être non vide"
            return false
        }

        return true
    }
    private fun updateDateLabel() {
        val format = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(format, Locale.US)
        date_stockage.setText(sdf.format(calendar.time))
    }
}