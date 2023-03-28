package com.fadalyis.weatherforecastapplication.network

import com.fadalyis.weatherforecastapplication.model.pojo.CurrentResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CurrentWeatherService {
    @GET("onecall?")
    suspend fun getCurrentWeather(@Query("lat") lat: String, @Query("lon") lon: String, @Query("lang") lang: String, @Query("units") units: String , @Query("appid") API_Key: String = "5105a7173c3805fa7994a304fe55b5ea"):Response<CurrentResponse>
}