package com.example.bepawsomedos.ui.home

import android.content.pm.PackageManager
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
import com.example.bepawsomedos.R
import com.example.bepawsomedos.api.DogApiResponse
import com.example.bepawsomedos.api.ImageAdapter
import com.example.bepawsomedos.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.min
import android.Manifest
import com.example.bepawsomedos.models.Animal
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DataAnimalFragment : Fragment() {

    private lateinit var nameTextView: TextView
    private lateinit var ageTextView: TextView
    private lateinit var sexTextView: TextView
    private lateinit var razaTextView: TextView
    private lateinit var subRazaTextView: TextView
    private lateinit var imageRecyclerView: RecyclerView
    private lateinit var imageView: ImageView
    private lateinit var imageAdapter: ImageAdapter
    private val telefonoAnimal = "123456789"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_data_animal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar las vistas
        nameTextView = view.findViewById(R.id.nameTextView)
        ageTextView = view.findViewById(R.id.ageTextView)
        sexTextView = view.findViewById(R.id.sexTextView)
        razaTextView = view.findViewById(R.id.razaTextView)
        subRazaTextView = view.findViewById(R.id.subRazaTextView)
        imageRecyclerView = view.findViewById(R.id.imageRecyclerView)

        // Dentro de onViewCreated en DataAnimalFragment
// Recuperar el ID del animal de los argumentos del fragmento
        val animalId = arguments?.getString("animalId")

// Verificar que animalId no sea nulo antes de usarlo en Firebase Database
        if (animalId != null) {
            // Obtener y mostrar los datos del animal desde Firebase
            val databaseReference = FirebaseDatabase.getInstance().reference
            databaseReference.child("animales").child(animalId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val animal = snapshot.getValue(Animal::class.java)
                    if (animal != null) {
                        // Actualizar las vistas con los datos del animal
                        nameTextView.text = "Nombre: ${animal.nombre}"
                        ageTextView.text = "Edad: ${animal.edad}"
                        sexTextView.text = "Sexo: ${animal.sexo}"
                        razaTextView.text = "Raza: ${animal.raza}"

                        // Llamar a la API para obtener imágenes de la raza específica
                        callDogApi(animal.raza)
                    } else {
                        // Manejar el caso en el que no se pueda obtener el objeto Animal
                        println("Error: No se pudo obtener el objeto Animal.")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Manejar el caso de error al obtener datos del animal
                    println("Error al obtener datos del animal desde Firebase: ${error.message}")
                }
            })
        } else {
            // Manejar el caso en el que animalId es nulo, por ejemplo, mostrar un mensaje de error o volver atrás
        }


        val callButton = view.findViewById<Button>(R.id.callButton)
        callButton.setOnClickListener {
            // Verifica si tienes permiso para realizar llamadas
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CALL_PHONE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Si tienes permiso, inicia la llamada
                // ...
            } else {
                // Si no tienes permiso, solicita el permiso al usuario
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.CALL_PHONE),
                    CALL_PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    // En la función showDogImages, actualiza el RecyclerView con las imágenes
    private fun showDogImages(images: String) {
        // Convierte la cadena de imágenes en una lista
        val imageList = images.split(",").map { it.trim() }

        // Limitar la lista a solo 5 imágenes
        val limitedImages = imageList.subList(0, min(5, imageList.size))

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
                    val images: String = response.body()?.message.orEmpty()
                    println("Llamada a la API exitosa. Imágenes obtenidas: $images")
                    if (images.isNotEmpty()) {
                        showDogImages(response.body()?.message.orEmpty())
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
