package com.mobtech.bitfinex.task.domain.ticker.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UiTicker(
    val symbol: String,
    val lastPrice: Double,
    val dailyChangePercentage: Double
) : Parcelable