package com.example.composecurrencyconverter.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.composecurrencyconverter.data.remote.Result
import com.example.composecurrencyconverter.data.remote.models.OpenExchangeRatesResponse
import com.example.composecurrencyconverter.repositories.OpenExchangeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class MainViewModelTest {

    @Mock
    private lateinit var repository: OpenExchangeRepository

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = MainViewModel(repository)
    }

    @Test
    fun `test Initialization of viewModel`() {
        // Verify that the ViewModel initializes with the correct StateFlow value
        Assert.assertTrue(viewModel.exchangeRates.value is Result.Loading)
    }

    @Test
    fun `test getExchangeRates Success`() {
        runTest {
            // Mock successful response from the repository
            val mockResponse = OpenExchangeRatesResponse(
                disclaimer = "Mock Disclaimer",
                license = "Mock License",
                timestamp = 1234567890,
                base = "USD",
                rates = mapOf(
                    "EUR" to 0.8, "GBP" to 0.7, "AED" to 3.67, "PKR" to 282.1
                )
            )
            `when`(repository.getExchangeRates()).thenReturn(Result.Success(mockResponse, 200))

            // Call the method to get exchange rates
            viewModel.getExchangeRates()

            Assert.assertTrue(viewModel.exchangeRates.first() is Result.Success)
            Assert.assertEquals(
                mockResponse,
                (viewModel.exchangeRates.value as Result.Success).data
            )
        }
    }

    @Test
    fun `test getExchangeRates Error`() {
        runTest {
            // Mock error response from the repository
            val mockError = Exception("An error occurred")
            `when`(repository.getExchangeRates()).thenReturn(
                Result.Failure(
                    message = mockError.message!!, errorCode = 400, exception = mockError
                )
            )
            // Call the method to get exchange rates
            viewModel.getExchangeRates()

            val actualValue = async {
                viewModel.exchangeRates
            }.await()

            Assert.assertTrue(actualValue.value is Result.Failure)
            Assert.assertEquals(mockError, (actualValue.value as Result.Failure).exception)
        }
    }
}
