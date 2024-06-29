package com.example.composecurrencyconverter.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.composecurrencyconverter.data.remote.models.OpenExchangeRatesResponse
import com.example.composecurrencyconverter.utils.Constants

@Entity(tableName = Constants.Entities.EXCHANGE_RATE_ENTITY)
data class ExchangeRateEntity(
    @PrimaryKey
    val currencyCode: String,
    val exchangeRate: Double,
    val timestamp: Long = System.currentTimeMillis()
)

fun List<ExchangeRateEntity>.asDomainModel(): OpenExchangeRatesResponse {
    val ratesMap = this.associateBy({ it.currencyCode }, { it.exchangeRate })
    return OpenExchangeRatesResponse(timestamp = this[0].timestamp, rates = ratesMap)
}