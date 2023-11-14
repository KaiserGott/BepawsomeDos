package com.example.bepawsomedos.ui.configuracion

import android.content.ContentValues
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.bepawsomedos.R
import com.example.bepawsomedos.databinding.FragmentConfiguracionBinding
import com.example.bepawsomedos.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import org.json.JSONObject

class ConfiguracionFragment : Fragment() {

    private lateinit var editImageUrl: EditText
    private lateinit var editMail: EditText
    private lateinit var editName: EditText
    private lateinit var editPassword: EditText
    private lateinit var editTelefono: EditText
    private lateinit var btnGuardar: Button
    private lateinit var name: String
    private lateinit var imageUrl: String
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private var userJson: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_configuracion, container, false)

        editImageUrl = view.findViewById(R.id.editImageUrl)
        editMail = view.findViewById(R.id.editMail)
        editName = view.findViewById(R.id.editName)
        editPassword = view.findViewById(R.id.editPassword)
        editTelefono = view.findViewById(R.id.editTelefono)
        btnGuardar = view.findViewById(R.id.btnGuardar)

        btnGuardar.setOnClickListener {
            guardarPerfil()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        databaseReference = FirebaseDatabase.getInstance().reference

        // Inicializa FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()

        // Obtén el usuario actual
        val currentUser = firebaseAuth.currentUser
        val userId = currentUser?.uid

        if (currentUser != null) {
            val userId = currentUser.uid
            databaseReference.child("usuarios").child(userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = snapshot.getValue(User::class.java)
                        if (user != null) {
                            val gson = Gson()
                            userJson = gson.toJson(user) // Asigna el JSON del usuario

                            println("Este es el logueado: $userJson")
                            //println(user)
                            //println(user.name)

                        } else {
                            Log.e(ContentValues.TAG, "Error: No se pudo obtener el objeto User.")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e(
                            ContentValues.TAG,
                            "Error al obtener datos del usuario: ${error.message}"
                        )
                        Toast.makeText(
                            requireContext(),
                            "Error al obtener datos del usuario",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }

    private fun guardarPerfil() {
        val imageUrl = editImageUrl.text.toString()
        val mail = editMail.text.toString()
        val name = editName.text.toString()
        val password = editPassword.text.toString()
        val telefono = editTelefono.text.toString()

        // Comprueba si userJson no es nulo
        if (userJson != null) {
            // Convierte el userJson en un objeto User
            val gson = Gson()
            val user = gson.fromJson(userJson, User::class.java)

            // Actualiza los campos del objeto User con los nuevos valores (no vacíos)
            if (imageUrl.isNotEmpty()) {
                user.imageUrl = imageUrl
            }
            if (mail.isNotEmpty()) {
                user.mail = mail
            }
            if (name.isNotEmpty()) {
                user.name = name
            }
            if (password.isNotEmpty()) {
                user.password = password
            }
            if (telefono.isNotEmpty()) {
                user.telefono = telefono
            }

            // Actualiza los datos del perfil en Firebase
            val currentUser = firebaseAuth.currentUser
            val userId = currentUser?.uid
            if (userId != null) {
                databaseReference.child("usuarios").child(userId).setValue(user)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            println("Los datos del perfil se actualizaron con éxito.")
                            Toast.makeText(
                                requireContext(),
                                "Se guardaron los cambios con éxito",
                                Toast.LENGTH_SHORT
                            ).show()
                            clearFields()
                        } else {
                            println("Error al actualizar los datos del perfil.")
                            Toast.makeText(
                                requireContext(),
                                "Error al guardar los datos.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                println("Error: ID de usuario nulo.")
                Toast.makeText(
                    requireContext(),
                    "Error al guardar los datos.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            // Maneja el caso donde userJson es nulo
            println("Error: userJson es nulo.")
            Toast.makeText(requireContext(), "Error al guardar los datos.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun clearFields() {
        editImageUrl.text.clear()
        editMail.text.clear()
        editName.text.clear()
        editPassword.text.clear()
        editTelefono.text.clear()
    }

}
