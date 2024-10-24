package com.mobtech.bitfinex.task.data.network

import com.mobtech.bitfinex.task.data.ticker.model.Ticker

interface BitfinexApiService {
    suspend fun getTickers(): List<Ticker>
}