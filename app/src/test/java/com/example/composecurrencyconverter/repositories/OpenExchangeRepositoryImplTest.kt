package com.example.composecurrencyconverter.repositories

import com.example.composecurrencyconverter.data.local.dao.ExchangesRatesDao
import com.example.composecurrencyconverter.data.local.entities.ExchangeRateEntity
import com.example.composecurrencyconverter.data.remote.Result
import com.example.composecurrencyconverter.data.remote.models.OpenExchangeRatesResponse
import com.example.composecurrencyconverter.data.remote.models.toDbModel
import com.example.composecurrencyconverter.data.remote.service.OpenExchangeApiService
import com.example.composecurrencyconverter.utils.Constants
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.anyList
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import retrofit2.Response

@ExperimentalCoroutinesApi
class OpenExchangeRepositoryImplTest {

    private lateinit var apiService: OpenExchangeApiService
    private lateinit var exchangeRateDao: ExchangesRatesDao
    private lateinit var repository: OpenExchangeRepositoryImpl

    @Before
    fun setUp() {
        apiService = mock()
        exchangeRateDao = mock()
        repository = OpenExchangeRepositoryImpl(apiService, exchangeRateDao)
    }

    @Test
    fun `test API call success`(): Unit = runTest {
        // Mock successful API response
        invokeMockSuccessResponse()

        // Call the repository function
        val result = repository.getExchangeRates()

        // Verify the result
        assertTrue(result is Result.Success)
        assertEquals(200, (result as Result.Success).responseCode)
        verify(apiService).getExchangeRates()
        verify(exchangeRateDao, times(1)).insertAll(result.data?.toDbModel()!!)
    }

    @Test
    fun `test API call failure`(): Unit = runTest {
        // Mock failed API response
        val mockResponse = mock<Response<OpenExchangeRatesResponse>>()
        `when`(apiService.getExchangeRates()).thenReturn(mockResponse)
        `when`(mockResponse.isSuccessful).thenReturn(false)
        `when`(mockResponse.message()).thenReturn("Error message")
        `when`(mockResponse.code()).thenReturn(404)

        // Call the repository function
        val result = repository.getExchangeRates()

        // Verify the result
        assertTrue(result is Result.Failure)
        assertEquals("Error message", (result as Result.Failure).message)
        assertEquals(404, result.errorCode)
        verify(apiService).getExchangeRates()
        verify(exchangeRateDao, never()).insertAll(anyList())
    }

    @Test
    fun `test Database Cache`(): Unit = runTest {
        // Mock successful API response
        invokeMockSuccessResponse()

        // Mock cached data in the database
        val cachedData = listOf(ExchangeRateEntity("USD", 1.0))
        `when`(exchangeRateDao.getAllExchangeRates()).thenReturn(cachedData)

        // Call the repository function
        val result = repository.getExchangeRates()

        // Verify the result
        assertTrue(result is Result.Success)
        assertEquals(200, (result as Result.Success).responseCode)
        assertEquals(cachedData[0].currencyCode, result.data?.toDbModel()?.get(0)?.currencyCode) // can't compare whole because of timestamp
        verify(apiService, never()).getExchangeRates() // No network call should be made
        verify(exchangeRateDao, never()).insertAll(anyList()) // No insertion should happen
    }

    @Test
    fun `test Stale Data`(): Unit = runTest {
        // Mock successful API response
        invokeMockSuccessResponse()

        // Mock stale cached data in the database
        val staleData = listOf(ExchangeRateEntity("USD", 1.0, timestamp = System.currentTimeMillis() - Constants.BandwidthThreshold.refreshInterval - 1))
        `when`(exchangeRateDao.getAllExchangeRates()).thenReturn(staleData)

        // Call the repository function
        val result = repository.getExchangeRates()

        // Verify the result
        assertTrue(result is Result.Success)
        assertEquals(200, (result as Result.Success).responseCode)
        verify(apiService).getExchangeRates() // Network call should be made
        verify(exchangeRateDao, times(1)).insertAll(anyList()) // Fresh data should be inserted
    }

    private suspend fun invokeMockSuccessResponse(): Response<OpenExchangeRatesResponse>? {
       val mockResponse  = mock<Response<OpenExchangeRatesResponse>>()
        `when`(apiService.getExchangeRates()).thenReturn(mockResponse)
        `when`(mockResponse.isSuccessful).thenReturn(true)
        `when`(mockResponse.body()).thenReturn(mock())
        `when`(mockResponse.code()).thenReturn(200)
        return mockResponse
    }
}