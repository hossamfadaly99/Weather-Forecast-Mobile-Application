package com.fadalyis.weatherforecastapplication.alert

import android.view.View
import com.fadalyis.weatherforecastapplication.model.pojo.FavAddress
import java.util.UUID

interface OnAlertClickListener {
    fun deleteAlert(id: String)

    //fun viewWeatherData(mapLatLon: String)
}