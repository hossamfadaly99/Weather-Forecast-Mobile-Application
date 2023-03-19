package com.fadalyis.weatherforecastapplication.utils


import androidx.room.TypeConverter
import com.fadalyis.weatherforecastapplication.model.pojo.Daily
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class DailyConverters {
    @TypeConverter
    fun fromStringToDaily(value: String?): List<Daily> {
        val listType: Type = object : TypeToken<List<Daily>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromDailyToString(list: List<Daily>): String? {
        val gson = Gson()
        return gson.toJson(list)
    }

}