package com.fadalyis.weatherforecastapplication.db


import android.location.Address
import androidx.room.*
import com.fadalyis.weatherforecastapplication.model.pojo.CurrentResponse
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDAO {
//    @Query("select * from WeatherTable")
//    fun getFavLocations(): Flow<List<Address>>
//
//    @Insert(onConflict = OnConflictStrategy.IGNORE)
//    suspend fun insertLocation(address: Address)
//
//    @Delete
//    suspend fun deleteLocation(address: Address)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCurrentWeather(currentWeather: CurrentResponse)

    @Query("SELECT * FROM WeatherTable LIMIT 1")
    fun getCurrentWeather(): Flow<CurrentResponse?>

    @Query("DELETE FROM WeatherTable")
    suspend fun deletePreviousWeather()

}