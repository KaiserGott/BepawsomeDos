package com.example.bepawsomedos.ui.home

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import android.Manifest
import com.example.bepawsomedos.R
import com.example.bepawsomedos.api.DogApiResponse
import com.example.bepawsomedos.api.ImageAdapter
import com.example.bepawsomedos.api.RetrofitClient
import com.example.bepawsomedos.models.Animal
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DataAnimalActivity : Fragment() {
    private lateinit var nameTextView: TextView
    private lateinit var ageTextView: TextView
    private lateinit var sexTextView: TextView
    private lateinit var razaTextView: TextView
    private lateinit var subRazaTextView: TextView
    private lateinit var imageRecyclerView: RecyclerView
    private lateinit var imageView: ImageView
    private lateinit var imageAdapter: ImageAdapter
    private val telefonoAnimal = "123456789"
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_data_animal, container, false)

        // Inicializar las vistas
        nameTextView = rootView.findViewById(R.id.nameTextView)
        ageTextView = rootView.findViewById(R.id.ageTextView)
        sexTextView = rootView.findViewById(R.id.sexTextView)
        razaTextView = rootView.findViewById(R.id.razaTextView)
        subRazaTextView = rootView.findViewById(R.id.subRazaTextView)
        imageRecyclerView = rootView.findViewById(R.id.imageRecyclerView)
        imageView = rootView.findViewById(R.id.ownerImageView)

        // Recuperar el ID del animal de la intent
        val animalId = requireActivity().intent.getStringExtra("animalId")

        // Verificar que animalId no sea nulo antes de usarlo en Firebase Database
        if (animalId != null) {
            // Inicializar la referencia a la base de datos
            databaseReference =
                FirebaseDatabase.getInstance().reference.child("animales").child(animalId)

            // Obtener y mostrar los datos del animal desde Firebase
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val animal = snapshot.getValue(Animal::class.java)
                    if (animal != null) {
                        val animalImageUrl = animal.imagenUrl
                        if (!animalImageUrl.isNullOrBlank()) {
                            // Cambia esta línea para cargar la imagen en un ImageView
                            Glide.with(this@DataAnimalActivity).load(animalImageUrl)
                                .into(imageView)
                        } else {
                            // Maneja el caso en el que la URL de la imagen es nula o vacía
                        }

                        // Mostrar la imagen grande antes que el nombre y la edad
                        nameTextView.text = "Nombre: ${animal.nombre}"
                        ageTextView.text = "Edad: ${animal.edad}"
                        razaTextView.text = "Raza: ${animal.raza}"
                        sexTextView.text = "Sexo: ${animal.sexo}"

                        // Obtener el nombre de la raza del animal y llamar a la API
                        val breedName = animal.raza
                        callDogApi(breedName)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    println("Error al leer datos desde Firebase: ${error.message}")
                }
            })
        } else {
            // Manejar el caso en el que animalId es nulo, por ejemplo, mostrar un mensaje de error o volver atrás
        }

        // Obtener el nombre de la raza del animal
        val breedName = requireActivity().intent.getStringExtra("raza")

        // Llamar a la API para obtener imágenes de la raza específica
        val dogApiService = RetrofitClient.create()
        val call = dogApiService.getDogImages(breedName)

        call.enqueue(object : Callback<DogApiResponse> {
            override fun onResponse(call: Call<DogApiResponse>, response: Response<DogApiResponse>) {
                if (response.isSuccessful) {
                    val images = response.body()?.message ?: emptyList()
                    println("Llamada a la API exitosa. Imágenes obtenidas: $images")
                    if (images.isNotEmpty()) {
                        showDogImages(images)
                    } else {
                        println("La lista de imágenes está vacía.")
                    }
                } else {
                    println("Error en la respuesta de la API: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<DogApiResponse>, t: Throwable) {
                println("Error de red al llamar a la API: ${t.message}")
            }
        })

        val callButton = rootView.findViewById<Button>(R.id.callButton)
        callButton.setOnClickListener {
            // Verifica si tienes permiso para realizar llamadas
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CALL_PHONE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Si tienes permiso, inicia la llamada
                val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$telefonoAnimal"))
                startActivity(intent)
            } else {
                // Si no tienes permiso, solicita el permiso al usuario
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.CALL_PHONE),
                    CALL_PERMISSION_REQUEST_CODE
                )
            }
        }

        return rootView
    }

    // En la función showDogImages, actualiza el RecyclerView con las imágenes
    private fun showDogImages(images: List<String>) {
        // Limitar la lista a solo 5 imágenes
        val limitedImages = images.subList(0, minOf(5, images.size))

        // Configurar el RecyclerView y el adaptador
        imageAdapter = ImageAdapter(limitedImages.toMutableList())
        imageRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        imageRecyclerView.adapter = imageAdapter
    }

    private fun callDogApi(breedName: String) {
        // Llamar a la API para obtener imágenes de la raza específica
        val dogApiService = RetrofitClient.create()
        val call = dogApiService.getDogImages(breedName)

        call.enqueue(object : Callback<DogApiResponse> {
            override fun onResponse(call: Call<DogApiResponse>, response: Response<DogApiResponse>) {
                if (response.isSuccessful) {
                    val images = response.body()?.message ?: emptyList()
                    println("Llamada a la API exitosa. Imágenes obtenidas: $images")
                    if (images.isNotEmpty()) {
                        showDogImages(images)
                    } else {
                        println("La lista de imágenes está vacía.")
                    }
                } else {
                    println("Error en la respuesta de la API: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<DogApiResponse>, t: Throwable) {
                println("Error de red al llamar a la API: ${t.message}")
            }
        })
    }

    companion object {
        private const val CALL_PERMISSION_REQUEST_CODE = 101
    }
}
