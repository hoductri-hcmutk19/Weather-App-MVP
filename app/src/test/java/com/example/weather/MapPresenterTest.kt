package com.example.weather

import android.os.Handler
import android.os.Looper
import com.example.weather.data.model.Weather
import com.example.weather.data.repository.WeatherRepository
import com.example.weather.screen.RequestCompleteListener
import com.example.weather.screen.map.MapContract
import com.example.weather.screen.map.MapPresenter
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch

class MapPresenterTest {
    private val mView = mockk<MapContract.View>(relaxed = true)
    private val mRepository = mockk<WeatherRepository>(relaxed = true)
    private val mPresenter = MapPresenter(mRepository)

    private val mCurrentWeather = Weather(1.0, 2.0, null, "city1", "country1", "false", null, null, null)
    private val mHourlyWeather = Weather(1.0, 2.0, null, "city2", "country2", "false", null, null, null)
    private val mDailyWeather = Weather(1.0, 2.0, null, "city3", "country3", "false", null, null, null)

    @Before
    fun setup() {
        mPresenter.setView(mView)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `favoriteWeather insert a favorite weather`() {
        // Mock data
        val favoriteWeather = Weather(1.0, 2.0, null, "city1", "country1", "true", null, null, null)

        // Mock repository behavior
        every { mRepository.insertFavoriteWeather(favoriteWeather) } returns Unit

        // Call the method under test
        mPresenter.favoriteWeather(favoriteWeather)

        // Verify
        verify { mRepository.insertFavoriteWeather(favoriteWeather) }
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
    fun `getWeatherRemote not call fetch method`() {
        // Mock data
        val latitude = 1.0
        val longitude = 2.0

        mPresenter.mIsDataFetching = true

        // Call the method under test
        mPresenter.getWeatherRemote(latitude, longitude)
    }

    @Test
    fun `getWeatherRemote call fetch method`() {
        // Mock data
        val latitude = 1.0
        val longitude = 2.0

        mPresenter.mIsDataFetching = false

        // Create a CountDownLatch with size 3
        val latch = CountDownLatch(3)

        // Mock repository behavior
        coEvery { mRepository.fetchWeatherForecastCurrent(any(), any(), any()) } coAnswers {
            // Mark that the task is complete
            latch.countDown()
        }
        coEvery { mRepository.fetchWeatherForecastHourly(any(), any(), any()) } coAnswers {
            // Mark that the task is complete
            latch.countDown()
        }
        coEvery { mRepository.fetchWeatherForecastDaily(any(), any(), any()) } coAnswers {
            // Mark that the task is complete
            latch.countDown()
        }

        // Call the method under test
        mPresenter.getWeatherRemote(latitude, longitude)

        // Wait for the tasks to complete
        latch.await()

        // Verify
        coVerify { mRepository.fetchWeatherForecastCurrent(latitude, longitude, any()) }
        coVerify { mRepository.fetchWeatherForecastHourly(latitude, longitude, any()) }
        coVerify { mRepository.fetchWeatherForecastDaily(latitude, longitude, any()) }
    }

    @Test
    fun `getWeatherRemote call fetch method success`() {
        // Mock data
        val latitude = 1.0
        val longitude = 2.0

        mPresenter.mIsDataFetching = false

        // Create a CountDownLatch with size 3
        val latch = CountDownLatch(3)

        // Mock repository behavior
        coEvery { mRepository.fetchWeatherForecastCurrent(any(), any(), any()) } coAnswers {
            val responseCallback = args[2] as RequestCompleteListener<Weather>
            responseCallback.onRequestSuccess(mCurrentWeather)
            // Mark that the task is complete
            latch.countDown()
        }
        coEvery { mRepository.fetchWeatherForecastHourly(any(), any(), any()) } coAnswers {
            val responseCallback = args[2] as RequestCompleteListener<Weather>
            responseCallback.onRequestSuccess(mHourlyWeather)
            // Mark that the task is complete
            latch.countDown()
        }
        coEvery { mRepository.fetchWeatherForecastDaily(any(), any(), any()) } coAnswers {
            val responseCallback = args[2] as RequestCompleteListener<Weather>
            responseCallback.onRequestSuccess(mDailyWeather)
            // Mark that the task is complete
            latch.countDown()
        }

        // Call the method under test
        mPresenter.getWeatherRemote(latitude, longitude)

        // Wait for the tasks to complete
        latch.await()

        // Verify
        coVerify { mRepository.fetchWeatherForecastCurrent(latitude, longitude, any()) }
        coVerify { mRepository.fetchWeatherForecastHourly(latitude, longitude, any()) }
        coVerify { mRepository.fetchWeatherForecastDaily(latitude, longitude, any()) }
    }

    @Test
    fun `getRemoteWeather call fetch method failed`() {
        // Mock data
        val latitude = 1.0
        val longitude = 2.0
        val e: Exception = Exception("This is an example exception")

        mPresenter.mIsDataFetching = false

        // Create a CountDownLatch with size 3
        val latch = CountDownLatch(3)

        // Mock repository behavior
        coEvery { mRepository.fetchWeatherForecastCurrent(any(), any(), any()) } coAnswers {
            val responseCallback = args[2] as RequestCompleteListener<Weather>
            responseCallback.onRequestFailed(e)
            // Mark that the task is complete
            latch.countDown()
        }
        coEvery { mRepository.fetchWeatherForecastHourly(any(), any(), any()) } coAnswers {
            val responseCallback = args[2] as RequestCompleteListener<Weather>
            responseCallback.onRequestFailed(e)
            // Mark that the task is complete
            latch.countDown()
        }
        coEvery { mRepository.fetchWeatherForecastDaily(any(), any(), any()) } coAnswers {
            val responseCallback = args[2] as RequestCompleteListener<Weather>
            responseCallback.onRequestFailed(e)
            // Mark that the task is complete
            latch.countDown()
        }

        // Call the method under test
        mPresenter.getWeatherRemote(latitude, longitude)

        // Wait for the tasks to complete
        latch.await()

        // Verify
        coVerify { mRepository.fetchWeatherForecastCurrent(latitude, longitude, any()) }
        coVerify { mRepository.fetchWeatherForecastHourly(latitude, longitude, any()) }
        coVerify { mRepository.fetchWeatherForecastDaily(latitude, longitude, any()) }
        verify { mView.onError(e) }
    }

    @Test
    fun `insertWeatherIfDataAvailable insert weather when isFavorite is true and getLocalWeather return not null`() {
        // Mock Handler, Looper
        mockkStatic(Looper::class)
        val looper = mockk<Looper> {
            every {
                thread
            } returns Thread.currentThread()
        }

        every {
            Looper.getMainLooper()
        } returns looper

        val handler = mockk<Handler>(relaxed = true)

        every { handler.post(any()) } answers { true }

        // Mock weather
        val favoriteWeather = Weather(1.0, 2.0, null, "city1", "country1", "true", null, null, null)
        val idWeather = "city1country1"

        // Mock repository behavior
        every { mRepository.getLocalWeather(idWeather) } returns favoriteWeather
        every { mRepository.insertCurrentWeather(mCurrentWeather, mHourlyWeather, mDailyWeather) } returns Unit
        every { mRepository.insertFavoriteWeather(mCurrentWeather, mHourlyWeather, mDailyWeather) } returns Unit

        // Call the method under test
        mPresenter.insertWeatherIfDataAvailable(mCurrentWeather, mHourlyWeather, mDailyWeather)

        // Verify
        verify { handler.post(any()) }
        verify { mRepository.getLocalWeather(idWeather) }
        verify { mRepository.insertFavoriteWeather(mCurrentWeather, mHourlyWeather, mDailyWeather) }
    }

    @Test
    fun `insertWeatherIfDataAvailable insert weather when isFavorite is false and getLocalWeather return not null`() {
        // Mock Handler, Looper
        mockkStatic(Looper::class)
        val looper = mockk<Looper> {
            every {
                thread
            } returns Thread.currentThread()
        }

        every {
            Looper.getMainLooper()
        } returns looper

        val handler = mockk<Handler>(relaxed = true)

        every { handler.post(any()) } answers { true }

        // Mock weather
        val nonFavoriteWeather = Weather(1.0, 2.0, null, "city1", "country1", "false", null, null, null)
        val idWeather = "city1country1"

        // Mock repository behavior
        every { mRepository.getLocalWeather(idWeather) } returns nonFavoriteWeather
        every { mRepository.insertCurrentWeather(mCurrentWeather, mHourlyWeather, mDailyWeather) } returns Unit
        every { mRepository.insertFavoriteWeather(mCurrentWeather, mHourlyWeather, mDailyWeather) } returns Unit

        // Call the method under test
        mPresenter.insertWeatherIfDataAvailable(mCurrentWeather, mHourlyWeather, mDailyWeather)

        // Verify
        verify { handler.post(any()) }
        verify { mRepository.getLocalWeather(idWeather) }
        verify { mRepository.insertCurrentWeather(mCurrentWeather, mHourlyWeather, mDailyWeather) }
    }

    @Test
    fun `insertWeatherIfDataAvailable not insert weather when getLocalWeather return null`() {
        // Mock Handler, Looper
        mockkStatic(Looper::class)
        val looper = mockk<Looper> {
            every {
                thread
            } returns Thread.currentThread()
        }

        every {
            Looper.getMainLooper()
        } returns looper

        val handler = mockk<Handler>(relaxed = true)

        every { handler.post(any()) } answers { true }

        // Mock weather
        val idWeather = "city1country1"

        // Mock repository behavior
        every { mRepository.getLocalWeather(idWeather) } returns null
        every { mRepository.insertCurrentWeather(mCurrentWeather, mHourlyWeather, mDailyWeather) } returns Unit
        every { mRepository.insertFavoriteWeather(mCurrentWeather, mHourlyWeather, mDailyWeather) } returns Unit

        // Call the method under test
        mPresenter.insertWeatherIfDataAvailable(mCurrentWeather, mHourlyWeather, mDailyWeather)

        // Verify
        verify { handler.post(any()) }
        verify { mPresenter.sendToView(mCurrentWeather, mHourlyWeather, mDailyWeather) }
    }

    @Test
    fun `sendToView update view`() {
        // Call the method under test
        mPresenter.sendToView(mCurrentWeather, mHourlyWeather, mDailyWeather)

        // Verify that view methods are called appropriately
        verify { mView.onProgressLoading(false) }
        verify { mView.onGetCurrentWeatherSuccess(mCurrentWeather) }
    }

    @Test
    fun `checkWeatherLocal return true if weather exist`() {
        // Mock weather
        val weather = Weather(1.0, 2.0, null, "city1", "country1", "true", null, null, null)
        val idWeather = "city1country1"

        // Mock repository behavior
        every { mRepository.getLocalWeather(idWeather) } returns weather

        // Call the method under test
        mPresenter.checkWeatherLocal(idWeather)

        // Verify that view methods are called appropriately
        verify { mView.onGetWeatherLocalSuccess(true) }
    }

    @Test
    fun `checkWeatherLocal return false if weather not exist`() {
        // Mock weather
        val idWeather = "city1country1"

        // Mock repository behavior
        every { mRepository.getLocalWeather(idWeather) } returns null

        // Call the method under test
        mPresenter.checkWeatherLocal(idWeather)

        // Verify that view methods are called appropriately
        verify { mView.onGetWeatherLocalSuccess(false) }
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
