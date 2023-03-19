package com.fadalyis.weatherforecastapplication.model

import android.location.Address
import com.fadalyis.weatherforecastapplication.db.LocalSource
import com.fadalyis.weatherforecastapplication.model.pojo.CurrentResponse
import com.fadalyis.weatherforecastapplication.network.RemoteSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf


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
        apiKey: String
    ): Flow<CurrentResponse> {
        return flowOf( remoteSource.getCurrentWeatherOnline(lat, lon, apiKey))
    }


    override suspend fun insertCurrentWeather(weatherResponse: CurrentResponse) {
        localSource.insertCurrentWeather(weatherResponse)
    }

    override suspend fun getCurrentWeatherOffline(): Flow<CurrentResponse?> {
        return localSource.getCurrentWeather()
    }

    override suspend fun insertFavLocation(address: Address) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFavLocation(address: Address) {
        TODO("Not yet implemented")
    }

    override suspend fun getFavLocations(): List<Address> {
        TODO("Not yet implemented")
    }
}