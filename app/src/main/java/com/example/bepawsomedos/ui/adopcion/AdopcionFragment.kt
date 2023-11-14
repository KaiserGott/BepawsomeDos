package com.example.bepawsomedos.ui.adopcion

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bepawsomedos.R
import com.example.bepawsomedos.adapters.AdaptadorAnimal
import com.example.bepawsomedos.models.Animal
import com.example.bepawsomedos.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class AdopcionFragment : Fragment() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var rvLista: RecyclerView
    private lateinit var adaptador: AdaptadorAnimal
    private lateinit var textAdopcion: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_adopcion, container, false)

        databaseReference = FirebaseDatabase.getInstance().reference
        firebaseAuth = FirebaseAuth.getInstance()

        val currentUser = firebaseAuth.currentUser

        textAdopcion = view.findViewById(R.id.text_adopcion)
        rvLista = view.findViewById(R.id.rvLista)
        val layoutManager = LinearLayoutManager(requireContext())
        rvLista.layoutManager = layoutManager

        adaptador = AdaptadorAnimal(ArrayList())
        rvLista.adapter = adaptador

        if (currentUser != null) {
            val userId = currentUser.uid
            obtenerAdopcionesIds(userId)
        }

        return view
    }

    private fun obtenerAdopcionesIds(userId: String) {
        databaseReference.child("usuarios").child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    if (user != null) {
                        val adopcionesIds = user.listaadopciones.filterNotNull().filter { it.isNotEmpty() }
                        obtenerDetallesAnimales(adopcionesIds)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("AdopcionFragment", "Error al obtener adopciones del usuario: ${error.message}")
                }
            })
    }

    private fun actualizarImagenAnimal(urlImagen: String) {
        // Obtener la referencia al ImageView
        val imageView = view?.findViewById<ImageView>(R.id.imagenUrl)

        // Cargar la imagen utilizando Glide
        Glide.with(this)
            .load(urlImagen)
            .apply(RequestOptions().centerCrop())
            .into(imageView!!)
    }

    private fun obtenerDetallesAnimales(animalIds: List<String>) {
        val listaAnimales = ArrayList<Animal>()

        // Usar un contador para verificar cuándo se han cargado todos los animales
        var animalesCargados = 0

        for (animalId in animalIds) {
            databaseReference.child("animales").child(animalId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val animal = snapshot.getValue(Animal::class.java)
                        if (animal != null) {
                            listaAnimales.add(animal)

                            // Actualizar la imagen del animal en el ImageView
                            actualizarImagenAnimal(animal.imagenUrl) // Asegúrate de tener la propiedad imageUrl en tu modelo Animal
                        }


                        // Incrementar el contador y verificar si todos los animales han sido cargados
                        animalesCargados++
                        if (animalesCargados == animalIds.size) {
                            adaptador.filtrar(listaAnimales)
                            textAdopcion.text = "Número de animales en adopción: ${listaAnimales.size}"
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("AdopcionFragment", "Error al obtener detalles del animal: ${error.message}")

                        // Incrementar el contador en caso de error para garantizar que se llame a filtrar
                        animalesCargados++
                        if (animalesCargados == animalIds.size) {
                            adaptador.filtrar(listaAnimales)
                            textAdopcion.text = "Número de animales en adopción: ${listaAnimales.size}"
                        }
                    }
                })
        }
    }
}
