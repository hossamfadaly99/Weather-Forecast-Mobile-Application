package com.fadalyis.weatherforecastapplication.model

import com.example.android.architecture.blueprints.todoapp.MainRule
import com.fadalyis.weatherforecastapplication.model.pojo.AlertSchedule
import com.fadalyis.weatherforecastapplication.model.pojo.Current
import com.fadalyis.weatherforecastapplication.model.pojo.CurrentResponse
import com.fadalyis.weatherforecastapplication.model.pojo.FavAddress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class RepositoryTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainRule= MainRule()


    private val current= Current(
        1, 1.2, "1", 2.1, 1, 1, "1", "1",
        2.2, "2.3", 1, listOf(), 1, 2.4, 2.5
    )

    private val weather: CurrentResponse = CurrentResponse(
        "1.1", "1.1", "Africa/Cairo", "25200",
        current, listOf(), listOf(), listOf()
    )

    private val favLocation: FavAddress = FavAddress(66.66, 60.541, "Paris", 1)
    private val favLocation2: FavAddress = FavAddress(31.121, 30.5651, "Cairo", 2)
    private val favLocation3: FavAddress = FavAddress(80.121, 80.5651, "London", 3)
    private val alert: AlertSchedule = AlertSchedule("UUID-1", 100_100_100L, 200_200_200L, "Notification")
    private val alert2: AlertSchedule = AlertSchedule("UUID-2", 100_100_100L, 200_200_200L, "Alarm")
    private val alert3: AlertSchedule = AlertSchedule("UUID-3", 100_100_100L, 200_200_200L, "Notification")
    private val alert4: AlertSchedule = AlertSchedule("UUID-4", 100_100_100L, 200_200_200L, "Notification")

    lateinit var fakeLocalDataSource: FakeLocalSource
    lateinit var fakeRemoteDataSource: FakeRemoteSource
    lateinit var repository: Repository
    lateinit var favList : MutableList<FavAddress>
    lateinit var alertList : MutableList<AlertSchedule>


    @Before
    fun setUp() {

        favList = mutableListOf(favLocation, favLocation2)
        alertList = mutableListOf(alert, alert2, alert3)

        fakeLocalDataSource = FakeLocalSource(weather, favList, alertList)
        fakeRemoteDataSource = FakeRemoteSource(weather)

        repository = Repository.getInstance(
            fakeRemoteDataSource,
            fakeLocalDataSource,
            Dispatchers.Main
        )
    }


    @After
    fun tearDown() {
        favList = mutableListOf()
        alertList = mutableListOf()
    }
    @Test
    fun getAllAlerts() = mainRule.runBlockingTest {
        repository.getAlerts().collectLatest{
            assertEquals(it.size, (alertList.size))
        }

    }
    @Test
    fun insertCurrentWeather_weatherResponse_returnTheSameWeatherResponse() = mainRule.runBlockingTest {
        repository.insertCurrentWeather(weather)
        launch {
            repository.getCurrentWeatherOffline().collect {
                assertThat(it, `is`(weather))
            }
        }
    }

    @Test
    fun getCurrentWeatherDB_weatherResponse_returnTheSameWeatherResponse() = mainRule.runBlockingTest {
        repository.insertCurrentWeather(weather)
        launch {
            repository.getCurrentWeatherOffline().collect {
                assertThat(it, `is`(weather))
            }
        }
    }

    @Test
    fun insertFavLocation_2address_returnSize2() = mainRule.runBlockingTest {
        var size = 0
        repository.getFavLocations().collect {
            size = it.size

        }
        repository.insertFavLocation(favLocation)
        repository.insertFavLocation(favLocation)


        repository.getFavLocations().collect {
            assertThat(it.size, `is`(size + 2))

        }
    }

    @Test
    fun getFavLocations_returnTrue() = mainRule.runBlockingTest {

        repository.getFavLocations().collect {
            assertThat(it.size, `is`((favList.size)))
        }

    }

    @Test
    fun deleteFavLocation_address_returnSize() = mainRule.runBlockingTest {
        repository.insertFavLocation(favLocation3)
        var size = 0
        repository.getFavLocations().collect {
            size = it.size
        }
        repository.deleteFavLocation(favLocation)
        repository.getFavLocations().collect {
            assertThat(it.size, `is`(size - 1))
        }
    }

    @Test
    fun deleteAlert_returnTrue() = mainRule.runBlockingTest {
        var size = 0
        repository.getAlerts().collect {
            size = it.size
        }
        repository.deleteAlert(alert.id)

        repository.getAlerts().collect {
            assertThat(it.size, `is`(size - 1))
        }
    }

    @Test
    fun insertAlert_returnTrue() = mainRule.runBlockingTest {
        var size = 0
        repository.getAlerts().collect {
            size = it.size
        }
        repository.insertAlert(alert3)

        repository.getAlerts().collect {
            assertThat(it.size, `is`(size + 1))
        }
    }




}