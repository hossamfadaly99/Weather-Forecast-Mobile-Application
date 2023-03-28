package com.fadalyis.weatherforecastapplication.network

import android.util.Log
import com.fadalyis.weatherforecastapplication.model.pojo.CurrentResponse
import retrofit2.Response

class CurrentWeatherClient private constructor() : RemoteSource {
    private val currentWeatherService: CurrentWeatherService by lazy {
        RetrofitHelper.getInstance().create(CurrentWeatherService::class.java)
    }
    lateinit var response: Response<CurrentResponse>
    override suspend fun getCurrentWeatherOnline(
        lat: String,
        lon: String,
        lang: String,
        units: String
    ): CurrentResponse {
        Log.i("menenenenmenenne", "getCurrentWeatherOnline: $lang")
        response = currentWeatherService.getCurrentWeather(lat, lon, lang, units)
        Log.i("iecrhje", "networkClient: ${response.body()?.lat}")
        //TODO remove null assertion
        return response.body()!!
    }

    companion object {
        private var instance: CurrentWeatherClient? = null
        fun getInstance(): CurrentWeatherClient {
            return instance ?: synchronized(this) {
                val temp = CurrentWeatherClient()
                instance = temp
                temp
            }
        }
    }


}