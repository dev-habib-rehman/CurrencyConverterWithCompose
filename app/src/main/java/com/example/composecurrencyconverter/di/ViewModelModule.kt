package com.example.composecurrencyconverter.di

import com.example.composecurrencyconverter.data.local.dao.ExchangesRatesDao
import com.example.composecurrencyconverter.data.remote.service.OpenExchangeApiService
import com.example.composecurrencyconverter.repositories.OpenExchangeRepository
import com.example.composecurrencyconverter.repositories.OpenExchangeRepositoryImpl
import com.example.composecurrencyconverter.ui.viewmodels.MainViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ViewModelModule {

    @Singleton
    @Provides
    fun provideExchangeRateRepository(
        apiService: OpenExchangeApiService,
        exchangeRatesDao: ExchangesRatesDao
    ): OpenExchangeRepository =
        OpenExchangeRepositoryImpl(apiService, exchangeRatesDao)


    @Singleton
    @Provides
    fun provideMainViewModel(repository: OpenExchangeRepository): MainViewModel =
        MainViewModel(repository)

}