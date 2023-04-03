package com.fadalyis.weatherforecastapplication.network

import com.fadalyis.weatherforecastapplication.model.pojo.AlertSchedule
import com.fadalyis.weatherforecastapplication.model.pojo.FavAddress

sealed class AlertApiState{
    class Success(val data: List<AlertSchedule>): AlertApiState()
    class Failure(val msg: Throwable): AlertApiState()
    object Loading: AlertApiState()
}