package com.example.bepawsomedos

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.bepawsomedos.ui.home.DataAnimalFragment

class DataAnimalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_animal)

        // Verifica si el fragmento ya está en el contenedor
        if (savedInstanceState == null) {
            val fragment = DataAnimalFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }
    }
}