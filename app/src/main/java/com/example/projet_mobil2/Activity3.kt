package com.example.projet_mobil2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts

class Activity3 : AppCompatActivity() {


    private val ajouterEntrepot= registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            if (data != null) {
                // Récupération des données renvoyées par AjouterLivreActivity
                val id = data.getIntExtra("id", -1)
                val Nom_entrepot = data.getStringExtra("nom_emplacement")
                val type_entrepot = data.getStringExtra("type")
                val temp_maximal = data.getIntExtra("temperature_max",0)
                val temp_minimal = data.getIntExtra("temperature_min",0)
                val hum_maximal = data.getIntExtra("humidite_max",0)
                val hum_minimal = data.getIntExtra("humidite_min",0)
                val temp_act = data.getIntExtra("temperature_act",0)
                val hum_act = data.getIntExtra("humidite_act",0)
                val adresse_entrepot = data.getStringExtra("adresse")

                val entrepot = Entrepot(id, Nom_entrepot!!, type_entrepot!!, temp_maximal!!, temp_minimal!!,hum_maximal!!,hum_minimal!!,temp_act!!,hum_act!!,adresse_entrepot!!)

                if (id == -1) {
                    ajouterLivre(livre)
                } else {
                    mettreAJourLivre(livre)
                }
                // Notification à l'adapter que les données ont changé
                runOnUiThread {
                    livresAdapter.notifyDataSetChanged()
                }
            }
        }
    }

}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_3)
    }
}