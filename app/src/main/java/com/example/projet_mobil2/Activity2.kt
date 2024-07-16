package com.example.projet_mobil2

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
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
    private lateinit var confirmerButton :Button

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
        confirmerButton=findViewById(R.id.btn_confirmation)

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
        confirmerButton.setOnClickListener {
            val nom = nom_entrepot.text.toString()
            val type = type_entrepot.text.toString()
            val date_stock = date_stockage.text.toString()
            val tempMax = temperature_max.text.toString()
            val tempMin = temperature_min.text.toString()
            val humMax = humidite_max.text.toString()
            val humMin = humidite_min.text.toString()
            val long = longitude.text.toString()
            val lat = latitude.text.toString()

            if (verifierFormulaire(nom, type, date_stock, tempMax,tempMin,humMax,humMin,long,lat)) {
                // Préparation du résultat pour retourner à MainActivity
                val resultIntent = Intent()
                resultIntent.putExtra("id", id)
                resultIntent.putExtra("type", type)
                resultIntent.putExtra("temperature_max", tempMax)
                resultIntent.putExtra("temperature_min", tempMin)
                resultIntent.putExtra("humidite_max", humMax)
                resultIntent.putExtra("humidite_min", humMin)
               // resultIntent.putExtra("temperature_act")
                //resultIntent.putExtra("humidite_act", auteur)
                //resultIntent.putExtra("adresse", datePublication)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }

    }
    private fun verifierFormulaire(nom: String, type: String, date_stock: String, tempMax: String,tempMin: String,humMax: String,humMin: String,long: String,lat: String): Boolean {
        // Vérification du nom
        if (nom.isEmpty()) {
            nom_entrepot.error = "Le nom de l'entrepot doit être non vide"
            return false
        }

        // Vérification du type
        if (type.isEmpty()) {
            type_entrepot.error = "le type  doit être non vide"
            return false
        }

        // Vérification de la date de stockage
        if (date_stock.isEmpty() || !date_stock.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
            return false
        }

        // Vérification de la temperature max
        if (tempMax.isEmpty() || tempMax.length != 13) {
            return false
        }
        // Vérification de la temperature minimale
        if (tempMin.isEmpty()) {
            return false
        }

        // Vérification de la humidite min
        if (humMin.isEmpty()) {
            return false
        }
        // Vérification de la humidite max
        if (humMax.isEmpty()) {
            return false
        }

        // Vérification de la longitude
        if (long.isEmpty()) {
            return false
        }
        // Vérification de la latitude
        if (lat.isEmpty()) {
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