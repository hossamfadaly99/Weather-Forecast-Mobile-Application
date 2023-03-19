package com.fadalyis.weatherforecastapplication.network

import com.fadalyis.weatherforecastapplication.model.pojo.CurrentResponse

sealed class DatabaseState {
    class Success(val data: CurrentResponse): DatabaseState()
    class Failure(val msg: Throwable): DatabaseState()
    object Loading: DatabaseState()
}