package com.mobtech.bitfinex.task.data.ticker.repository

import com.mobtech.bitfinex.task.data.ticker.model.Ticker

interface TickersRepository {
    suspend fun getTickers(): List<Ticker>
}