package com.example.composecurrencyconverter.di

import android.content.Context
import androidx.room.Room
import com.example.composecurrencyconverter.BuildConfig
import com.example.composecurrencyconverter.data.local.dao.ExchangesRatesDao
import com.example.composecurrencyconverter.data.local.db.CurrencyConverterDatabase
import com.example.composecurrencyconverter.data.remote.service.OpenExchangeApiService
import com.example.composecurrencyconverter.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext application: Context): CurrencyConverterDatabase =
        Room.databaseBuilder(
            application,
            CurrencyConverterDatabase::class.java,
            Constants.Database.DB_NAME
        ).build()


    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): OpenExchangeApiService =
        retrofit.create(OpenExchangeApiService::class.java)


    @Singleton
    @Provides
    fun provideExchangeRateDao(appDatabase: CurrencyConverterDatabase): ExchangesRatesDao =
        appDatabase.exchangesRatesDao()
}