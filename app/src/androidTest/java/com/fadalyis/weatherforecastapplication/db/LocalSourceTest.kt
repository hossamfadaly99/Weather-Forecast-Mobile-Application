package com.fadalyis.weatherforecastapplication.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.fadalyis.weatherforecastapplication.model.pojo.AlertSchedule
import com.fadalyis.weatherforecastapplication.model.pojo.Current
import com.fadalyis.weatherforecastapplication.model.pojo.CurrentResponse
import com.fadalyis.weatherforecastapplication.model.pojo.FavAddress
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class LocalSourceTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val rule = InstantTaskExecutorRule()

    lateinit var database: AppDataBase
    lateinit var localSource: ConcreteLocalSource

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
        localSource = ConcreteLocalSource(database.getWeatherDao(), database.getFavoriteDao(), database.getAlertDao())
    }

    @After
    fun closeDataBase() = database.close()


    @Test
    fun insertCurrentWeather_weatherResponse_returnNotNull()= runBlockingTest {
        launch {
            localSource.getCurrentWeather().collect{
                TestCase.assertNull(it)
                cancel()
            }
        }
        database.getWeatherDao().insertCurrentWeather(weather)

        //When
        launch {
            localSource.getCurrentWeather().collect{
                MatcherAssert.assertThat(it, CoreMatchers.`is`(CoreMatchers.notNullValue()))
                cancel()
            }
        }

    }
    @Test
    fun getCurrentWeather_weatherResponse_returnNotNull()= runBlockingTest {
        localSource.insertCurrentWeather(weather)
        launch {
            localSource.getCurrentWeather().collect {
                MatcherAssert.assertThat(it, CoreMatchers.`is`(CoreMatchers.notNullValue()))
                cancel()
            }
        }
    }
    @Test
    fun deleteCurrentWeather_weatherResponse_returnNullAfterInsertAndDelete()= runBlockingTest {
        localSource.insertCurrentWeather(weather)
        database.getWeatherDao().deletePreviousWeather()
        launch {
            database.getWeatherDao().getCurrentWeather().collect {
                TestCase.assertNull(it)
                cancel()
            }
        }
    }

    @Test
    fun insertFavLocation_favAddress_returnSizeOne()= runBlockingTest {
        launch {
            localSource.getFavLocations().collect{
                MatcherAssert.assertThat(it.size, CoreMatchers.`is`(0) )
                cancel()
            }
        }
        localSource.insertFavLocation(favLocation)

        //When
        launch {
            localSource.getFavLocations().collect{
                MatcherAssert.assertThat(it.size, CoreMatchers.`is`(1) )
                cancel()
            }
        }

    }
    @Test
    fun getFavLocation_favAddress_returnNotNull()= runBlockingTest {
        localSource.insertFavLocation(favLocation)
        launch {
            localSource.getFavLocations().collect {
                MatcherAssert.assertThat(it, CoreMatchers.`is`(CoreMatchers.notNullValue()))
                cancel()
            }
        }
    }
    @Test
    fun deleteFavLocation_favLocation_returnSizeZero()= runBlockingTest {
        localSource.insertFavLocation(favLocation)
        localSource.deleteFavLocation(favLocation)
        launch {
            localSource.getFavLocations().collect {
                MatcherAssert.assertThat(it.size, CoreMatchers.`is`(0) )
                cancel()
            }
        }
    }

    @Test
    fun insertAlert_alert_returnSizeOne()= runBlockingTest {
        launch {
            localSource.getAlerts().collect{
                MatcherAssert.assertThat(it.size, CoreMatchers.`is`(0) )
                cancel()
            }
        }
        localSource.insertAlert(alert)

        //When
        launch {
            localSource.getAlerts().collect{
                MatcherAssert.assertThat(it.size, CoreMatchers.`is`(1) )
                cancel()
            }
        }

    }
    @Test
    fun getAlert_alert_returnNotNullAfterInsert()= runBlockingTest {
        localSource.insertAlert(alert)
        launch {
            localSource.getFavLocations().collect {
                MatcherAssert.assertThat(it, CoreMatchers.`is`(CoreMatchers.notNullValue()))
                cancel()
            }
        }
    }
    @Test
    fun deleteAlert_alert_returnSizeZero()= runBlockingTest {
        localSource.insertAlert(alert)
        localSource.deleteAlert(alert.id)
        launch {
            localSource.getAlerts().collect {
                MatcherAssert.assertThat(it.size, CoreMatchers.`is`(0) )
                cancel()
            }
        }
    }
}