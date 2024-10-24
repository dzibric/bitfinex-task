package com.mobtech.bitfinex.task.domain.ticker.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.text.DecimalFormat

@Parcelize
data class UiTicker(
    val name: String,
    val symbol: String,
    val icon: Int,
    val lastPrice: Double,
    val dailyChangePercentage: Double
) : Parcelable {

    val formattedPrice: String
        get() = if (lastPrice < 100) {
            DecimalFormat("€#,##0.00##").format(lastPrice)
        } else {
            DecimalFormat("€#,##0.00").format(lastPrice)
        }

    val formattedPercentage: String
        get() {
            val sign = if (dailyChangePercentage >= 0) "+" else ""
            return "$sign${DecimalFormat("#0.###").format(dailyChangePercentage)}%"
        }
}