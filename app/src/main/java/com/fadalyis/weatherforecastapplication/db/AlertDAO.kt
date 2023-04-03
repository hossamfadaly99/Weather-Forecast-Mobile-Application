package com.fadalyis.weatherforecastapplication.db

import androidx.room.*
import com.fadalyis.weatherforecastapplication.model.pojo.AlertSchedule
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDAO {

    @Query("select * from AlertTable")
    fun getAlerts(): Flow<List<AlertSchedule>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAlert(alert: AlertSchedule)

    @Query("delete from AlertTable where id =:id")
    fun deleteAlert(id: String)

}