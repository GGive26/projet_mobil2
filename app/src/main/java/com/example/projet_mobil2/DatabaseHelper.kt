package com.example.projet_mobil2

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context:Context):SQLiteOpenHelper(context,DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_ENTREPOT = ("CREATE TABLE $TABLE_ENTREPOT ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COLUMN_NOM_EMPLACEMENT TEXT,"
                + "$COLUMN_TYPE TEXT,"
                + "$COLUMN_DATE_STOCKAGE TEXT,"
                + "$COLUMN_TEMPERATURE_MAX INT"
                + "$COLUMN_TEMPERATURE_MIN INT"
                + "$COLUMN_HUMIDITE_MAX INT"
                + "$COLUMN_HUMIDITE_MIN INT)"
                )
        db.execSQL(CREATE_ENTREPOT)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ENTREPOT")
        onCreate(db)
    }

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "entrepot.db"
        const val TABLE_ENTREPOT = "entrepot"
        const val COLUMN_ID = "id"
        const val COLUMN_NOM_EMPLACEMENT ="nom_emplacement"
        const val COLUMN_TYPE ="type"
        const val COLUMN_DATE_STOCKAGE ="date_stockage"
        const val COLUMN_TEMPERATURE_MAX ="temperature_max"
        const val COLUMN_TEMPERATURE_MIN ="temperature_min"
        const val COLUMN_HUMIDITE_MAX ="humidite_max"
        const val COLUMN_HUMIDITE_MIN ="humidite_min"
    }
}
