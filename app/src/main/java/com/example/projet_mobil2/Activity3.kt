package com.example.projet_mobil2

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts

class Activity3 : AppCompatActivity() {

    private lateinit var entrepotAdapter: EntrepotAdapter
    private lateinit var entrepotListView: ListView
    private lateinit var ajouterEntrepotButton: Button
    private lateinit var dbHelper: DatabaseHelper
    private val livresList = mutableListOf<Entrepot>()

    private val ajouterEntrepotLauncher= registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            try{
            val data = result.data

            if (data != null) {
                // Récupération des données renvoyées par AjouterLivreActivity
                val NOM_EMPLACEMENT = data.getStringExtra("nom_emplacement")
                val TYPE = data.getStringExtra("type")
                val DATE_STOCKAGE = data.getStringExtra("date_stockage")
                val TEMPERATURE_MAX = data.getIntExtra("temperature_max", 0)
                val TEMPERATURE_MIN = data.getIntExtra("temperature_min", 0)
                val HUMIDITE_MAX = data.getIntExtra("humidite_max", 0)
                val HUMIDITE_MINIM = data.getIntExtra("humidite_min", 0)
                // val temp_act = data.getIntExtra("temperature_act",0)
                //val hum_act = data.getIntExtra("humidite_act",0)
                // val adresse_entrepot = data.getStringExtra("adresse")
                val entrepot = Entrepot(
                    NOM_EMPLACEMENT!!,
                    TYPE!!,
                    DATE_STOCKAGE!!,
                    TEMPERATURE_MAX,
                    TEMPERATURE_MIN,
                    HUMIDITE_MAX,
                    HUMIDITE_MINIM
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
            }finally{
                Toast.makeText(this,"Page d'acceuil",Toast.LENGTH_SHORT)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_3)

        dbHelper = DatabaseHelper(this)


        // Initialisation des vues
        entrepotListView = findViewById(R.id.list_item)
        ajouterEntrepotButton = findViewById(R.id.btnAjouter)
        dbHelper = DatabaseHelper(this)

    try {

        val nom: String = intent.getStringExtra("nom").toString()
        val type: String = intent.getStringExtra("type").toString()
        val date_s: String = intent.getStringExtra("date_stockage").toString()
        val temp_max = intent.getStringExtra("temperature_max").toString().toInt()
        val temp_min = intent.getStringExtra("temperature_min").toString().toInt()
        val hum_max = intent.getStringExtra("humidite_max",).toString().toInt()
        val hum_min = intent.getStringExtra("humidite_min").toString().toInt()

        val item = Entrepot(nom, type, date_s, temp_max, temp_min, hum_max, hum_min)
        ajouterEntrepot(item)
        livresList.add(item)
    }finally{
        Toast.makeText(this,"Page d'acceuil",Toast.LENGTH_SHORT)
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
        entrepotListView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val e = livresList[position]
            val intent = Intent(this, Activity2::class.java)
            intent.putExtra("type", e.TYPE)
            intent.putExtra("temperature_max", e.TEMPERATURE_MAX)
            intent.putExtra("temperature_min", e.TEMPERATURE_MIN)
            intent.putExtra("humidite_max", e.HUMIDITE_MAX)
            intent.putExtra("humidite_min", e.HUMIDITE_MIN)
            // intent.putExtra("temperature_act",e.TEMP_ACT)
            //intent.putExtra("humidite_act", e.HUMIDITE_ACT)
            //intent.putExtra("adresse", e.DATE_STOCK)
            ajouterEntrepotLauncher.launch(intent)
        }

        // Gestion du long clic sur un item de la liste pour supprimer le livre
        entrepotListView.onItemLongClickListener = AdapterView.OnItemLongClickListener { _, _, position, _ ->
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
                put(DatabaseHelper.COLUMN_TYPE,entrepot.TYPE)
                put(DatabaseHelper.COLUMN_DATE_STOCKAGE, entrepot.DATE_STOCKAGE)
                put(DatabaseHelper.COLUMN_HUMIDITE_MAX, entrepot.HUMIDITE_MAX)
                put(DatabaseHelper.COLUMN_TEMPERATURE_MAX, entrepot.TEMPERATURE_MAX)
                put(DatabaseHelper.COLUMN_HUMIDITE_MIN, entrepot.HUMIDITE_MIN)
                put(DatabaseHelper.COLUMN_TEMPERATURE_MIN, entrepot.TEMPERATURE_MIN)
                //put(DatabaseHelper.COLUMN_ADRESSE, 0)
                //put(DatabaseHelper.COLUMN_TEMPERATURE_ACT, 0)
                //put(DatabaseHelper.COLUMN_HUMIDITE_ACT, 0)
            }
            db.insert(DatabaseHelper.TABLE_ENTREPOT, null, values)
        }

    // Méthode pour mettre à jour un livre
    private fun mettreAJourEntrepot(Entrepot: Entrepot) {

        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_NOM_EMPLACEMENT, Entrepot.NOM_EMPLACEMENT)
            put(DatabaseHelper.COLUMN_TYPE,Entrepot.TYPE)
            put(DatabaseHelper.COLUMN_HUMIDITE_MAX, Entrepot.HUMIDITE_MAX)
            put(DatabaseHelper.COLUMN_TEMPERATURE_MAX, Entrepot.TEMPERATURE_MAX)
            put(DatabaseHelper.COLUMN_HUMIDITE_MIN, Entrepot.HUMIDITE_MIN)
            put(DatabaseHelper.COLUMN_TEMPERATURE_MIN, Entrepot.TEMPERATURE_MIN)
            //put(DatabaseHelper.COLUMN_ADRESSE, 0)
            put(DatabaseHelper.COLUMN_DATE_STOCKAGE, Entrepot.DATE_STOCKAGE)
            //put(DatabaseHelper.COLUMN_TEMPERATURE_ACT, 0)
            //put(DatabaseHelper.COLUMN_HUMIDITE_ACT, 0)
        }
        val selection = "${DatabaseHelper.COLUMN_NOM_EMPLACEMENT} = ?"
        val selectionArgs = arrayOf(Entrepot.NOM_EMPLACEMENT)
        db.update(DatabaseHelper.TABLE_ENTREPOT, values, selection, selectionArgs)

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
                val nom_entrepot = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOM_EMPLACEMENT))
                val type_entrepot = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TYPE))
                val date_stock = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE_STOCKAGE))
                val temp_max = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TEMPERATURE_MAX))
                val temp_min = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TEMPERATURE_MIN))
                val humidite_max = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HUMIDITE_MAX))
                val humidite_min = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HUMIDITE_MIN))


                val entrepot = Entrepot(nom_entrepot, type_entrepot, date_stock,temp_max,temp_min,humidite_max,humidite_min)
                livresList.add(entrepot)
            } while (cursor.moveToNext())
        }
        cursor.close()
        runOnUiThread {
            entrepotAdapter.notifyDataSetChanged()
        }
    }

}