package com.example.bepawsomedos.models

data class User(
    val id: String = "",
    val name: String = "",
    val password: String = "",
    val mail: String = "",
    val imageUrl: String = "",
    val telefono: String = "",
){
    constructor() : this("","","","","","")
}