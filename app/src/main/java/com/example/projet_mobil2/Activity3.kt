package com.example.projet_mobil2

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.TextView

class Activity3 : AppCompatActivity() {

    private lateinit var entrepotAdapter: EntrepotAdapter
    private lateinit var entrepotListView: ListView
    private lateinit var ajouterEntrepotButton: Button
    private lateinit var dbHelper: DatabaseHelper
    private val livresList = mutableListOf<Entrepot>()

    // Déclaration des variables pour afficher les valeurs de température et d'humidité
    private lateinit var temperatureValue: TextView
    private lateinit var humidityValue: TextView
    private lateinit var simulateButton: Button
    // Déclaration des variables pour gérer les capteurs
    private lateinit var sensorManager: SensorManager
    private var temperatureSensor: Sensor? = null
    private var humiditySensor: Sensor? = null
    private lateinit var sensorEventListener: SensorEventListener


    //declaration des variables de firebase
    private lateinit var firebaseDatabase: FirebaseDatabase //bd
    private lateinit var firebaseProductsRef: DatabaseReference

    private val ajouterEntrepotLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                if (data != null) {
                    // Récupération des données renvoyées par AjouterLivreActivity
                    var NOM_EMPLACEMENT = data.getStringExtra("nom")
                    var TYPE = data.getStringExtra("type")
                    var DATE_STOCKAGE = data.getStringExtra("date_stockage")
                    var TEMPERATURE_MAX = data.getStringExtra("temperature_max").toString().toInt()
                    var TEMPERATURE_MIN = data.getStringExtra("temperature_min").toString().toInt()
                    var HUMIDITE_MAX = data.getStringExtra("humidite_max").toString().toInt()
                    var HUMIDITE_MINIM = data.getStringExtra("humidite_min").toString().toInt()
                    var Adresse=data.getStringExtra("Adresse")

                    var entrepot = Entrepot(
                        NOM_EMPLACEMENT!!,
                        TYPE!!,
                        DATE_STOCKAGE!!,
                        TEMPERATURE_MAX,
                        TEMPERATURE_MIN,
                        HUMIDITE_MAX,
                        HUMIDITE_MINIM,
                        Adresse!!
                    )

                    if (NOM_EMPLACEMENT.isNotEmpty()) {
                        ajouterEntrepot(entrepot)
                    } else {
                        mettreAJourEntrepot(entrepot)
                    }
                    // Notification à l'adapter que les données ont changé
                    runOnUiThread {
                        entrepotAdapter.notifyDataSetChanged()
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_3)

        dbHelper = DatabaseHelper(this)


        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseProductsRef = firebaseDatabase.getReference("Project-Mobil") //name??


        // Initialisation des TextView et du bouton
        temperatureValue = findViewById(R.id.temp_capteur)
        humidityValue = findViewById(R.id.hum_capteur)

        // Initialisation du gestionnaire de capteurs
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        // Obtention des capteurs de température et d'humidité
        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
        humiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY)

        // Initialisation des vues
        entrepotListView = findViewById(R.id.list_item)
        ajouterEntrepotButton = findViewById(R.id.btnAjouter)
        dbHelper = DatabaseHelper(this)


        val nom: String = intent.getStringExtra("nom").toString()
        val type: String = intent.getStringExtra("type").toString()
        val date_s: String = intent.getStringExtra("date_stockage").toString()
        val temp_max = intent.getStringExtra("temperature_max").toString().toInt()
        val temp_min = intent.getStringExtra("temperature_min").toString().toInt()
        val hum_max = intent.getStringExtra("humidite_max").toString().toInt()
        val hum_min = intent.getStringExtra("humidite_min").toString().toInt()
        val Adresse=intent.getStringExtra("Adresse").toString()

        val item = Entrepot(nom, type, date_s, temp_max, temp_min, hum_max, hum_min,Adresse)

        livresList.add(item)
        ajouterEntrepot(item)

        // Définition du listener pour les événements des capteurs
        sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                // Vérification du type de capteur et mise à jour des valeurs affichées
                when (event.sensor.type) {
                    Sensor.TYPE_AMBIENT_TEMPERATURE -> {
                        val temperature = event.values[0]
                        temperatureValue.text = "Température: ${temperature}°C"
                        checkTemperature(temperature,item.TEMPERATURE_MAX,item.TEMPERATURE_MIN)
                    }
                    Sensor.TYPE_RELATIVE_HUMIDITY -> {
                        val humidity = event.values[0]
                        humidityValue.text = "Humidité: ${humidity}%"
                        checkHumidity(humidity,item.HUMIDITE_MAX,item.HUMIDITE_MIN)
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                // Gérer les changements de précision, si nécessaire
            }
        }

        // Enregistrement du listener pour les capteurs de température et d'humidité
        temperatureSensor?.let {
            sensorManager.registerListener(sensorEventListener, it, SensorManager.SENSOR_DELAY_NORMAL)
        } ?: run {
            Toast.makeText(this, "Capteur de température non disponible", Toast.LENGTH_SHORT).show()
        }

