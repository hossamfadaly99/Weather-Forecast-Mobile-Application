package com.fadalyis.weatherforecastapplication.utils


import androidx.room.TypeConverter
import com.fadalyis.weatherforecastapplication.model.pojo.Alert
import com.fadalyis.weatherforecastapplication.model.pojo.Hourly
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class AlertConverters {
    @TypeConverter
    fun fromStringToListOfAlert(value: String?): List<Alert>? {
        val listType: Type = object : TypeToken<List<Alert>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromListOfAlertToString(list: List<Alert>?): String? {
        val gson = Gson()
        return gson.toJson(list)
    }

}