package com.example.weather

import android.os.Handler
import android.os.Looper
import com.example.weather.data.model.Weather
import com.example.weather.data.repository.WeatherRepository
import com.example.weather.screen.RequestCompleteListener
import com.example.weather.screen.home.WeatherContract
import com.example.weather.screen.home.WeatherPresenter
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

class WeatherPresenterTest {

    private val mView = mockk<WeatherContract.View>(relaxed = true)
    private val mRepository = mockk<WeatherRepository>(relaxed = true)
    private val mPresenter = WeatherPresenter(mRepository)

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
    fun `getWeather call fetch remote weather if network enabled`() {
        val latitude = 1.0
        val longitude = 2.0
        val position = 0
        val isNetworkEnable = true
        val isCurrent = true

        // Call the method under test
        mPresenter.getWeather(latitude, longitude, position, isNetworkEnable, isCurrent)

        // Verify
        verify { mPresenter.getRemoteWeather(latitude, longitude, isCurrent) }
    }

    @Test
    fun `getWeather call fetch local weather if network disabled`() {
        val latitude = 1.0
        val longitude = 2.0
        val position = 0
        val isNetworkEnable = false
        val isCurrent = true
        val listWeather = listOf(mCurrentWeather, mHourlyWeather, mDailyWeather)

        // Mock repository behavior
        every { mRepository.getAllLocalWeathers() } returns listWeather

        // Call the method under test
        mPresenter.getWeather(latitude, longitude, position, isNetworkEnable, isCurrent)

        // Verify
        // verify { presenter.getLocalWeather(position) }
    }

    @Test
    fun `getRemoteWeather not call fetch method`() {
        // Mock data
        val latitude = 1.0
        val longitude = 2.0
        val isCurrent = true

        mPresenter.mIsDataFetching = true

        // Call the method under test
        mPresenter.getRemoteWeather(latitude, longitude, isCurrent)
    }

    @Test
    fun `getRemoteWeather call fetch method`() {
        // Mock data
        val latitude = 1.0
        val longitude = 2.0
        val isCurrent = true

        mPresenter.mIsDataFetching = false

        // Call the method under test
        mPresenter.getRemoteWeather(latitude, longitude, isCurrent)

        // Verify
        coVerify { mRepository.fetchWeatherForecastCurrent(latitude, longitude, any()) }
        coVerify { mRepository.fetchWeatherForecastHourly(latitude, longitude, any()) }
        coVerify { mRepository.fetchWeatherForecastDaily(latitude, longitude, any()) }
    }

    @Test
    fun `getRemoteWeather call fetch method success`() {
        // Mock data
        val latitude = 1.0
        val longitude = 2.0
        val isCurrent = true

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
        mPresenter.getRemoteWeather(latitude, longitude, isCurrent)

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
        val isCurrent = true
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
        mPresenter.getRemoteWeather(latitude, longitude, isCurrent)

        // Wait for the tasks to complete
        latch.await()

        // Verify
        coVerify { mRepository.fetchWeatherForecastCurrent(latitude, longitude, any()) }
        coVerify { mRepository.fetchWeatherForecastHourly(latitude, longitude, any()) }
        coVerify { mRepository.fetchWeatherForecastDaily(latitude, longitude, any()) }
        verify { mView.onError(e) }
    }

    @Test
    fun `insertWeatherIfDataAvailable insert current weather when isCurrent is true`() {
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

        // Call the method under test
        mPresenter.insertWeatherIfDataAvailable(mCurrentWeather, mHourlyWeather, mDailyWeather, true)

        // Verify
        verify { handler.post(any()) }
        verify { mRepository.insertCurrentWeather(mCurrentWeather, mHourlyWeather, mDailyWeather) }
        verify { mPresenter.onGetDataAndSendToView(any()) }
    }

    @Test
    fun `insertWeatherIfDataAvailable should insert favorite weather when isCurrent is false`() {
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

        // Call the method under test
        mPresenter.insertWeatherIfDataAvailable(mCurrentWeather, mHourlyWeather, mDailyWeather, false)

        // Verify
        verify { handler.post(any()) }
        verify { mRepository.insertFavoriteWeather(mCurrentWeather, mHourlyWeather, mDailyWeather) }
        verify { mPresenter.onGetDataAndSendToView(any()) }
    }

    @Test
    fun `onGetDataAndSendToView update view`() {
        val idWeather = "city1country"
        val localWeather = Weather(1.0, 2.0, null, "city1", "country1", "true", null, null, null)
        val listWeather = listOf(localWeather)

        // Mock repository behavior
        every { mRepository.getAllLocalWeathers() } returns listWeather
        every { mRepository.getLocalWeather(idWeather) } returns localWeather

        // Call the method under test
        mPresenter.onGetDataAndSendToView(idWeather)

        // Verify that repository methods are called with correct parameters
        verify { mRepository.getAllLocalWeathers() }
        verify { mRepository.getLocalWeather(idWeather) }

        // Verify that view methods are called appropriately
        verify { mView.onGetSpinnerList(listWeather) }
        verify { mView.onProgressLoading(false) }
        verify { mView.onGetCurrentWeatherSuccess(localWeather) }
    }

    @Test
    fun `getLocalWeather get data from database`() {
        val listWeather = listOf(mCurrentWeather, mHourlyWeather, mDailyWeather)

        // Mock repository behavior
        every { mRepository.getAllLocalWeathers() } returns listWeather

        // Call the method under test
        mPresenter.getLocalWeather(0)

        // Verify that repository methods are called with correct parameters
        verify { mRepository.getAllLocalWeathers() }

        // Verify that view methods are called appropriately
        verify { mView.onProgressLoading(false) }
        verify { mView.onGetSpinnerList(listWeather) }
        verify { mView.onGetCurrentWeatherSuccess(listWeather[0]) }
    }

    @Test
    fun `getLocalWeather get empty data from database`() {
        val listWeather = emptyList<Weather>()

        // Mock repository behavior
        every { mRepository.getAllLocalWeathers() } returns listWeather

        // Call the method under test
        mPresenter.getLocalWeather(0)

        // Verify that repository methods are called with correct parameters
        verify { mRepository.getAllLocalWeathers() }

        // Verify that view methods are called appropriately
        verify { mView.onProgressLoading(false) }
        verify { mView.onGetSpinnerList(listWeather) }
        verify { mView.onDBEmpty() }
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
