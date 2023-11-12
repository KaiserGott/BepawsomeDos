package com.example.bepawsomedos.viewModels

import androidx.lifecycle.ViewModel
import com.example.bepawsomedos.api.DogApiService
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class AnimalViewModel : ViewModel() {
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://dog.ceo/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService: DogApiService = retrofit.create(DogApiService::class.java)
    private val database = FirebaseDatabase.getInstance().reference.child("animales")

    fun leerAnimalesDesdeFirebase(listener: ValueEventListener) {
        database.addListenerForSingleValueEvent(listener)
    }
}