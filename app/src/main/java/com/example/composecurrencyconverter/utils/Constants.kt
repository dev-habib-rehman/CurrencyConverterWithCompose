package com.example.composecurrencyconverter.utils

object Constants {

    object Database {
        const val DB_NAME = "PayPay_Test_DB"
    }

    object Entities {
        const val EXCHANGE_RATE_ENTITY = "exchange_rates"
        const val API_TIME_STAMP_ENTITY = "api_request_timestamp"
    }

    object ApiEndpoints {
        const val CURRENCY_RATES = "api/latest.json"
    }

    object QueryParams {
        const val APP_ID = "app_id"
    }

    object BandwidthThreshold {
        const val refreshInterval = 30 * 60 * 1000 // 30 minutes in milliseconds
        fun isDataValid(timestamp: Long): Boolean {
            val currentTime = System.currentTimeMillis()
            return currentTime - timestamp < refreshInterval
        }
    }
}