        humiditySensor?.let {
            sensorManager.registerListener(sensorEventListener, it, SensorManager.SENSOR_DELAY_NORMAL)
        } ?: run {
            Toast.makeText(this, "Capteur d'humidité non disponible", Toast.LENGTH_SHORT).show()
        }

        // Configuration de l'adapter pour la ListView
        entrepotAdapter = EntrepotAdapter(this@Activity3, livresList)
        entrepotListView.adapter = entrepotAdapter


        // Chargement des livres depuis la base de données local
        chargerEntrepotDepuisLocal()
        // Gestion du bouton pour ajouter un nouveau livre
        ajouterEntrepotButton.setOnClickListener {
            val intent = Intent(this, Activity2::class.java)
            ajouterEntrepotLauncher.launch(intent)
        }

        // Gestion du clic sur un item de la liste pour éditer le livre
        entrepotListView.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val e = livresList[position]
                val intent = Intent(this, Activity2::class.java)
                intent.putExtra("type", e.TYPE)
                intent.putExtra("nom", e.NOM_EMPLACEMENT)
                intent.putExtra("date_s", e.DATE_STOCKAGE)
                intent.putExtra("temperature_max", e.TEMPERATURE_MAX)
                intent.putExtra("temperature_min", e.TEMPERATURE_MIN)
                intent.putExtra("humidite_max", e.HUMIDITE_MAX)
                intent.putExtra("humidite_min", e.HUMIDITE_MIN)
                intent.putExtra("Adresse",e.ADRESSE)
                ajouterEntrepotLauncher.launch(intent)
            }

        // Gestion du long clic sur un item de la liste pour supprimer le livre
        entrepotListView.onItemLongClickListener =
            AdapterView.OnItemLongClickListener { _, _, position, _ ->
                val e = livresList[position]
                AlertDialog.Builder(this)
                    .setTitle("Confirmation")
                    .setMessage("Voulez-vous supprimer ce livre?")
                    .setPositiveButton("Oui") { dialog, _ ->
                        supprimerEntrepot(e, position)
                        dialog.dismiss()
                    }
                    .setNegativeButton("Non", null)
                    .show()
                true
            }
    }

    private fun ajouterEntrepot(entrepot: Entrepot) {

        // Ajouter le livre à la base de données locale
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_NOM_EMPLACEMENT, entrepot.NOM_EMPLACEMENT)
            put(DatabaseHelper.COLUMN_TYPE, entrepot.TYPE)
            put(DatabaseHelper.COLUMN_DATE_STOCKAGE, entrepot.DATE_STOCKAGE)
            put(DatabaseHelper.COLUMN_HUMIDITE_MAX, entrepot.HUMIDITE_MAX)
            put(DatabaseHelper.COLUMN_TEMPERATURE_MAX, entrepot.TEMPERATURE_MAX)
            put(DatabaseHelper.COLUMN_HUMIDITE_MIN, entrepot.HUMIDITE_MIN)
            put(DatabaseHelper.COLUMN_TEMPERATURE_MIN, entrepot.TEMPERATURE_MIN)
            put(DatabaseHelper.COLUMN_ADRESSE,entrepot.ADRESSE)

        }
        db.insert(DatabaseHelper.TABLE_ENTREPOT, null, values)


//ajouter dans firebase
        firebaseProductsRef.child(entrepot.NOM_EMPLACEMENT).setValue(entrepot)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Ajouter au Firebase avec succes", Toast.LENGTH_LONG)
                        .show()
// Notification à l'adapter que les données ont changé
                    runOnUiThread {
                        entrepotAdapter.notifyDataSetChanged()
                    }
                } else {
                    Log.e(
                        "FirebaseError",
                        "Erreur en ajoutant un produit dans Firebase",
                        task.exception
                    )
                }
            }
    }

    // Méthode pour mettre à jour un livre
    private fun mettreAJourEntrepot(entrepot: Entrepot) {

        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_NOM_EMPLACEMENT, entrepot.NOM_EMPLACEMENT)
            put(DatabaseHelper.COLUMN_TYPE,entrepot.TYPE)
            put(DatabaseHelper.COLUMN_HUMIDITE_MAX, entrepot.HUMIDITE_MAX)
            put(DatabaseHelper.COLUMN_TEMPERATURE_MAX, entrepot.TEMPERATURE_MAX)
            put(DatabaseHelper.COLUMN_HUMIDITE_MIN, entrepot.HUMIDITE_MIN)
            put(DatabaseHelper.COLUMN_TEMPERATURE_MIN, entrepot.TEMPERATURE_MIN)
            put(DatabaseHelper.COLUMN_DATE_STOCKAGE, entrepot.DATE_STOCKAGE)
            put(DatabaseHelper.COLUMN_ADRESSE,entrepot.ADRESSE)
        }
        val selection = "${DatabaseHelper.COLUMN_NOM_EMPLACEMENT} = ?"
        val selectionArgs = arrayOf(entrepot.NOM_EMPLACEMENT)
        db.update(DatabaseHelper.TABLE_ENTREPOT, values, selection, selectionArgs)

        firebaseProductsRef.child(entrepot.NOM_EMPLACEMENT).setValue(entrepot)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
