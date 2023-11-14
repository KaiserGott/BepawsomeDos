package com.example.bepawsomedos.ui.home

import ImagePagerAdapter
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.bepawsomedos.R
import com.example.bepawsomedos.api.DogApiResponse2
import com.example.bepawsomedos.api.ImageAdapter
import com.example.bepawsomedos.api.RetrofitClient
import com.example.bepawsomedos.models.Animal
import com.example.bepawsomedos.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DataAnimalFragment : Fragment() {

    private lateinit var nameTextView: TextView
    private lateinit var ageTextView: TextView
    private lateinit var sexTextView: TextView
    private lateinit var razaTextView: TextView
    private lateinit var ownerTextView: TextView
    private lateinit var ownerImageView: ImageView
    private lateinit var imageRecyclerView: RecyclerView
    private lateinit var viewPager: ViewPager2

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
        imageRecyclerView = view.findViewById(R.id.imageRecyclerView)
        viewPager = view.findViewById(R.id.viewPager)
        ownerTextView = view.findViewById(R.id.ownerTextView)
        ownerImageView = view.findViewById(R.id.ownerImageView)

        // Recuperar el ID del animal de los argumentos del fragmento
        val animalId = arguments?.getString("animalId")

        // Verificar que animalId no sea nulo antes de usarlo en Firebase Database
        if (animalId != null) {
            // Obtener y mostrar los datos del animal desde Firebase
            val databaseReference = FirebaseDatabase.getInstance().reference
            databaseReference.child("animales").child(animalId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val animal = snapshot.getValue(Animal::class.java)
                        if (animal != null) {
                            // Actualizar las vistas con los datos del animal
                            nameTextView.text = "Nombre: ${animal.nombre}"
                            ageTextView.text = "Edad: ${animal.edad}"
                            sexTextView.text = "Sexo: ${animal.sexo}"
                            razaTextView.text = "Raza: ${animal.raza}"

                            // Obtener el dueño del animal desde Firebase
                            val usuarioId = animal.usuarioId
                            val userReference = databaseReference.child("usuarios").child(usuarioId)
                            userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(userSnapshot: DataSnapshot) {
                                    val user = userSnapshot.getValue(User::class.java)
                                    if (user != null) {
                                        println(user)
                                        // Actualizar las vistas con los datos del dueño
                                        ownerTextView.text = user.name
                                        // Cargar la imagen del dueño usando Glide (asegúrate de tener la biblioteca agregada en tu proyecto)
                                        Glide.with(requireContext())
                                            .load(user.imageUrl)
                                            .into(ownerImageView)
                                    } else {
                                        // Manejar el caso en el que no se pueda obtener el objeto User
                                        println("Error: No se pudo obtener el objeto User.")
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    // Manejar el caso de error al obtener datos del usuario
                                    println("Error al obtener datos del usuario desde Firebase: ${error.message}")
                                }
                            })

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


        val adoptarButton = view.findViewById<Button>(R.id.adoptionButton)
        adoptarButton.setOnClickListener {
            // Obtener el animalId del argumento
            val animalId = arguments?.getString("animalId")

            // Verificar que animalId no sea nulo antes de adoptar
            if (animalId != null) {
                // Lógica para agregar el animal a la lista de adopciones del usuario
                adoptarAnimal(animalId)
            }
        }

        val callButton = view.findViewById<Button>(R.id.callButton)
        callButton.setOnClickListener {
            // Verifica si tienes permiso para realizar llamadas
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.CALL_PHONE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Si tienes permiso, obtén el número de teléfono del dueño del animal
                val telefonoDueño = "1234"

                // Inicia la llamada
                val intent = Intent(Intent.ACTION_CALL)
                intent.data = Uri.parse("tel:$telefonoDueño")
                startActivity(intent)
            } else {
                // Si no tienes permiso, solicita el permiso al usuario
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.CALL_PHONE),
                    CALL_PERMISSION_REQUEST_CODE
                )
            }
        }
    }


    private fun fetchOwnerData(ownerId: String) {
        // Ensure view is not null
        val currentView = view ?: return

        // Obtener y mostrar los datos del dueño del animal desde Firebase
        val databaseReference = FirebaseDatabase.getInstance().reference
        databaseReference.child("usuarios").child(ownerId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val owner = snapshot.getValue(User::class.java)
                if (owner != null) {
                    // Mostrar el nombre del dueño en la interfaz de usuario
                    val ownerName = owner.name
                    currentView.findViewById<TextView>(R.id.ownerTextView)?.text = "Dueño: $ownerName"

                    // Load the user's image using Glide
                    val ownerImageView = currentView.findViewById<ImageView>(R.id.ownerImageView)
                    val imgUrl = owner.imageUrl // replace with the actual field in your User class
                    // Use Glide to load the image from the URL
                    Glide.with(requireContext())
                        .load(imgUrl)
                        .placeholder(R.drawable.baseline_person_24) // Placeholder drawable
                        .error(R.drawable.baseline_person_24) // Error drawable if loading fails
                        .into(ownerImageView)
                } else {
                    // Manejar el caso en el que no se pueda obtener el objeto User
                    println("Error: No se pudo obtener el objeto User para el dueño del animal.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar el caso de error al obtener datos del dueño del animal
                println("Error al obtener datos del dueño del animal desde Firebase: ${error.message}")
            }
        })
    }

    private fun adoptarAnimal(animalId: String) {
        // Obtener el ID del usuario actual
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            // Obtener la lista actual de adopciones del usuario
            val userReference = FirebaseDatabase.getInstance().getReference("usuarios").child(userId)
            userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    if (user != null) {
                        // Agregar el nuevo ID del animal a la lista existente de adopciones
                        user.listaadopciones.add(animalId)

                        // Actualizar la lista de adopciones del usuario en la base de datos
                        val updates = hashMapOf<String, Any>("listaadopciones" to user.listaadopciones)
                        FirebaseDatabase.getInstance().getReference("usuarios").child(userId).updateChildren(updates)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    // Notificar al usuario que la adopción fue exitosa
                                    Toast.makeText(requireContext(), "Adopción exitosa", Toast.LENGTH_SHORT).show()
                                } else {
                                    // Notificar al usuario en caso de error
                                    Toast.makeText(requireContext(), "Error al adoptar el animal", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        // Manejar el caso en el que no se pueda obtener el objeto User
                        println("Error: No se pudo obtener el objeto User.")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Manejar el caso de error al obtener datos del usuario
                    println("Error al obtener datos del usuario desde Firebase: ${error.message}")
                }
            })
        }
    }

    private fun callDogApi(breedName: String) {
        // Llamar a la API para obtener imágenes de la raza específica
        val dogApiService = RetrofitClient.create()
        val call = dogApiService.getDogImages(breedName)

        call.enqueue(object : Callback<DogApiResponse2> {
            override fun onResponse(
                call: Call<DogApiResponse2>,
                response: Response<DogApiResponse2>
            ) {
                if (response.isSuccessful) {
                    val images: List<String> = response.body()?.message.orEmpty()
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

            override fun onFailure(call: Call<DogApiResponse2>, t: Throwable) {
                println("Error de red al llamar a la API: ${t.message}")
            }
        })
    }

    // En la función showDogImages, actualiza el RecyclerView con las imágenes
    private fun showDogImages(images: List<String>) {
        // Limitar la lista a solo 5 imágenes
        val limitedImages = images.take(5)

        // Verifica que las URLs de las imágenes sean válidas
        if (limitedImages.isNotEmpty()) {
            // Configurar el ViewPager2 y su adaptador
            val imagePagerAdapter = ImagePagerAdapter(limitedImages)
            viewPager.adapter = imagePagerAdapter
        } else {
            println("La lista de imágenes está vacía.")
        }
    }

    companion object {
        private const val CALL_PERMISSION_REQUEST_CODE=101
        }
}