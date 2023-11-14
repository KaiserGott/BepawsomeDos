package com.example.bepawsomedos.models

data class User(
    val id: String = "",
    var name: String = "",
    var password: String = "",
    var mail: String = "",
    var imageUrl: String = "",
    var telefono: String = "",
    var listafavoritos: MutableList<String> = mutableListOf(),  // Lista de animales favoritos
    val listaadopciones: MutableList<String> = mutableListOf(),  // Lista de animales adoptados
){
    constructor() : this("","","","","","")
}