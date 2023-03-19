package com.fadalyis.weatherforecastapplication.network

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
        apiKey: String
    ): CurrentResponse {
        response = currentWeatherService.getCurrentWeather(lat, lon, apiKey)
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