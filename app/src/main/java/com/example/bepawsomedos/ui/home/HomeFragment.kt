package com.example.bepawsomedos.ui.home

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.bepawsomedos.R
import com.example.bepawsomedos.api.DogApiResponse
import com.example.bepawsomedos.models.Animal
import com.example.bepawsomedos.models.User
import com.example.bepawsomedos.viewModels.AnimalViewModel
import com.example.bepawsomedos.viewModels.AnimalViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private lateinit var name: String
    private lateinit var imageUrl: String

    private val animalViewModel: AnimalViewModel by lazy {
        ViewModelProvider(this, AnimalViewModelFactory()).get(AnimalViewModel::class.java)
    }

    private lateinit var animalButtonsLayout: LinearLayout
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        databaseReference = FirebaseDatabase.getInstance().reference

        // Inicializa FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()

        val textViewUserName = view.findViewById<TextView>(R.id.textViewNombreUsuario)
        val imageViewUser = view.findViewById<ImageView>(R.id.imageViewUserProfile2)

        // Obtén el usuario actual
        val currentUser = firebaseAuth.currentUser

        if (currentUser != null) {
            val userId = currentUser.uid
            databaseReference.child("usuarios").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    if (user != null) {
                        name = user.name ?: ""
                        imageUrl = user.imageUrl ?: ""

                        Log.d(TAG, "Datos del usuario obtenidos correctamente.")
                        Log.d(TAG, "Nombre de usuario: $name")
                        Log.d(TAG, "URL de la imagen: $imageUrl")

                        textViewUserName.text = name

                        if (imageUrl.isNotEmpty()) {
                            Glide.with(requireContext()).load(imageUrl).into(imageViewUser)
                        }
                    } else {
                        Log.e(TAG, "Error: No se pudo obtener el objeto User.")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Error al obtener datos del usuario: ${error.message}")
                    Toast.makeText(requireContext(), "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show()
                }
            })
        }

        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        animalButtonsLayout = view.findViewById(R.id.animalButtonsLayout)

        animalViewModel.leerAnimalesDesdeFirebase(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (animalSnapshot in snapshot.children) {
                    val animal = animalSnapshot.getValue(Animal::class.java)
                    if (animal != null) {
                        val customView = createCustomAnimalView(animalSnapshot.key!!, animal)
                        animalButtonsLayout.addView(customView)
                    } else {
                        println("Error: Animal object is null.")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error al leer datos desde Firebase: ${error.message}")
            }
        })
    }

    private fun createCustomAnimalView(animalId: String, animal: Animal): View {
        val customView = layoutInflater.inflate(R.layout.custom_animal_view, null)
        val nameTextView: TextView = customView.findViewById(R.id.animalNameTextView)
        val razaTextView: TextView = customView.findViewById(R.id.animalBreedTextView)
        val ageTextView: TextView = customView.findViewById(R.id.animalAgeTextView)
        val sexTextView: TextView = customView.findViewById(R.id.animalSexTextView)
        val imageView = customView.findViewById<ImageView>(R.id.animalImageView)

        nameTextView.text = "Nombre: ${animal.nombre}"
        razaTextView.text = "Raza: ${animal.raza}"
        ageTextView.text = "Edad: ${animal.edad}"
        sexTextView.text = "Sexo: ${animal.sexo}"

        // Llama al nuevo método para obtener la imagen de la raza
        animalViewModel.getDogImage(animal.raza, object : Callback<DogApiResponse> {
            override fun onResponse(call: Call<DogApiResponse>, response: Response<DogApiResponse>) {
                if (response.isSuccessful) {
                    // Carga la imagen desde la URL utilizando Glide
                    val imageUrl = response.body()?.message
                    if (imageUrl != null) {
                        Glide.with(requireContext())
                            .load(imageUrl)
                            .into(imageView)
                    }
                } else {
                    Log.e(TAG, "Error al obtener la imagen del perro: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DogApiResponse>, t: Throwable) {
                Log.e(TAG, "Error de red al obtener la imagen del perro", t)
            }
        })
        customView.setOnClickListener {
            // Crea una instancia del nuevo fragmento
            val nuevoFragmento = DataAnimalFragment()

            // Pasa datos al nuevo fragmento, si es necesario
            val bundle = Bundle()
            bundle.putString("animalId", animalId)
            nuevoFragmento.arguments = bundle

            // Realiza la transacción del fragmento
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            // fragmentTransaction.replace(R.id.fragment_home.xml, nuevoFragmento)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        return customView
    }
}