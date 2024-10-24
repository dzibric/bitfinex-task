package com.mobtech.bitfinex.task.domain.ticker.mocks

import com.mobtech.bitfinex.task.data.ticker.model.Ticker
import com.mobtech.bitfinex.task.domain.ticker.model.UiTicker

fun mockUiTicker(name: String = "", symbol: String = "", icon: Int = 0, price: Double = 0.0, changePerc: Double = 0.0) =
    UiTicker(
        name = name,
        symbol = symbol,
        icon = icon,
        lastPrice = price,
        dailyChangePercentage = changePerc
    )

fun mockTicker(name: String = "", symbol: String = "", price: Double = 0.0, changePerc: Double = 0.0) =
    Ticker(
        name = name,
        symbol = symbol,
        lastPrice = price,
        dailyChangePercentage = changePerc
    )