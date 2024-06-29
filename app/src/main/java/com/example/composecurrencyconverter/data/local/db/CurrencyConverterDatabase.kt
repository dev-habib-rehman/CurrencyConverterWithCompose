package com.example.composecurrencyconverter.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.composecurrencyconverter.data.local.dao.ExchangesRatesDao
import com.example.composecurrencyconverter.data.local.entities.ExchangeRateEntity

@Database(
    entities = [ExchangeRateEntity::class],
    version = 1,
    exportSchema = false
)
abstract class CurrencyConverterDatabase : RoomDatabase() {
    abstract fun exchangesRatesDao(): ExchangesRatesDao
}