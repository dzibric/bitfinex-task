package com.mobtech.bitfinex.task.domain.ticker.mapper

import com.mobtech.bitfinex.task.data.ticker.model.Ticker
import com.mobtech.bitfinex.task.domain.ticker.model.UiTicker

interface TickerMapper {
    fun mapTickerToUi(ticker: Ticker): UiTicker
}