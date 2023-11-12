package com.example.bepawsomedos.ui.home

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.bepawsomedos.MainActivity
import com.example.bepawsomedos.R
import com.example.bepawsomedos.api.DogApiResponse
import com.example.bepawsomedos.models.Animal
import com.example.bepawsomedos.models.User
import com.example.bepawsomedos.viewModels.AnimalViewModel
import com.example.bepawsomedos.viewModels.AnimalViewModelFactory
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {
    lateinit var nameUserCredential: String
    lateinit var imageUrlUserCredential: String


    private val animalViewModel: AnimalViewModel by lazy {
        ViewModelProvider(this, AnimalViewModelFactory()).get(AnimalViewModel::class.java)
    }

    private lateinit var animalButtonsLayout: LinearLayout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val sharedPreferences = requireContext().getSharedPreferences("Credenciales", Context.MODE_PRIVATE)

        val jsonObjectString = sharedPreferences.getString("UserLogueado", null)
        var jsonObject: JSONObject? = null
        if (jsonObjectString != null) {
            try {
                jsonObject = JSONObject(jsonObjectString)
                val name = jsonObject.getString("name")
                val imageUrl = jsonObject.getString("imageUrl")
                // Crea el objeto User con los datos obtenidos
                val userObject = User(name, imageUrl)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        var gson = Gson()
        var userObject = gson.fromJson(jsonObject.toString(), User::class.java)

        if (userObject != null) {
            nameUserCredential = userObject.name ?: ""
            imageUrlUserCredential = userObject.imageUrl ?: ""
        } else {
            // Maneja el caso en el que userObject es nulo, por ejemplo, muestra un mensaje de error.
            Log.e(TAG, "Error: userObject is null")
        }

        val textViewUserName = view.findViewById<TextView>(R.id.textViewNombreUsuario)
        textViewUserName.text = nameUserCredential

        val imageViewUser = view.findViewById<ImageView>(R.id.imageViewUserProfile2)
        if (imageUrlUserCredential.isNotEmpty()) {
            Picasso.get().load(imageUrlUserCredential).into(imageViewUser)
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
            fragmentTransaction.replace(R.id.containerFragmentDataAnimal, nuevoFragmento)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        return customView
    }
}