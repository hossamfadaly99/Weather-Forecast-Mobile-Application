package com.fadalyis.weatherforecastapplication.network

import com.fadalyis.weatherforecastapplication.model.pojo.CurrentResponse

sealed class ApiState{
    class Success(val data: CurrentResponse): ApiState()
    class Failure(val msg: Throwable): ApiState()
    object Loading: ApiState()
}