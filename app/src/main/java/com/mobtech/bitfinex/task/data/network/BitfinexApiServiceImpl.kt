package com.mobtech.bitfinex.task.data.network

import com.mobtech.bitfinex.task.data.ticker.model.Ticker
import com.mobtech.bitfinex.task.util.BASE_URL
import com.mobtech.bitfinex.task.util.TICKERS_SYMBOLS_QUERY
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

class BitfinexApiServiceImpl(private val client: HttpClient) : BitfinexApiService {
    override suspend fun getTickers(): List<Ticker> {
        // Move BASE_URL to App config if different variants are available
        val response: HttpResponse = client.get("$BASE_URL/tickers?symbols=$TICKERS_SYMBOLS_QUERY")

        return response.body<List<List<Any>>>().map { tickerData ->
            Ticker(
                symbol = tickerData[0] as String,
                lastPrice = tickerData[7] as Double,
                dailyChangePercentage = tickerData[6] as Double
            )
        }
    }
}