package com.example.composecurrencyconverter.data.remote.models

import com.example.composecurrencyconverter.data.local.entities.ExchangeRateEntity

data class OpenExchangeRatesResponse constructor(
    val disclaimer: String = "",
    val license: String = "",
    val timestamp: Long,
    val base: String = "USD",
    val rates: Map<String, Double> = mapOf()
)

fun OpenExchangeRatesResponse.toDbModel(): List<ExchangeRateEntity> {
    return rates.map { (currencyCode, exchangeRate) ->
        ExchangeRateEntity(
            currencyCode = currencyCode,
            exchangeRate = exchangeRate,
            timestamp = System.currentTimeMillis()
        )
    }
}
