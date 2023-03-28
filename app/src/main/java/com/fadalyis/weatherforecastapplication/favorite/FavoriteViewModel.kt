package com.fadalyis.weatherforecastapplication.favorite

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fadalyis.weatherforecastapplication.Home.TAG
import com.fadalyis.weatherforecastapplication.model.RepositoryInterface
import com.fadalyis.weatherforecastapplication.model.pojo.FavAddress
import com.fadalyis.weatherforecastapplication.network.FavApiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class FavoriteViewModel (private val _repoInterface: RepositoryInterface) : ViewModel() {
    private var _location: MutableStateFlow<FavApiState> =
        MutableStateFlow(FavApiState.Loading)
    val location: StateFlow<FavApiState> = _location


    init {
        getSavedLocation()
    }

    private fun getSavedLocation() = viewModelScope.launch {
        _repoInterface.getFavLocations()
            .catch {
                Log.i(TAG, "getSavedWeather: catch")
                _location.value = FavApiState.Failure(it)
            }
            .collect {
                Log.i(TAG, "getSavedWeather: collect ")
                if (it != null)
                    _location.value = FavApiState.Success(it)
                else {
                    _location.value = FavApiState.Failure(Throwable("no items in database - implemented throwable"))
                }
            }
    }

    fun saveLocation(favAddress: FavAddress) = viewModelScope.launch{
        _repoInterface.insertFavLocation(favAddress)
    }

    fun deleteLocation(favAddress: FavAddress) = viewModelScope.launch{
        _repoInterface.deleteFavLocation(favAddress)
    }
}