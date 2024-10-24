package com.mobtech.bitfinex.task.domain.ticker.mapper

import com.mobtech.bitfinex.task.data.ticker.model.Ticker
import com.mobtech.bitfinex.task.domain.ticker.model.UiTicker

class TickerMapperImpl : TickerMapper {
    override fun mapTickerToUi(ticker: Ticker): UiTicker {
        return UiTicker(
            symbol = ticker.symbol,
            lastPrice = ticker.lastPrice,
            dailyChangePercentage = ticker.dailyChangePercentage
        )
    }
}