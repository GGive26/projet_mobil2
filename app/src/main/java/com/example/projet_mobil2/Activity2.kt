package com.example.projet_mobil2

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.time.LocalDate


class Activity2 : AppCompatActivity() {

    private lateinit var nom_entrepot:EditText
    private lateinit var type_entrepot:EditText
   // private lateinit var temp_actuel: EditText
   // private lateinit var humidite_actuel:EditText
    private lateinit var temperature_max:EditText
    private lateinit var temperature_min:EditText
    private lateinit var humidite_max:EditText
    private lateinit var humidite_min:EditText
   // private lateinit var longitude:EditText
    //private lateinit var latitude:EditText
   // private lateinit var adresse:EditText
    private lateinit var date_stockage:Button
    private lateinit var confirmerButton :Button
    var id=-1
    private val calendar = Calendar.getInstance()

    // base de donnees locale
    private lateinit var database:DatabaseHelper

    private lateinit var date_stock_aff:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_2)

        var date :String
        // Initialisation des vues
        nom_entrepot = findViewById(R.id.edit_nom)
        type_entrepot = findViewById(R.id.edit_type)
       // longitude = findViewById(R.id.edit_longitude)
        //latitude = findViewById(R.id.edit_latitude)
        humidite_max = findViewById(R.id.edit_hum_max)
        humidite_min = findViewById(R.id.edit_hum_min)
        temperature_max = findViewById(R.id.edit_temp_max)
        temperature_min = findViewById(R.id.edit_temp_min)
        confirmerButton=findViewById(R.id.btn_confirmation)
        date_stockage=findViewById(R.id.btn_Date)
        date_stock_aff=findViewById(R.id.txtDate)

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
            date=calendar.toString()
        }

        confirmerButton.setOnClickListener {
            val nom = nom_entrepot.text.toString()
            val type = type_entrepot.text.toString()
             date_stock_aff.text = date_stockage.text.toString()
            date=date_stock_aff.toString()
            val tempMax = temperature_max.text.toString()
            val tempMin = temperature_min.text.toString()
            val humMax = humidite_max.text.toString()
            val humMin = humidite_min.text.toString()
           // val long = longitude.text.toString()
            //val lat = latitude.text.toString()

            if (verifierFormulaire(nom, type, date, tempMax,tempMin,humMax,humMin)) {
                // Préparation du résultat pour retourner à MainActivity
                val resultIntent = Intent(this@Activity2,Activity3::class.java)
                resultIntent.putExtra("id", id)
                resultIntent.putExtra("nom",nom)
                resultIntent.putExtra("date_stockage",date)
                resultIntent.putExtra("type", type)
                resultIntent.putExtra("temperature_max", tempMax)
                resultIntent.putExtra("temperature_min", tempMin)
                resultIntent.putExtra("humidite_max", humMax)
                resultIntent.putExtra("humidite_min", humMin)
               // resultIntent.putExtra("temperature_act")
                //resultIntent.putExtra("humidite_act", auteur)
                //resultIntent.putExtra("adresse", adresse)
                startActivity(resultIntent)
            }
        }

    }
    private fun verifierFormulaire(nom: String,type: String,date_stock_aff: String,tempMax: String,tempMin: String,humMax: String,humMin: String): Boolean {
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
        if (date_stock_aff.isEmpty()) {
            ShowInfo(this@Activity2,"la date de stockage doit etre remplis")
            return false
        }

        // Vérification de la temperature max
        if (tempMax.isEmpty()) {
            ShowInfo(this@Activity2,"la temperature Maximal doit etre remplie ")
            return false
        }
        // Vérification de la temperature minimale
        if (tempMin.isEmpty()) {
            ShowInfo(this@Activity2,"la temperature Minimal doit etre remplie ")
            return false
        }

        // Vérification de la humidite min
        if (humMin.isEmpty()) {
            ShowInfo(this@Activity2,"l'humidite Minimale doit etre remplie ")
            return false
        }
        // Vérification de la humidite max
        if (humMax.isEmpty()) {
            ShowInfo(this@Activity2,"l'humidite Maximal doit etre remplie ")
            return false
        }

        return true
    }
    private fun updateDateLabel() {
        val format = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(format, Locale.US)
        date_stock_aff.setText(sdf.format(calendar.time))
    }
    fun ShowInfo(context:Context,message:String){
        Toast.makeText(context,message,Toast.LENGTH_LONG).show()
    }
}