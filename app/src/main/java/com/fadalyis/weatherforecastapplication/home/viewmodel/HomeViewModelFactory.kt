package com.fadalyis.weatherforecastapplication.home.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fadalyis.weatherforecastapplication.model.RepositoryInterface

class HomeViewModelFactory (private val _repoInterface: RepositoryInterface):
    ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(HomeViewModel::class.java)){
            HomeViewModel(_repoInterface) as T
        }
        else{
            throw IllegalArgumentException("ViewModel class not found")
        }
    }
}