package com.fadalyis.weatherforecastapplication.db

import androidx.room.*
import com.fadalyis.weatherforecastapplication.model.pojo.FavAddress
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDAO {

    @Query("select * from FavoriteTable")
    fun getFavLocations(): Flow<List<FavAddress>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLocation(address: FavAddress)

    @Delete
    suspend fun deleteLocation(address: FavAddress)

}