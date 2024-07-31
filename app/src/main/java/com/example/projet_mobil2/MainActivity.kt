package com.example.projet_mobil2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button


class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this)
        val Btn_demarrer=findViewById<Button>(R.id.btnDemarrer);

        val switch=Intent(this,Activity2::class.java);

        Btn_demarrer.setOnClickListener { startActivity(switch) }
    }
}