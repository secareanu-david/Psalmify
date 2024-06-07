package com.example.psalmify

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData

class FragmentViewModel(private val repository: PsalmRepository) : ViewModel() {
    var allPsalm: LiveData<List<PsalmItem>> = repository.allPsalmItems.asLiveData()
    var favoritePsalms : LiveData<List<PsalmItem>> = repository.favoritePsalmItems.asLiveData()
}
class FragmentViewModelFactory(private val repository: PsalmRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FragmentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FragmentViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
