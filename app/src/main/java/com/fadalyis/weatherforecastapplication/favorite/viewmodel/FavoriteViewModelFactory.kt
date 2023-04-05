package com.fadalyis.weatherforecastapplication.favorite.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fadalyis.weatherforecastapplication.model.RepositoryInterface

class FavoriteViewModelFactory (private val _repoInterface: RepositoryInterface):
    ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)){
            FavoriteViewModel(_repoInterface) as T
        }
        else{
            throw IllegalArgumentException("ViewModel class not found")
        }
    }
}