package com.fadalyis.weatherforecastapplication.model

import android.location.Address
import android.util.Log
import com.fadalyis.weatherforecastapplication.db.LocalSource
import com.fadalyis.weatherforecastapplication.model.pojo.AlertSchedule
import com.fadalyis.weatherforecastapplication.model.pojo.CurrentResponse
import com.fadalyis.weatherforecastapplication.model.pojo.FavAddress
import com.fadalyis.weatherforecastapplication.network.RemoteSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.*


class Repository private constructor(
    var remoteSource: RemoteSource,
    var localSource: LocalSource
) : RepositoryInterface {
    companion object {
        private var instance: Repository? = null
        fun getInstance(remoteSource: RemoteSource, localSource: LocalSource): Repository {
            return instance ?: synchronized(this) {
                val temp = Repository(
                    remoteSource, localSource
                )
                instance = temp
                temp
            }
        }
    }

    override suspend fun getCurrentWeatherOnline(
        lat: String,
        lon: String,
        lang: String,
        units: String
    ){
        val response = remoteSource.getCurrentWeatherOnline(lat, lon, lang, units)
        Log.i("iecrhje", "getCurrentWeatherOnline: ${response.lat}")
        localSource.insertCurrentWeather(response)
    }


    override suspend fun insertCurrentWeather(weatherResponse: CurrentResponse) {
        localSource.insertCurrentWeather(weatherResponse)
    }

    override suspend fun getCurrentWeatherOffline(): Flow<CurrentResponse?> {
        return localSource.getCurrentWeather()
    }

    override suspend fun insertFavLocation(address: FavAddress) {
        localSource.insertFavLocation(address)
    }

    override suspend fun deleteFavLocation(address: FavAddress) {
        localSource.deleteFavLocation(address)
    }

    override suspend fun getFavLocations(): Flow<List<FavAddress>> {
        return  localSource.getFavLocations()
    }

    override suspend fun getAlerts(): Flow<List<AlertSchedule>> {
        return localSource.getFAlerts()
    }

    override suspend fun insertAlert(alert: AlertSchedule) {
        localSource.insertAlert(alert)
    }

    override suspend fun deleteAlert(id: String) {
        localSource.deleteAlert(id)
    }
}