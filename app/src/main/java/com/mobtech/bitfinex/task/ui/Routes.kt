package com.mobtech.bitfinex.task.ui

sealed class Routes(val route: String) {
    data object Tickers: Routes("tickers")
}