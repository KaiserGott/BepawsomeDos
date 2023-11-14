package com.example.bepawsomedos

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bepawsomedos.adapters.AdaptadorAnimal
import com.example.bepawsomedos.databinding.ActivityMainBinding
import com.example.bepawsomedos.models.Animal
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.bumptech.glide.Glide
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.example.bepawsomedos.models.User

class MainActivity : AppCompatActivity() {


    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_BepawsomeDos_NoActionBar)

        // Ahora obtén la referencia de la base de datos después de habilitar la persistencia
        databaseReference = FirebaseDatabase.getInstance().reference
        firebaseAuth = FirebaseAuth.getInstance()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)


        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top-level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_publicacion, R.id.nav_configuracion
            ), drawerLayout
        )

        // Configuración de la barra de acciones con el controlador de navegación y la configuración de AppBar
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Configuración de NavigationView con el controlador de navegación
        navView.setupWithNavController(navController)

        // Obtener el nombre de usuario y su imagen
        val headerView = navView.getHeaderView(0)
        val textViewUserName = headerView.findViewById<TextView>(R.id.textViewNombreUsuarioHeader)
        val imageViewUser = headerView.findViewById<ImageView>(R.id.imageViewUserProfileHeader)

        // Obtén el usuario actual
        val currentUser = firebaseAuth.currentUser
        val userId = currentUser?.uid

        if (currentUser != null) {
            databaseReference.child("usuarios").child(userId!!).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    if (user != null) {
                        val name = user.name ?: ""
                        val imageUrl = user.imageUrl ?: ""

                        Log.d("MainActivity", "Datos del usuario obtenidos correctamente.")
                        Log.d("MainActivity", "Nombre de usuario: $name")
                        Log.d("MainActivity", "URL de la imagen: $imageUrl")

                        textViewUserName.text = name

                        if (imageUrl.isNotEmpty()) {
                            Glide.with(applicationContext).load(imageUrl).into(imageViewUser)
                        }
                    } else {
                        Log.e("MainActivity", "Error: No se pudo obtener el objeto User.")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MainActivity", "Error al obtener datos del usuario: ${error.message}")
                }
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
