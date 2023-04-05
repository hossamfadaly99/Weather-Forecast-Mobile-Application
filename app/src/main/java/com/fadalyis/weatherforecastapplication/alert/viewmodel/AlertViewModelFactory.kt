package com.fadalyis.weatherforecastapplication.alert.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fadalyis.weatherforecastapplication.model.RepositoryInterface

class AlertViewModelFactory (private val _repoInterface: RepositoryInterface):
    ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AlertViewModel::class.java)){
            AlertViewModel(_repoInterface) as T
        }
        else{
            throw IllegalArgumentException("ViewModel class not found")
        }
    }
}