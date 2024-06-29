package com.example.composecurrencyconverter.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.composecurrencyconverter.data.local.entities.ExchangeRateEntity
import com.example.composecurrencyconverter.utils.Constants

@Dao
interface ExchangesRatesDao {
    @Query("SELECT * FROM ${Constants.Entities.EXCHANGE_RATE_ENTITY}")
    suspend fun getAllExchangeRates(): List<ExchangeRateEntity>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(exchangeRates: List<ExchangeRateEntity>)
}