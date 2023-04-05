package com.fadalyis.weatherforecastapplication.favorite.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.android.architecture.blueprints.todoapp.MainRule
import com.fadalyis.weatherforecastapplication.alert.viewmodel.AlertViewModel
import com.fadalyis.weatherforecastapplication.model.FakeRepository
import com.fadalyis.weatherforecastapplication.model.pojo.FavAddress
import com.fadalyis.weatherforecastapplication.network.FavApiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class FavoriteViewModelTest {

    private val favAddress1 = FavAddress(30.3030, 33.3333, "Cairo", 1)
    private val favAddress2 = FavAddress(32.3030, 32.3333, "Alex", 2)

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val mainRule = MainRule()

    lateinit var viewModel: FavoriteViewModel
    lateinit var repo: FakeRepository

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        repo = FakeRepository()
        viewModel = FavoriteViewModel(repo)
    }


    @Test
    fun getLocation_returnEmptyList() {
        val value = viewModel.location.value
        value as FavApiState.Success
        assertThat(value.data, `is` (mutableListOf()))
    }

    @Test
    fun saveLocation_2address_returnSizeTwo() = mainRule.runBlockingTest{
        viewModel.saveLocation(favAddress1)
        viewModel.saveLocation(favAddress2)
        val value = viewModel.location.value
        value as FavApiState.Success
        assertThat(value.data.size, `is`(2))
    }

    @Test
    fun deleteLocation_save2addressDelete1Address_returnSize1() = mainRule.runBlockingTest{
        viewModel.saveLocation(favAddress1)
        viewModel.saveLocation(favAddress2)
        viewModel.deleteLocation(favAddress1)
        val value = viewModel.location.value
        value as FavApiState.Success
        assertThat(value.data.size, `is`(1))
    }
}