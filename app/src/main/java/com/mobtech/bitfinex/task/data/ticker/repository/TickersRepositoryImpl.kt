package com.mobtech.bitfinex.task.data.ticker.repository

import com.mobtech.bitfinex.task.data.network.BitfinexApiService

class TickersRepositoryImpl(
    private val apiService: BitfinexApiService
) : TickersRepository {

    override suspend fun getTickers() = apiService.getTickers()
}