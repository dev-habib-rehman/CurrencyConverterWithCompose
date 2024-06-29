package com.example.composecurrencyconverter.data.local

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.composecurrencyconverter.data.local.dao.ExchangesRatesDao
import com.example.composecurrencyconverter.data.local.db.CurrencyConverterDatabase
import com.example.composecurrencyconverter.data.local.entities.ExchangeRateEntity
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExchangesRatesDaoTest {

    private lateinit var database: CurrencyConverterDatabase
    private lateinit var exchangesRatesDao: ExchangesRatesDao

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, CurrencyConverterDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        exchangesRatesDao = database.exchangesRatesDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndReadAllExchangeRates() = runTest {
        // Given
        val exchangeRatesList = listOf(
            ExchangeRateEntity("EUR", 0.8),
            ExchangeRateEntity("GBP", 0.7),
            ExchangeRateEntity("AED", 3.67),
            ExchangeRateEntity("PKR", 282.1)
        )

        // When
        exchangesRatesDao.insertAll(exchangeRatesList)
        val result = exchangesRatesDao.getAllExchangeRates()

        // Then
        assertNotNull(result)
        assertEquals(exchangeRatesList.size, result?.size)
    }
}
