package com.example.projet_mobil2

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import androidx.activity.result.contract.ActivityResultContracts

class Activity3 : AppCompatActivity() {

    private lateinit var entrepotAdapter: EntrepotAdapter
    private lateinit var entrepotListView: ListView
    private lateinit var ajouterEntrepotButton: Button
    private lateinit var dbHelper: DatabaseHelper
    private val livresList = mutableListOf<Entrepot>()

    private val ajouterEntrepotLauncher= registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            if (data != null) {
                // Récupération des données renvoyées par AjouterLivreActivity
                var NOM_EMPLACEMENT = data.getStringExtra("nom")
                var TYPE = data.getStringExtra("type")
                var DATE_STOCKAGE=data.getStringExtra("date_stockage")
                var TEMPERATURE_MAX = data.getStringExtra("temperature_max").toString().toInt()
                var TEMPERATURE_MIN = data.getStringExtra("temperature_min").toString().toInt()
                var HUMIDITE_MAX = data.getStringExtra("humidite_max").toString().toInt()
                var HUMIDITE_MINIM = data.getStringExtra("humidite_min").toString().toInt()

                var entrepot= Entrepot(NOM_EMPLACEMENT!!,TYPE!!, DATE_STOCKAGE!!,TEMPERATURE_MAX,TEMPERATURE_MIN,HUMIDITE_MAX,HUMIDITE_MINIM)

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


        // Initialisation des vues
        entrepotListView = findViewById(R.id.list_item)
        ajouterEntrepotButton = findViewById(R.id.btnAjouter)
        dbHelper = DatabaseHelper(this)


        val nom:String=intent.getStringExtra("nom").toString()
        val type:String=intent.getStringExtra("type").toString()
        val date_s:String=intent.getStringExtra("date_stockage").toString()
        val temp_max =intent.getStringExtra("temperature_max").toString().toInt()
        val temp_min=intent.getStringExtra("temperature_min").toString().toInt()
        val hum_max=intent.getStringExtra("humidite_max").toString().toInt()
        val hum_min=intent.getStringExtra("humidite_min").toString().toInt()

        val item= Entrepot(nom,type,date_s,temp_max,temp_min,hum_max,hum_min)

        livresList.add(item)
        ajouterEntrepot(item)

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
            intent.putExtra("nom",e.NOM_EMPLACEMENT)
            intent.putExtra("date_s",e.DATE_STOCKAGE)
            intent.putExtra("temperature_max", e.TEMPERATURE_MAX)
            intent.putExtra("temperature_min", e.TEMPERATURE_MIN)
            intent.putExtra("humidite_max", e.HUMIDITE_MAX)
            intent.putExtra("humidite_min", e.HUMIDITE_MIN)
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

            }
            db.insert(DatabaseHelper.TABLE_ENTREPOT, null, values)
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
        }
        val selection = "${DatabaseHelper.COLUMN_NOM_EMPLACEMENT} = ?"
        val selectionArgs = arrayOf(entrepot.NOM_EMPLACEMENT)
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
                var nom_entrepot = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOM_EMPLACEMENT))
                var type_entrepot = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TYPE))
                var date_stock = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE_STOCKAGE))
                var temp_max = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TEMPERATURE_MAX))
                var temp_min = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TEMPERATURE_MIN))
                var humidite_max = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HUMIDITE_MAX))
                var humidite_min = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HUMIDITE_MIN))


                var entrepot = Entrepot(nom_entrepot, type_entrepot, date_stock,temp_max,temp_min,humidite_max,humidite_min)
                livresList.add(entrepot)
            } while (cursor.moveToNext())
        }
        cursor.close()
        runOnUiThread {
            entrepotAdapter.notifyDataSetChanged()
        }
    }

}