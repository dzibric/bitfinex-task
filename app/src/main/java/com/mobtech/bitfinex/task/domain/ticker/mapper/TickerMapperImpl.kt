package com.mobtech.bitfinex.task.domain.ticker.mapper

import com.mobtech.bitfinex.task.R
import com.mobtech.bitfinex.task.data.ticker.model.Ticker
import com.mobtech.bitfinex.task.domain.ticker.model.UiTicker

class TickerMapperImpl : TickerMapper {

    private val iconMap = mapOf(
        "BTC" to R.drawable.ic_bitcoin,
        "ETH" to R.drawable.ic_eth,
        "CHSB" to R.drawable.ic_swissborg,
        "LTC" to R.drawable.ic_litecoin,
        "XRP" to R.drawable.ic_xrp,
        "DSH" to R.drawable.ic_dash,
        "RRT" to R.drawable.ic_recovery,
        "EOS" to R.drawable.ic_eos,
        "SAN" to R.drawable.ic_san,
        "DAT" to R.drawable.ic_data,
        "SNT" to R.drawable.ic_san,
        "DOGE" to R.drawable.ic_doge,
        "LUNA" to R.drawable.ic_terra,
        "MATIC" to R.drawable.ic_polygon,
        "NEXO" to R.drawable.ic_nexo,
        "OCEAN" to R.drawable.ic_ocean,
        "BEST" to R.drawable.ic_bitpanda,
        "AAVE" to R.drawable.ic_aave,
        "PLU" to R.drawable.ic_pluto,
        "FIL" to R.drawable.ic_filecoin
    )

    override fun mapTickerToUi(ticker: Ticker): UiTicker {
        return UiTicker(
            name = ticker.name,
            symbol = ticker.symbol,
            icon = iconMap[ticker.symbol] ?: R.drawable.ic_bitcoin,
            lastPrice = ticker.lastPrice,
            dailyChangePercentage = ticker.dailyChangePercentage
        )
    }
}