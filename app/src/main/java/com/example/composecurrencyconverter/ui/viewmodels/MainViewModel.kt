package com.example.composecurrencyconverter.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composecurrencyconverter.data.remote.Result
import com.example.composecurrencyconverter.data.remote.models.OpenExchangeRatesResponse
import com.example.composecurrencyconverter.repositories.OpenExchangeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: OpenExchangeRepository
) : ViewModel() {

    private val _exchangeRates =
        MutableStateFlow<Result<OpenExchangeRatesResponse>>(Result.Loading())
    val exchangeRates: StateFlow<Result<OpenExchangeRatesResponse>> = _exchangeRates

    init {
        getExchangeRates()
    }

    fun getExchangeRates() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.getExchangeRates()
            _exchangeRates.value = result
        }
    }
}