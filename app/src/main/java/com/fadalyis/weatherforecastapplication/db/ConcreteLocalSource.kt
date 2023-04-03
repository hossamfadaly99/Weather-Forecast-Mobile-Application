package com.fadalyis.weatherforecastapplication.db


import android.content.Context
import android.location.Address
import com.fadalyis.weatherforecastapplication.model.pojo.AlertSchedule
import com.fadalyis.weatherforecastapplication.model.pojo.CurrentResponse
import com.fadalyis.weatherforecastapplication.model.pojo.FavAddress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.*

class ConcreteLocalSource(context: Context) : LocalSource {

    private val weatherDao: WeatherDAO by lazy {
        val db = AppDataBase.getInstance(context)
        db.getWeatherDao()
    }
    private val favoriteDao: FavoriteDAO by lazy {
        val db = AppDataBase.getInstance(context)
        db.getFavoriteDao()
    }
    private val alertDao: AlertDAO by lazy {
        val db = AppDataBase.getInstance(context)
        db.getAlertDao()
    }

    override suspend fun insertCurrentWeather(currentWeather: CurrentResponse) {
        weatherDao.deletePreviousWeather()
        weatherDao.insertCurrentWeather(currentWeather)
    }

    override suspend fun getCurrentWeather(): Flow<CurrentResponse?> {
        return weatherDao.getCurrentWeather()
    }

    override suspend fun insertFavLocation(location: FavAddress) {
        favoriteDao.insertLocation(location)
    }

    override suspend fun deleteFavLocation(location: FavAddress) {
        favoriteDao.deleteLocation(location)
    }

    override suspend fun getFavLocations(): Flow<List<FavAddress>> {
        return  favoriteDao.getFavLocations()
    }

    override suspend fun insertAlert(alert: AlertSchedule) {
        alertDao.insertAlert(alert)
    }

    override suspend fun deleteAlert(id: String) {
        alertDao.deleteAlert(id)
    }

    override suspend fun getFAlerts(): Flow<List<AlertSchedule>> {
        return alertDao.getAlerts()
    }


}