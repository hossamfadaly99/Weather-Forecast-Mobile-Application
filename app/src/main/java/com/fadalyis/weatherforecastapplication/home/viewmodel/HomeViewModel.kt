package com.fadalyis.weatherforecastapplication.home.viewmodel


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fadalyis.weatherforecastapplication.model.RepositoryInterface
import com.fadalyis.weatherforecastapplication.network.ApiState
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

private const val TAG = "HomeViewModel"
class HomeViewModel(private val _repoInterface: RepositoryInterface) : ViewModel() {
    private var _weather: MutableStateFlow<ApiState> =
        MutableStateFlow(ApiState.Loading)
    val weather: StateFlow<ApiState> = _weather

    init {
        getSavedWeather()
    }

    private fun getSavedWeather() = viewModelScope.launch {
        _repoInterface.getCurrentWeatherOffline()
            .catch {
                Log.i(TAG, "getSavedWeather: catch")
                _weather.value = ApiState.Failure(it)
            }
            .collect {
                Log.i(TAG, "getSavedWeather: collect ")
                if (it != null)
                    _weather.value = ApiState.Success(it)
                else {
                    _weather.value = ApiState.Failure(Throwable("no items in database - implemented throwable"))
                }
            }
    }

    fun getOnlineWeather(
        lat: String,
        lon: String,
        lang: String,
        units: String
    ) = viewModelScope.launch {
        _repoInterface.getCurrentWeatherOnline(lat, lon, lang, units)
    }

}