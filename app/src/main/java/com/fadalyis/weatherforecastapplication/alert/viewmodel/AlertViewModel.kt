package com.fadalyis.weatherforecastapplication.alert.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fadalyis.weatherforecastapplication.model.RepositoryInterface
import com.fadalyis.weatherforecastapplication.model.pojo.AlertSchedule
import com.fadalyis.weatherforecastapplication.model.pojo.FavAddress
import com.fadalyis.weatherforecastapplication.network.AlertApiState
import com.fadalyis.weatherforecastapplication.network.FavApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.UUID

private const val TAG = "AlertViewModel"
class AlertViewModel (private val _repoInterface: RepositoryInterface) : ViewModel() {
    private var _alert: MutableStateFlow<AlertApiState> =
        MutableStateFlow(AlertApiState.Loading)
    val alert: StateFlow<AlertApiState> = _alert


    init {
        getSavedAlert()
    }

    fun getSavedAlert() = viewModelScope.launch {
        _repoInterface.getAlerts()
            .catch {
                Log.i(TAG, "getSavedWeather: catch")
                _alert.value = AlertApiState.Failure(it)
            }
            .collect {
//                Log.i(TAG, "getSavedWeather: collect ")
                if (it != null)
                    _alert.value = AlertApiState.Success(it)
                else {
                    _alert.value = AlertApiState.Failure(Throwable("no items in database - implemented throwable"))
                }
            }
    }

    fun saveAlert(alert: AlertSchedule) = viewModelScope.launch{
        _repoInterface.insertAlert(alert)
    }

    fun deleteAlert(id: String) = viewModelScope.launch{
        _repoInterface.deleteAlert(id)
    }
}