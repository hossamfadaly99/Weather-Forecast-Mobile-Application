package com.fadalyis.weatherforecastapplication.db

import android.location.Address
import com.fadalyis.weatherforecastapplication.model.pojo.CurrentResponse
import com.fadalyis.weatherforecastapplication.model.pojo.LocationData
import kotlinx.coroutines.flow.Flow

interface LocalSource {
    suspend fun insertCurrentWeather(currentWeather: CurrentResponse)
    suspend fun getCurrentWeather(): Flow<CurrentResponse?>
    suspend fun insertFavLocation(address: Address)
    suspend fun deleteFavLocation(address: Address)
    //suspend fun getFavLocations(): Flow<List<Address>>

}