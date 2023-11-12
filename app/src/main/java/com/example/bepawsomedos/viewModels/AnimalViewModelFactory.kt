package com.example.bepawsomedos.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AnimalViewModelFactory : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AnimalViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AnimalViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}