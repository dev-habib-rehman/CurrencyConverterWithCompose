package com.example.composecurrencyconverter.repositories

import com.example.composecurrencyconverter.data.local.dao.ExchangesRatesDao
import com.example.composecurrencyconverter.data.local.entities.asDomainModel
import com.example.composecurrencyconverter.data.remote.Result
import com.example.composecurrencyconverter.data.remote.models.OpenExchangeRatesResponse
import com.example.composecurrencyconverter.data.remote.models.toDbModel
import com.example.composecurrencyconverter.data.remote.service.OpenExchangeApiService
import com.example.composecurrencyconverter.utils.Constants
import javax.inject.Inject

class OpenExchangeRepositoryImpl @Inject constructor(
    private val apiService: OpenExchangeApiService,
    private val exchangeRateDao: ExchangesRatesDao
) : OpenExchangeRepository {
    override suspend fun getExchangeRates(): Result<OpenExchangeRatesResponse> {

        // Check if data is availble in the local database
        val cachedExchangeRate = exchangeRateDao.getAllExchangeRates()

        if ((cachedExchangeRate != null) && cachedExchangeRate.isEmpty()
                .not() && Constants.BandwidthThreshold.isDataValid(
                cachedExchangeRate[0].timestamp
            )
        ) // Use cached data it's still valid
            return Result.Success(cachedExchangeRate.asDomainModel(), 200)

        return try {
            val response = apiService.getExchangeRates()
            if (response.isSuccessful && response.body() != null) {
                // Cache the response in the local database with the current timestamp
                response.body()?.toDbModel()?.let { exchangeRateDao.insertAll(it) }
                Result.Success(response.body()!!, response.code())
            } else {
                Result.Failure(message = response.message(), errorCode = response.code())
            }
        } catch (e: Exception) {
            Result.Failure(
                message = e.localizedMessage ?: e.stackTraceToString(),
                errorCode = null,
                exception = e
            )
        }
    }
}