package com.example.bepawsomedos.ui.configuracion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ConfiguracionViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "Aca deberia ir Un Switch"
    }
    val text: LiveData<String> = _text
}