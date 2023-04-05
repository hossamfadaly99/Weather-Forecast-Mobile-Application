package com.fadalyis.weatherforecastapplication.model

import com.fadalyis.weatherforecastapplication.model.pojo.AlertSchedule
import com.fadalyis.weatherforecastapplication.model.pojo.Current
import com.fadalyis.weatherforecastapplication.model.pojo.CurrentResponse
import com.fadalyis.weatherforecastapplication.model.pojo.FavAddress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeRepository():RepositoryInterface {

    private var favList : MutableList<FavAddress> = mutableListOf()
    private var alertList : MutableList<AlertSchedule> = mutableListOf()
    private val current= Current(
        1, 1.2, "1", 2.1, 1, 1, "1", "1",
        2.2, "2.3", 1, listOf(), 1, 2.4, 2.5
    )
    private var currentResponse: CurrentResponse = CurrentResponse(
        "1.1", "1.1", "Africa/Cairo", "25200",
        current, listOf(), listOf(), listOf()
    )


    override suspend fun getCurrentWeatherOnline(
        lat: String,
        lon: String,
        lang: String,
        units: String
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun insertCurrentWeather(weatherResponse: CurrentResponse) {
        currentResponse = weatherResponse
    }

    override suspend fun getCurrentWeatherOffline(): Flow<CurrentResponse?> {
        return flowOf(currentResponse)
    }

    override suspend fun insertFavLocation(address: FavAddress) {
        favList.add(address)
    }

    override suspend fun deleteFavLocation(address: FavAddress) {
        favList.remove(address)
    }

    override suspend fun getFavLocations(): Flow<List<FavAddress>> {
        return flowOf(favList)
    }

    override suspend fun getAlerts(): Flow<List<AlertSchedule>> {
        return flowOf(alertList)
    }

    override suspend fun insertAlert(alert: AlertSchedule) {
        alertList.add(alert)
    }

    override suspend fun deleteAlert(id: String) {
        val alert: AlertSchedule = alertList?.first { it.id == id }!!
        alertList?.remove(alert)
    }
}