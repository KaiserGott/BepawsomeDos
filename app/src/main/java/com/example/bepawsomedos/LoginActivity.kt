package com.example.bepawsomedos

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bepawsomedos.models.User
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val registrarseBtn: MaterialButton = findViewById(R.id.registrarsebtn)

        registrarseBtn.setOnClickListener {
            // Agregué la lógica para redirigir a la página de registro
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference.child("usuarios")

        val nombreUsuario: TextView = findViewById(R.id.nombreUsuario)
        val contrasenia: TextView = findViewById(R.id.contrasenia)
        val loginBtn: MaterialButton = findViewById(R.id.loginbtn)

        loginBtn.setOnClickListener {
            val nombreUsuarioInput = nombreUsuario.text.toString()
            val contraseniaInput = contrasenia.text.toString()

            // Intenta buscar un usuario por nombre de usuario
            databaseReference.orderByChild("name").equalTo(nombreUsuarioInput)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            for (userSnapshot in snapshot.children) {
                                val user = userSnapshot.getValue(User::class.java)
                                if (user?.password == contraseniaInput) {
                                    // Usuario encontrado y contraseña coincide
                                    signInWithEmailAndPassword(user.mail, contraseniaInput)
                                    return
                                }
                            }
                        }

                        // Si no se encontró el usuario por nombre de usuario, intenta con correo electrónico
                        databaseReference.orderByChild("mail").equalTo(nombreUsuarioInput)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        for (userSnapshot in snapshot.children) {
                                            val user = userSnapshot.getValue(User::class.java)
                                            if (user?.password == contraseniaInput) {
                                                // Usuario encontrado y contraseña coincide
                                                signInWithEmailAndPassword(user.mail, contraseniaInput)
                                                return
                                            }
                                        }
                                    }

                                    // Usuario no encontrado o contraseña incorrecta
                                    Toast.makeText(this@LoginActivity, "Acceso Denegado", Toast.LENGTH_SHORT).show()
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(this@LoginActivity, "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show()
                                }
                            })
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@LoginActivity, "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    private fun signInWithEmailAndPassword(mail: String?, password: String) {
        auth.signInWithEmailAndPassword(mail ?: "", password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Inicio de sesión exitoso
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Si el inicio de sesión falla, muestra un mensaje al usuario.
                    Toast.makeText(baseContext, "Inicio de sesión fallido. ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }

    }

}

