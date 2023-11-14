package com.example.bepawsomedos.ui.favorite

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

class FavoriteFragment : Fragment() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var rvLista: RecyclerView
    private lateinit var adaptador: AdaptadorAnimal

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorite, container, false)

        databaseReference = FirebaseDatabase.getInstance().reference
        firebaseAuth = FirebaseAuth.getInstance()

        val currentUser = firebaseAuth.currentUser

        if (currentUser != null) {
            val userId = currentUser.uid
            rvLista = view.findViewById(R.id.rvLista)
            val layoutManager = LinearLayoutManager(requireContext())
            rvLista.layoutManager = layoutManager

            adaptador = AdaptadorAnimal(ArrayList())
            rvLista.adapter = adaptador

            obtenerFavoritosIds(userId)
        }

        return view
    }

    private fun obtenerFavoritosIds(userId: String) {
        databaseReference.child("usuarios").child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    if (user != null) {
                        obtenerDetallesAnimales(user.listafavoritos)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FavoriteFragment", "Error al obtener favoritos del usuario: ${error.message}")
                }
            })
    }

    private fun obtenerDetallesAnimales(animalIds: List<String>) {
        val listaAnimales = ArrayList<Animal>()

        for (animalId in animalIds) {
            databaseReference.child("animales").child(animalId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val animal = snapshot.getValue(Animal::class.java)
                        if (animal != null) {
                            listaAnimales.add(animal)
                            adaptador.filtrar(listaAnimales)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("FavoriteFragment", "Error al obtener detalles del animal: ${error.message}")
                    }
                })
        }
    }
}
