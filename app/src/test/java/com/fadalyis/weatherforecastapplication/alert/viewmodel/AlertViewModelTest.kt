package com.fadalyis.weatherforecastapplication.alert.viewmodel

import android.util.Log
import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.android.architecture.blueprints.todoapp.MainRule
import com.fadalyis.weatherforecastapplication.model.FakeRepository
import com.fadalyis.weatherforecastapplication.model.pojo.AlertSchedule
import com.fadalyis.weatherforecastapplication.network.AlertApiState
import com.fadalyis.weatherforecastapplication.network.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.withContext
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.*

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class AlertViewModelTest {

    private val alert: AlertSchedule =
        AlertSchedule("UUID-1", 100_100_100L, 200_200_200L, "Notification")

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val mainRule = MainRule()

    lateinit var viewModel: AlertViewModel
    lateinit var repo: FakeRepository

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        repo = FakeRepository()
        viewModel = AlertViewModel(repo)
    }


    @Test
    fun saveAlert_alert_returnSizeOne() = mainRule.runBlockingTest {
        viewModel.saveAlert(alert)
        val value = viewModel.alert.value
        value as AlertApiState.Success
        assertThat(value.data.size, `is`(1))
    }

    @Test
    fun deleteAlert_alert_returnSizeZero()  = mainRule.runBlockingTest {
        viewModel.saveAlert(alert)
        viewModel.deleteAlert(alert.id)

        repo.getAlerts().collect{
            assertEquals(it.size, 0)
        }
    }
}