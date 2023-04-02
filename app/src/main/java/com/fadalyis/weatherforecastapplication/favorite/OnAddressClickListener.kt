package com.fadalyis.weatherforecastapplication.favorite

import android.view.View
import com.fadalyis.weatherforecastapplication.model.pojo.FavAddress

interface OnAddressClickListener {
    fun deleteAddress(address: FavAddress)
    fun viewWeatherData(mapLatLon: String)
}