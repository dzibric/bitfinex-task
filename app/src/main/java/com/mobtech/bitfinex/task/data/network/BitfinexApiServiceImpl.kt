package com.mobtech.bitfinex.task.data.network

import com.mobtech.bitfinex.task.data.network.exceptions.NetworkErrorException
import com.mobtech.bitfinex.task.data.network.exceptions.ServerErrorException
import com.mobtech.bitfinex.task.data.network.exceptions.UnknownErrorException
import com.mobtech.bitfinex.task.data.ticker.model.Ticker
import com.mobtech.bitfinex.task.util.BASE_URL
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.double
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

class BitfinexApiServiceImpl(private val client: HttpClient) : BitfinexApiService {

    private val symbolMap = mapOf(
        "tBTCUSD" to Pair("Bitcoin", "BTC"),
        "tETHUSD" to Pair("Ethereum", "ETH"),
        "tCHSB:USD" to Pair("SwissBorg", "CHSB"),
        "tLTCUSD" to Pair("Litecoin", "LTC"),
        "tXRPUSD" to Pair("Ripple", "XRP"),
        "tDSHUSD" to Pair("Dash", "DSH"),
        "tRRTUSD" to Pair("Recovery Right Token", "RRT"),
        "tEOSUSD" to Pair("EOS", "EOS"),
        "tSANUSD" to Pair("Santiment", "SAN"),
        "tDATUSD" to Pair("Data", "DAT"),
        "tSNTUSD" to Pair("Status", "SNT"),
        "tDOGE:USD" to Pair("Dogecoin", "DOGE"),
        "tLUNA:USD" to Pair("Terra", "LUNA"),
        "tMATIC:USD" to Pair("Polygon", "MATIC"),
        "tNEXO:USD" to Pair("Nexo", "NEXO"),
        "tOCEAN:USD" to Pair("Ocean Protocol", "OCEAN"),
        "tBEST:USD" to Pair("Bitpanda Ecosystem Token", "BEST"),
        "tAAVE:USD" to Pair("Aave", "AAVE"),
        "tPLUUSD" to Pair("Pluton", "PLU"),
        "tFILUSD" to Pair("Filecoin", "FIL")
    )

    override suspend fun getTickers(): List<Ticker> {
        // Move BASE_URL to App config if different variants are available
        val response: HttpResponse = client.get("$BASE_URL/tickers?symbols=${symbolMap.keys.joinToString(",")}") {
            headers {
                append(HttpHeaders.Accept, "application/json")
            }
        }
        val responseCode = response.status.value
        when (responseCode) {
            200 -> return mapTickers(response.body())
            in 400..499 -> throw NetworkErrorException()
            in 500..599 -> throw ServerErrorException()
            else -> throw UnknownErrorException()
        }
    }

    private fun mapTickers(tickerData: JsonArray): List<Ticker> {
        return tickerData.map { tickerElement ->
            val tickerArray = tickerElement.jsonArray
            val symbol = tickerArray[0].jsonPrimitive.content
            val lastPrice = tickerArray[7].jsonPrimitive.double
            val dailyChangePercentage = tickerArray[6].jsonPrimitive.double

            Ticker(
                name = symbolMap[symbol]?.first ?: "",
                symbol = symbolMap[symbol]?.second ?: "",
                lastPrice = lastPrice,
                dailyChangePercentage = dailyChangePercentage
            )
        }
    }
}