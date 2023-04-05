package com.fadalyis.weatherforecastapplication.model

import com.fadalyis.weatherforecastapplication.db.LocalSource
import com.fadalyis.weatherforecastapplication.model.pojo.AlertSchedule
import com.fadalyis.weatherforecastapplication.model.pojo.CurrentResponse
import com.fadalyis.weatherforecastapplication.model.pojo.FavAddress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeLocalSource(
    var currentWeather: CurrentResponse?,
    var favAddressList: MutableList<FavAddress>? = mutableListOf(),
    var alertsList: MutableList<AlertSchedule>? = mutableListOf()
) : LocalSource {
    override suspend fun insertCurrentWeather(currentWeather: CurrentResponse) {
        this.currentWeather = currentWeather
    }

    override suspend fun getCurrentWeather(): Flow<CurrentResponse?> {
        return flowOf(currentWeather)
    }

    override suspend fun insertFavLocation(address: FavAddress) {
        favAddressList?.add(address)
    }

    override suspend fun deleteFavLocation(address: FavAddress) {
        favAddressList?.remove(address)
    }

    override suspend fun getFavLocations(): Flow<List<FavAddress>> {
        return flowOf(favAddressList?: mutableListOf())
    }

    override suspend fun insertAlert(alert: AlertSchedule) {
        alertsList?.add(alert)
    }

    override suspend fun deleteAlert(id: String) {
        val alert: AlertSchedule = alertsList?.first { it.id == id }!!
        alertsList?.remove(alert)
    }

    override suspend fun getAlerts(): Flow<List<AlertSchedule>> {
        return flowOf(alertsList?: mutableListOf())
    }
}