package com.example.bepawsomedos.models

class User(
    val name: String = "",
    val mail: String = "",
    val password: String = "",
    val imageUrl: String = "",
    val telefono: String = "",

    val favoritos: MutableList<Animal> = mutableListOf(),
    val adopciones: MutableList<Animal> = mutableListOf()
) {
    constructor() : this("", "", "", "", "")

    fun agregarAnimalAFavoritos(animal: Animal) {
        favoritos.add(animal)
    }

    fun eliminarAnimalDeFavoritos(animal: Animal) {
        favoritos.remove(animal)
    }

    fun agregarAnimalAAdopciones(animal: Animal) {
        adopciones.add(animal)
    }


}