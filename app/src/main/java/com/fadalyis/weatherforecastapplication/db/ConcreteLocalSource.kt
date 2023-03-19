package com.fadalyis.weatherforecastapplication.db


import android.content.Context
import android.location.Address
import com.fadalyis.weatherforecastapplication.model.pojo.CurrentResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class ConcreteLocalSource (context: Context): LocalSource{

    private val weatherDao: WeatherDAO by lazy {
        val db = AppDataBase.getInstance(context)
        db.getWeatherDao()
    }

    override suspend fun insertCurrentWeather(currentWeather: CurrentResponse) {
        weatherDao.deletePreviousWeather()
        weatherDao.insertCurrentWeather(currentWeather)
    }

    override suspend fun getCurrentWeather(): Flow<CurrentResponse?> {
        return  weatherDao.getCurrentWeather()
    }

    override suspend fun insertFavLocation(location: Address) {

    }

    override suspend fun deleteFavLocation(location: Address) {

    }

//    override suspend fun getFavLocations(): Flow<List<Address>> {
//
//    }


}