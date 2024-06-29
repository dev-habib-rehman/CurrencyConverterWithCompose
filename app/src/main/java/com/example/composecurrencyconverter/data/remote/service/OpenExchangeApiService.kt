package com.example.composecurrencyconverter.data.remote.service

import com.example.composecurrencyconverter.BuildConfig
import com.example.composecurrencyconverter.data.remote.models.OpenExchangeRatesResponse
import com.example.composecurrencyconverter.utils.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenExchangeApiService {

    @GET(Constants.ApiEndpoints.CURRENCY_RATES)
    suspend fun getExchangeRates(@Query(Constants.QueryParams.APP_ID) apiKey: String = BuildConfig.API_KEY)
            : Response<OpenExchangeRatesResponse>
}