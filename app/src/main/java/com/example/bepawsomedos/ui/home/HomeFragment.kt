package com.example.bepawsomedos.ui.home

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.bepawsomedos.DataAnimalActivity
import com.example.bepawsomedos.R
import com.example.bepawsomedos.adapters.AdaptadorAnimal
import com.example.bepawsomedos.api.DogApiResponse
import com.example.bepawsomedos.databinding.FragmentHomeBinding
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private lateinit var name: String
    private lateinit var imageUrl: String
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adaptador: AdaptadorAnimal
    var listaAnimales = arrayListOf<Animal>()
    private var isButtonClicked = false
    private lateinit var buttonRight: Button
    private lateinit var animalButtonsLayout: LinearLayout
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth

    private val animalViewModel: AnimalViewModel by lazy {
        ViewModelProvider(this, AnimalViewModelFactory()).get(AnimalViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        binding.etBuscador.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun afterTextChanged(p0: Editable?) {
                filtrar(p0.toString())
            }
        })

        databaseReference = FirebaseDatabase.getInstance().reference

        // Inicializa FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()

        val textViewUserName = view.findViewById<TextView>(R.id.textViewNombreUsuario)
        val imageViewUser = view.findViewById<ImageView>(R.id.imageViewUserProfile2)

        // Obtén el usuario actual
        val currentUser = firebaseAuth.currentUser
        val userId = currentUser?.uid

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

        animalButtonsLayout = view.findViewById(R.id.animalButtonsLayout)

        animalViewModel.leerAnimalesDesdeFirebase(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (animalSnapshot in snapshot.children) {
                    val animal = animalSnapshot.getValue(Animal::class.java)
                    if (animal != null && userId != animal.usuarioId) {
                        // Verifica que el usuario actual no sea el creador del animal
                        val customView = createCustomAnimalView(animalSnapshot.key!!, animal)
                        animalButtonsLayout.addView(customView)

                        // Agrega el listener al botón derecho en cada vista personalizada
                        val buttonRight = customView.findViewById<Button>(R.id.buttonRight)
                        setupButtonRightClickListener(buttonRight, animalSnapshot.key!!)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error al leer datos desde Firebase: ${error.message}")
            }
        })
    }

    private fun setupButtonRightClickListener(buttonRight: Button, animalId: String) {
        buttonRight.setOnClickListener {
            if (isButtonClicked) {
                buttonRight.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.purple_700))
                // Aquí es donde debes agregar el objeto a la lista de favoritos
                // Puedes utilizar el animalId para identificar el animal y guardarlo en la lista de favoritos del usuario
                agregarAFavoritos(animalId)
                println("Hola")
                isButtonClicked = !isButtonClicked
            } else {
                buttonRight.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.teal_700))
                // Aquí es donde debes quitar el objeto de la lista de favoritos
                // Puedes utilizar el animalId para identificar el animal y quitarlo de la lista de favoritos del usuario
                quitarDeFavoritos(animalId)
                println("Chao")

                // Renderizar en la otra vista (si es necesario)

                isButtonClicked = !isButtonClicked
            }
        }
    }

    private fun agregarAFavoritos(animalId: String) {
        // Implementa la lógica para agregar el animal a la lista de favoritos del usuario
        // Utiliza el animalId para identificar el animal
        // Actualiza la lista de favoritos en la base de datos o donde sea que estés almacenando esa información

        val currentUser = firebaseAuth.currentUser
        val userId = currentUser?.uid

        if (userId != null) {
            databaseReference.child("usuarios").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    if (user != null) {
                        // Agrega el animalId a la lista de favoritos del usuario
                        user.listafavoritos.add(animalId)

                        // Actualiza la información en la base de datos
                        databaseReference.child("usuarios").child(userId).setValue(user)
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
    }

    private fun quitarDeFavoritos(animalId: String) {
        // Implementa la lógica para quitar el animal de la lista de favoritos del usuario
        // Utiliza el animalId para identificar el animal
        // Actualiza la lista de favoritos en la base de datos o donde sea que estés almacenando esa información

        val currentUser = firebaseAuth.currentUser
        val userId = currentUser?.uid

        if (userId != null) {
            databaseReference.child("usuarios").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    if (user != null) {
                        // Quita el animalId de la lista de favoritos del usuario
                        user.listafavoritos.remove(animalId)

                        // Actualiza la información en la base de datos
                        databaseReference.child("usuarios").child(userId).setValue(user)
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
    }

    private fun createCustomAnimalView(animalId: String, animal: Animal): View {
        val customView = layoutInflater.inflate(R.layout.custom_animal_view, null)
        val nameTextView: TextView = customView.findViewById(R.id.animalNameTextView)
        val razaTextView: TextView = customView.findViewById(R.id.animalBreedTextView)
        val ageTextView: TextView = customView.findViewById(R.id.animalAgeTextView)
        val sexTextView: TextView = customView.findViewById(R.id.animalSexTextView)
        val imageView = customView.findViewById<ImageView>(R.id.animalImageView)
        val buttonRight = customView.findViewById<Button>(R.id.buttonRight)

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

        // Agrega el listener al botón derecho
        setupButtonRightClickListener(buttonRight, animalId)

        customView.setOnClickListener {
            // Crea una instancia de la nueva actividad
            val intent = Intent(requireContext(), DataAnimalActivity::class.java)

            // Pasa datos al nuevo fragmento, si es necesario
            val bundle = Bundle()
            bundle.putString("animalId", animalId)
            println("Animal ID en DataAnimalFragment: $animalId")
            intent.putExtras(bundle)

            // Inicia la nueva actividad
            startActivity(intent)
        }

        return customView
    }

    fun setupRecyclerView() {
        binding.rvLista.layoutManager = LinearLayoutManager(context)
        adaptador = AdaptadorAnimal(listaAnimales)
        binding.rvLista.adapter = adaptador
    }

    fun filtrar(texto: String) {
        var listaFiltrada = arrayListOf<Animal>()
        adaptador.filtrar(listaFiltrada)
        // Filtra las vistas en animalButtonsLayout
        for (i in 0 until animalButtonsLayout.childCount) {
            val customView = animalButtonsLayout.getChildAt(i)
            val animalNombre = customView.findViewById<TextView>(R.id.animalNameTextView).text.toString()
            val animalRaza = customView.findViewById<TextView>(R.id.animalBreedTextView).text.toString()
            if (animalNombre.toLowerCase().contains(texto.toLowerCase()) || animalRaza.toLowerCase().contains(texto.toLowerCase())) {
                customView.visibility = View.VISIBLE
            } else {
                customView.visibility = View.GONE
            }
        }
    }
}
