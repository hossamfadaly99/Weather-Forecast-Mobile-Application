package com.fadalyis.weatherforecastapplication.utils


import androidx.room.TypeConverter
import com.fadalyis.weatherforecastapplication.model.pojo.Current
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class CurrentConverters {
    @TypeConverter
    fun fromStringToArrayList(value: String?): Current {
        val listType: Type = object : TypeToken<Current>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromArrayListToString(list: Current): String? {
        val gson = Gson()
        return gson.toJson(list)
    }

}