// Notification à l'adapter que les données ont changé
                    runOnUiThread {
                        entrepotAdapter.notifyDataSetChanged()
                    }
                } else {
                    Log.e(
                        "FirebaseError",
                        "Erreur en ajoutant un produit dans Firebase",
                        task.exception
                    )
                }
            }

    }
// Méthode pour supprimer un livre
    private fun supprimerEntrepot(Entrepot: Entrepot, position: Int) {
            // Suppression du livre de la base de données locale
            val db = dbHelper.writableDatabase
            val selection = "${DatabaseHelper.COLUMN_NOM_EMPLACEMENT} = ?"
            val selectionArgs = arrayOf(Entrepot.NOM_EMPLACEMENT)
            db.delete(DatabaseHelper.TABLE_ENTREPOT, selection, selectionArgs)
            // Suppression du livre de la liste locale
            livresList.removeAt(position)

            firebaseProductsRef.child(Entrepot.NOM_EMPLACEMENT).removeValue()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        //reaffiche les produits restants
                        chargerEntrepotDepuisLocal()
                        // Notification à l'adapter que les données ont changé
                        runOnUiThread {
                            entrepotAdapter.notifyDataSetChanged()
                        }
                    } else {
                        Log.e("FirebaseError", "Erreur en supprimant un produit dans Firebase", task.exception)
                    }
                }
            // Notification à l'adapter que les données ont changé
            runOnUiThread {
                entrepotAdapter.notifyDataSetChanged()
            }
    }

    // Méthode pour charger les livres depuis la base de données locale
    private fun chargerEntrepotDepuisLocal() {
        val db = dbHelper.readableDatabase
        val cursor = db.query(DatabaseHelper.TABLE_ENTREPOT, null, null, null, null, null, null)

        if (cursor.moveToFirst()) {
            do {
                var nom_entrepot = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOM_EMPLACEMENT))
                var type_entrepot = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TYPE))
                var date_stock = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE_STOCKAGE))
                var temp_max = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TEMPERATURE_MAX))
                var temp_min = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TEMPERATURE_MIN))
                var humidite_max = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HUMIDITE_MAX))
                var humidite_min = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HUMIDITE_MIN))
                var Adresse=cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ADRESSE))


                var entrepot = Entrepot(nom_entrepot, type_entrepot, date_stock,temp_max,temp_min,humidite_max,humidite_min,Adresse!!)
                livresList.add(entrepot)

                firebaseProductsRef.child(nom_entrepot.toString()).setValue(entrepot) //already a string ??
            } while (cursor.moveToNext())
        }
        cursor.close()
        runOnUiThread {
            entrepotAdapter.notifyDataSetChanged()
        }
    }

    // Méthode pour vérifier si la température dépasse les seuils définis
    private fun checkTemperature(temperature: Float,temp_max:Int,temp_min:Int) {
        when {
            temperature > temp_max -> {
                // Affichage d'un Toast si la température est trop élevée
                Toast.makeText(this, "Température trop élevée: $temperature°C", Toast.LENGTH_SHORT).show()
            }
            temperature < temp_min -> {
                // Affichage d'un Toast si la température est trop basse
                Toast.makeText(this, "Température trop basse: $temperature°C", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Méthode pour vérifier si l'humidité dépasse les seuils définis
    private fun checkHumidity(humidity: Float,hum_max:Int,hum_min: Int) {
        when {
            humidity > hum_max -> {
                // Affichage d'un Toast si l'humidité est trop élevée
                Toast.makeText(this, "Humidité trop élevée: $humidity%", Toast.LENGTH_SHORT).show()
            }
            humidity < hum_min -> {
                // Affichage d'un Toast si l'humidité est trop basse
                Toast.makeText(this, "Humidité trop basse: $humidity%", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Méthode appelée lorsque l'activité est en pause
    override fun onPause() {
        super.onPause()
        // Désenregistrement du listener pour économiser les ressources
        sensorManager.unregisterListener(sensorEventListener)
    }

    // Méthode appelée lorsque l'activité est reprise
    override fun onResume() {
        super.onResume()
        // Réenregistrement du listener pour recommencer à recevoir les mises à jour des capteurs
        temperatureSensor?.let {
            sensorManager.registerListener(sensorEventListener, it, SensorManager.SENSOR_DELAY_NORMAL)
        }

        humiditySensor?.let {
            sensorManager.registerListener(sensorEventListener, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

}