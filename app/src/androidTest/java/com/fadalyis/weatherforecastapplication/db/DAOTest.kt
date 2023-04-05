package com.fadalyis.weatherforecastapplication.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.fadalyis.weatherforecastapplication.model.pojo.AlertSchedule
import com.fadalyis.weatherforecastapplication.model.pojo.Current
import com.fadalyis.weatherforecastapplication.model.pojo.CurrentResponse
import com.fadalyis.weatherforecastapplication.model.pojo.FavAddress
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class DAOTest{
    @ExperimentalCoroutinesApi
    @get:Rule
    val rule = InstantTaskExecutorRule()

    lateinit var database: AppDataBase
    private val current= Current(
        1, 1.2, "1", 2.1, 1, 1, "1", "1",
        2.2, "2.3", 1, listOf(), 1, 2.4, 2.5
    )

    private val weather: CurrentResponse = CurrentResponse(
        "1.1", "1.1", "Africa/Cairo", "25200",
        current, listOf(), listOf(), listOf()
    )

    private val favLocation: FavAddress = FavAddress(31.121, 30.5651, "Cairo", 1)
    private val alert: AlertSchedule = AlertSchedule("UUID-55", 100_100_100L, 200_200_200L, "Notification")


    @Before
    fun createDataBase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDataBase::class.java
        ).build()
    }

    @After
    fun closeDataBase() = database.close()


    @Test
    fun insertCurrentWeather_weatherResponse_returnNotNull()= runBlockingTest {
        launch {
            database.getWeatherDao().getCurrentWeather().collect{
                assertNull(it)
                cancel()
            }
        }
        database.getWeatherDao().insertCurrentWeather(weather)

        //When
        launch {
            database.getWeatherDao().getCurrentWeather().collect{
                assertThat(it, `is`(notNullValue()))
                cancel()
            }
        }

    }
    @Test
    fun getCurrentWeather_weatherResponse_returnNotNull()= runBlockingTest {
        database.getWeatherDao().insertCurrentWeather(weather)
        launch {
            database.getWeatherDao().getCurrentWeather().collect {
                assertThat(it, `is`(notNullValue()))
                cancel()
            }
        }
    }
    @Test
    fun deleteCurrentWeather_weatherResponse_returnNullAfterInsertAndDelete()= runBlockingTest {
        database.getWeatherDao().insertCurrentWeather(weather)
        database.getWeatherDao().deletePreviousWeather()
        launch {
            database.getWeatherDao().getCurrentWeather().collect {
                assertNull(it)
                cancel()
            }
        }
    }

    @Test
    fun insertFavLocation_favAddress_returnSizeOne()= runBlockingTest {
        launch {
            database.getFavoriteDao().getFavLocations().collect{
                assertThat(it.size,`is`(0) )
                cancel()
            }
        }
        database.getFavoriteDao().insertLocation(favLocation)

        //When
        launch {
            database.getFavoriteDao().getFavLocations().collect{
                assertThat(it.size,`is`(1) )
                cancel()
            }
        }

    }
    @Test
    fun getFavLocation_favAddress_returnNotNull()= runBlockingTest {
        database.getFavoriteDao().insertLocation(favLocation)
        launch {
            database.getFavoriteDao().getFavLocations().collect {
                assertThat(it, `is`(notNullValue()))
                cancel()
            }
        }
    }
    @Test
    fun deleteFavLocation_favLocation_returnSizeZero()= runBlockingTest {
        database.getFavoriteDao().insertLocation(favLocation)
        database.getFavoriteDao().deleteLocation(favLocation)
        launch {
            database.getFavoriteDao().getFavLocations().collect {
                assertThat(it.size,`is`(0))
                cancel()
            }
        }
    }


    @Test
    fun insertAlert_alert_returnSizeOne()= runBlockingTest {
        launch {
            database.getAlertDao().getAlerts().collect{
                assertThat(it.size,`is`(0) )
                cancel()
            }
        }
        database.getAlertDao().insertAlert(alert)

        //When
        launch {
            database.getAlertDao().getAlerts().collect{
                assertThat(it.size,`is`(1) )
                cancel()
            }
        }

    }
    @Test
    fun getAlert_alert_returnNotNullAfterInsert()= runBlockingTest {
        database.getAlertDao().insertAlert(alert)
        launch {
            database.getFavoriteDao().getFavLocations().collect {
                assertThat(it, `is`(notNullValue()))
                cancel()
            }
        }
    }
    @Test
    fun deleteAlert_alert_returnSizeZero()= runBlockingTest {
        database.getAlertDao().insertAlert(alert)
        database.getAlertDao().deleteAlert(alert.id)
        launch {
            database.getAlertDao().getAlerts().collect {
                assertThat(it.size,`is`(0) )
                cancel()
            }
        }
    }
}