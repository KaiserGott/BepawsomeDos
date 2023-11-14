package com.example.bepawsomedos.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bepawsomedos.api.DogApiResponse
import com.example.bepawsomedos.api.DogApiService
import com.example.bepawsomedos.models.Animal
import com.google.firebase.database.*

import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AnimalViewModel : ViewModel() {
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://dog.ceo/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService: DogApiService = retrofit.create(DogApiService::class.java)
    private val database = FirebaseDatabase.getInstance().reference.child("animales")

    // Lista de animales desde Firebase
    private val _animalesLiveData = MutableLiveData<List<Animal>>()
    val animalesLiveData: LiveData<List<Animal>>
        get() = _animalesLiveData

    fun leerAnimalesDesdeFirebase() {
        val animalesList = mutableListOf<Animal>()
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (animalSnapshot in snapshot.children) {
                    val animal = animalSnapshot.getValue(Animal::class.java)
                    animal?.let {
                        animalesList.add(it)
                    }
                }
                _animalesLiveData.postValue(animalesList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar el error
            }
        })
    }

    fun leerAnimalesDesdeFirebase(listener: ValueEventListener) {
        database.addListenerForSingleValueEvent(listener)
    }

    fun buscarPorRaza(raza: String) {
        val animalesList = mutableListOf<Animal>()
        database.orderByChild("raza").equalTo(raza).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (animalSnapshot in snapshot.children) {
                    val animal = animalSnapshot.getValue(Animal::class.java)
                    animal?.let {
                        animalesList.add(it)
                    }
                }
                _animalesLiveData.postValue(animalesList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar el error
            }
        })
    }

    // Método para reiniciar la búsqueda y obtener todos los animales
    fun reiniciarBusqueda() {
        leerAnimalesDesdeFirebase()
    }
    fun reiniciarBusquedea23() {
        leerAnimalesDesdeFirebase()
    }

    fun getDogImage(breed: String, callback: Callback<DogApiResponse>) {
        apiService.getDogImage(breed).enqueue(callback)
    }
}
