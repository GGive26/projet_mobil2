package com.example.projet_mobil2

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EntrepotAdapter (private val context: Context, private val EntrepotList: List<Entrepot>): BaseAdapter() {
    override fun getCount(): Int {
        return EntrepotList.size
    }

    override fun getItem(position: Int): Any {
       return EntrepotList[position]
    }

    override fun getItemId(position: Int): Long {
       return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_entrepot, parent, false)
            holder = ViewHolder()
            holder.nom_entrepot = view.findViewById(R.id.txt_aff_nom)
            holder.type_entrepot = view.findViewById(R.id.txt_aff_type)
            holder.temp_actuel = view.findViewById(R.id.txt_aff_temp_act)
            holder.humidite_actuel=view.findViewById(R.id.txt_aff_hum_act)
            holder.adresse=view.findViewById(R.id.txt_aff_adress)

            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val entrepot = EntrepotList[position]
        holder.nom_entrepot.text = entrepot.NOM_EMPLACEMENT
        holder.type_entrepot.text = entrepot.TYPE
        holder.temp_actuel.text = entrepot.TEMPERATURE_ACT.toString()
        holder.humidite_actuel.text=entrepot.HUMIDITE_ACT.toString()
        holder.adresse.text=entrepot.ADRESSE
        return view
    }


    private class ViewHolder {
         lateinit var nom_entrepot: TextView
         lateinit var type_entrepot: TextView
         lateinit var temp_actuel: TextView
         lateinit var humidite_actuel: TextView
         lateinit var adresse: TextView
    }

}