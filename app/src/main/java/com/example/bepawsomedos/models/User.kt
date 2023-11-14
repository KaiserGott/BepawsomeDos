package com.example.bepawsomedos.models

data class User(
    val id: String = "",
    val name: String = "",
    val password: String = "",
    val mail: String = "",
    val imageUrl: String = "",
    val telefono: String = "",
    var listafavoritos: MutableList<String> = mutableListOf(),  // Lista de animales favoritos
    val listaadopciones: List<String> = listOf(),  // Lista de animales adoptados
){
    constructor() : this("","","","","","")
}