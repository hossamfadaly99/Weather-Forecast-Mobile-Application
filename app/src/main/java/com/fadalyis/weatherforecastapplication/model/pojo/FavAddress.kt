package com.fadalyis.weatherforecastapplication.model.pojo

import android.location.Address
import android.os.Parcelable
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "FavoriteTable")
data class FavAddress(
    val lat: Double,
    val lon: Double,
    @PrimaryKey
    val city: String,
    val id: Int
): Parcelable
