package com.example.bepawsomedos.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val isDarkModeEnabled = MutableLiveData<Boolean>()
}