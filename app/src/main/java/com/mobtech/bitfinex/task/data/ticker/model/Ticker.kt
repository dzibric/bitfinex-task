package com.mobtech.bitfinex.task.data.ticker.model

data class Ticker(
    val name: String,
    val symbol: String,
    val lastPrice: Double,
    val dailyChangePercentage: Double
)