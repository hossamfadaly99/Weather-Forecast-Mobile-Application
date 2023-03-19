package com.fadalyis.weatherforecastapplication.utils


import androidx.room.TypeConverter
import com.fadalyis.weatherforecastapplication.model.pojo.Hourly
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class HourlyConverters {
    @TypeConverter
    fun fromStringToListOfHourly(value: String?): List<Hourly> {
        val listType: Type = object : TypeToken<List<Hourly>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromListOfHourlyToString(list: List<Hourly>): String? {
        val gson = Gson()
        return gson.toJson(list)
    }

}