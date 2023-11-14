package com.example.bepawsomedos.api

data class DogBreedsResponse(
    val message: Map<String, List<String>>,
    val status: String
)