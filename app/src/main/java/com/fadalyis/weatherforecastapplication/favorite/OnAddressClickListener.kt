package com.fadalyis.weatherforecastapplication.favorite

import com.fadalyis.weatherforecastapplication.model.pojo.FavAddress

interface OnAddressClickListener {
    fun deleteAddress(address: FavAddress)
}