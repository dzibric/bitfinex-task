package com.mobtech.bitfinex.task.data.ticker.model

data class Ticker(
    val symbol: String,
    val lastPrice: Double,
    val dailyChangePercentage: Double
)