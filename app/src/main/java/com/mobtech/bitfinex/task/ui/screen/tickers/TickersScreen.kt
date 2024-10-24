package com.mobtech.bitfinex.task.ui.screen.tickers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mobtech.bitfinex.task.domain.ticker.model.UiTicker

@Composable
fun TickersScreen(
    tickers: List<UiTicker>,
    search: String,
    isLoading: Boolean,
    onSearchChange: (String) -> Unit,
    error: TickersError
) {
    Column {
        // Search Bar
        OutlinedTextField(
            value = search,
            onValueChange = onSearchChange,
            label = { Text("Search") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        // Crypto List
        LazyColumn {
            items(tickers) { ticker ->
                TickerItem(
                    symbol = ticker.symbol,
                    name = ticker.name,
                    price = ticker.lastPrice.toString(),
                    percentageChange = ticker.dailyChangePercentage.toString()
                )
            }
        }
    }
}

@Composable
fun TickerItem(
    symbol: String,
    name: String,
    price: String,
    percentageChange: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = name, style = MaterialTheme.typography.bodyLarge)
                Text(text = symbol, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(text = price, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = percentageChange,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (percentageChange.startsWith("+")) Color.Green else Color.Red
                )
            }
        }
    }
}

@Composable
@Preview
fun TickersScreenPreview() {
    TickersScreen(
        tickers = listOf(
            UiTicker("BTC", 50000.0, 0.0),
            UiTicker("ETH", 3000.0, 0.0),
            UiTicker("ADA", 2.0, 0.0),
            UiTicker("DOT", 30.0, 0.0),
            UiTicker("SOL", 150.0, 0.0),
            UiTicker("LUNA", 50.0, 0.0),
            UiTicker("AVAX", 50.0, 0.0),
            UiTicker("FTT", 50.0, 0.0),
            UiTicker("BNB", 50.0, 0.0)
        ),
        search = "",
        isLoading = false,
        onSearchChange = {},
        error = TickersError.None
    )
}

@Composable
@Preview
fun TickersScreenErrorPreview() {
    TickersScreen(
        tickers = emptyList(),
        search = "",
        isLoading = false,
        onSearchChange = {},
        error = TickersError.Network
    )
}

@Composable
@Preview
fun TickersScreenLoadingPreview() {
    TickersScreen(
        tickers = emptyList(),
        search = "",
        isLoading = true,
        onSearchChange = {},
        error = TickersError.None
    )
}