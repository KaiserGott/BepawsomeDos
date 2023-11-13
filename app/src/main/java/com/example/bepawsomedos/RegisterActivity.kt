package com.example.bepawsomedos

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bepawsomedos.models.User
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance()

        val nuevoUsuario: EditText = findViewById(R.id.nuevoUsuario)
        val nuevaContrasenia: EditText = findViewById(R.id.nuevaContrasenia)
        val nuevoCorreo: EditText = findViewById(R.id.nuevoCorreo)
        val nuevoTelefono: EditText = findViewById(R.id.nuevoTelefono) // Nuevo campo para el teléfono
        val registerBtn: MaterialButton = findViewById(R.id.registrarsebtn)

        registerBtn.setOnClickListener {
            val nuevoUsuarioInput = nuevoUsuario.text.toString()
            val nuevaContraseniaInput = nuevaContrasenia.text.toString()
            val nuevoCorreoInput = nuevoCorreo.text.toString()
            val nuevoTelefonoInput = nuevoTelefono.text.toString()

            // Verifica que los campos no estén vacíos
            if (nuevoUsuarioInput.isNotEmpty() && nuevaContraseniaInput.isNotEmpty() && nuevoCorreoInput.isNotEmpty()) {
                // Crea un nuevo usuario en Firebase Authentication
                auth.createUserWithEmailAndPassword(nuevoCorreoInput, nuevaContraseniaInput)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Registro exitoso
                            val userId = auth.currentUser?.uid
                            // Guarda el nuevo usuario en la base de datos de Firebase
                            userId?.let {
                                val user = User(
                                    it,
                                    nuevoUsuarioInput,
                                    nuevaContraseniaInput,
                                    nuevoCorreoInput,
                                    "",
                                    nuevoTelefonoInput
                                )
                                databaseReference.reference.child("usuarios").child(userId).setValue(user)
                                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                                // Lleva al usuario de vuelta a la actividad de inicio de sesión
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            // Si el registro falla, muestra un mensaje de error al usuario.
                            Toast.makeText(this, "Error en el registro: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                // Si alguno de los campos está vacío, muestra un mensaje al usuario.
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
