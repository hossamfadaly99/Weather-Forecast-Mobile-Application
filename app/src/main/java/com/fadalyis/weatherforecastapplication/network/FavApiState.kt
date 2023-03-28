package com.fadalyis.weatherforecastapplication.network

import com.fadalyis.weatherforecastapplication.model.pojo.FavAddress

sealed class FavApiState{
    class Success(val data: List<FavAddress>): FavApiState()
    class Failure(val msg: Throwable): FavApiState()
    object Loading: FavApiState()
}