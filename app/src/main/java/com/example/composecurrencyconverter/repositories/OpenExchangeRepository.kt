package com.example.composecurrencyconverter.repositories

import com.example.composecurrencyconverter.data.remote.Result
import com.example.composecurrencyconverter.data.remote.models.OpenExchangeRatesResponse

interface OpenExchangeRepository {
    suspend fun getExchangeRates(): Result<OpenExchangeRatesResponse>
}