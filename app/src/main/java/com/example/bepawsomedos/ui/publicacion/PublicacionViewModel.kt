package com.example.bepawsomedos.ui.publicacion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PublicacionViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Estas en Publicaciones"
    }
    val text: LiveData<String> = _text
}