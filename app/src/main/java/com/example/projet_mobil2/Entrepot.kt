package com.example.projet_mobil2


import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Entrepot(
    var id:Long=0,
    var NOM_EMPLACEMENT:String,
    var TYPE : String,
    var DATE_STOCKAGE :String,
    var TEMPERATURE_MAX :Int,
    var TEMPERATURE_MIN :Int,
    var HUMIDITE_MAX :Int,
    var HUMIDITE_MIN :Int,
    var TEMPERATURE_ACT :Int,
    var HUMIDITE_ACT :Int,
    var LONGITUDE :String,
    var LATITUDE :String,
    var ADRESSE :String,
) : Parcelable


