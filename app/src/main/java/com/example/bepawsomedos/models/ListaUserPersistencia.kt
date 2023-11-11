package com.example.bepawsomedos.models

class ListaUserPersistencia (
    val usuariosList: MutableList<User> = mutableListOf()
    ){
        init {
            usuariosList.add(
                User(
                    "Admin",
                    "admin.gmail@gmail.com",
                    "Admin",
                    "https://img.freepik.com/foto-gratis/hombre-camisa-azul-pulgar-arriba_1368-4929.jpg?w=900&t=st=1699379871~exp=1699380471~hmac=7999dcf71bae80b3d1862a706bc95ded4a6e2f73df1df04cc9f987dcd4c30326",
                    "123456"
                )
            )
            usuariosList.add(
                User(
                    "Tester",
                    "tester@gmail.com",
                    "Tester",
                    "https://icon-library.com/images/google-user-icon/google-user-icon-21.jpg",
                    "234567"
                )
            )
            usuariosList.add(
                User(
                    "Pepe",
                    "pepe@gmail.com",
                    "Pepe",
                    "https://icon-library.com/icon/google-user-icon-18.html",
                    "345678"
                )
            )
            usuariosList.add(
                User(
                    "Pablo",
                    "pablo@gmail.com",
                    "Pablo",
                    "https://icon-library.com/icon/google-user-icon-19.html",
                    "456789"
                )
            )
        }

        fun encontrarUsuario(nombre: String, password: String): User? {
            return usuariosList.find { it.name == nombre && it.password == password }
        }

        fun obtenerTester(): User {
            return usuariosList[1]
        }
    }

