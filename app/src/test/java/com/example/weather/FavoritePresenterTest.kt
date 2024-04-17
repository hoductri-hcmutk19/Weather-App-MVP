package com.example.weather

import com.example.weather.data.model.Weather
import com.example.weather.data.repository.WeatherRepository
import com.example.weather.screen.favorite.FavoriteContract
import com.example.weather.screen.favorite.FavoritePresenter
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test

class FavoritePresenterTest {

    private val mView = mockk<FavoriteContract.View>(relaxed = true)
    private val mRepository = mockk<WeatherRepository>()
    private val mPresenter = FavoritePresenter(mRepository)

    private val mFavoriteWeather1 = Weather(10.0, 10.0, null, "city1", "country1", "true", null, null, null)
    private val mFavoriteWeather2 = Weather(11.0, 11.0, null, "city2", "country2", "true", null, null, null)
    private val mNonFavoriteWeather = Weather(12.0, 12.0, null, "city3", "country3", "false", null, null, null)

    @Before
    fun setup() {
        mPresenter.setView(mView)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `getAllFavorite return only favorite weathers if exist current weather`() {
        val allWeathers = listOf(mFavoriteWeather1, mFavoriteWeather2, mNonFavoriteWeather)

        // Mock repository behavior
        every { mRepository.getAllLocalWeathers() } returns allWeathers

        // Call the method under test
        mPresenter.getAllFavorite()

        // Verify that only favorite weathers are passed to the view
        verify { mView.onGetWeatherListSuccess(listOf(mFavoriteWeather1, mFavoriteWeather2)) }
    }

    @Test
    fun `getAllFavorite return only favorite weathers if not exist current weather`() {
        // Mock data
        val allWeathers = listOf(mFavoriteWeather1, mFavoriteWeather2)

        // Mock repository behavior
        every { mRepository.getAllLocalWeathers() } returns allWeathers

        // Call the method under test
        mPresenter.getAllFavorite()

        // Verify that only favorite weathers are passed to the view
        verify { mView.onGetWeatherListSuccess(listOf(mFavoriteWeather1, mFavoriteWeather2)) }
    }

    @Test
    fun `removeFavoriteWeather delete the weather with the given id`() {
        // Mock data
        val weatherId = "city1country1"

        // Mock repository behavior
        every { mRepository.deleteWeather(weatherId) } returns Unit

        // Call the method under test
        mPresenter.removeFavoriteWeather(weatherId)

        // Verify that the deleteWeather method of the repository is called with the correct id
        verify { mRepository.deleteWeather(weatherId) }
    }

    @Test
    fun `onStart should perform necessary initialization`() {
        // Call the method under test
        mPresenter.onStart()
    }

    @Test
    fun `onStop should perform necessary cleanup`() {
        // Call the method under test
        mPresenter.onStop()
    }
}
