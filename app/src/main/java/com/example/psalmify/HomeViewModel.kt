package com.example.psalmify

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData

class HomeViewModel(private val repository: PsalmRepository) : ViewModel() {
    var allPsalm: LiveData<List<PsalmItem>> = repository.allPsalmItems.asLiveData()
}
class HomeViewModelFactory(private val repository: PsalmRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
