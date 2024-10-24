package com.mobtech.bitfinex.task.ui.screen.tickers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mobtech.bitfinex.task.R
import com.mobtech.bitfinex.task.domain.ticker.model.UiTicker

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun TickersScreen(
    tickers: List<UiTicker>,
    search: String,
    sortOption: SortOption,
    isLoading: Boolean,
    onSearchChange: (String) -> Unit,
    onSortOptionChange: (SortOption) -> Unit,
    onRefresh: () -> Unit,
    error: TickersError
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isLoading,
        onRefresh = onRefresh
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            OutlinedTextField(
                value = search,
                onValueChange = onSearchChange,
                label = { Text(stringResource(R.string.search)) },
                maxLines = 1,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    cursorColor = MaterialTheme.colorScheme.primary,
                ),
                shape = RoundedCornerShape(16.dp)
            )

            SortDropdown(sortOption, onSortOptionChange)

            Box(
                Modifier.fillMaxSize()
            ) {
                when {
                    error != TickersError.None -> {
                        ErrorItem(error, onRefresh)
                    }

                    tickers.isEmpty() && !isLoading -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = stringResource(R.string.no_results_try_changing_search_query),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    else -> {
                        LazyColumn {
                            items(tickers) { ticker ->
                                TickerItem(
                                    symbol = ticker.symbol,
                                    name = ticker.name,
                                    icon = ticker.icon,
                                    price = ticker.formattedPrice,
                                    percentageChange = ticker.formattedPercentage
                                )
                            }
                        }
                    }
                }

                PullRefreshIndicator(
                    refreshing = isLoading,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }
}

@Composable
fun SortDropdown(
    sortOption: SortOption,
    onSortOptionChange: (SortOption) -> Unit
) {

    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(
            onClick = { expanded = true },
            elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent)
        ) {
            Text(text = stringResource(R.string.sorted_by, stringResource(sortOption.titleRes)))
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            SortOption.entries.forEach {
                DropdownMenuItem(onClick = {
                    onSortOptionChange(it)
                    expanded = false
                }) {
                    Text(stringResource(it.titleRes))
                }
            }
        }
    }
}

@Composable
fun TickerItem(
    symbol: String,
    name: String,
    icon: Int,
    price: String,
    percentageChange: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.elevatedCardColors().copy(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                Text(text = symbol, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(text = price, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                Text(
                    text = percentageChange,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = if (percentageChange.startsWith("+")) Color.Green else Color.Red
                )
            }
        }
    }
}

@Composable
fun ErrorItem(error: TickersError, onRefresh: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Error Icon",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            text = stringResource(
                when (error) {
                    TickersError.Network -> R.string.network_error_occurred
                    TickersError.Server -> R.string.server_error_occurred
                    else -> R.string.unknown_error_occurred
                }
            ),
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge
        )
        Button(
            onClick = onRefresh,
            modifier = Modifier.padding(top = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Blue,
                contentColor = Color.White
            )
        ) {
            Text(color = Color.White, text = stringResource(R.string.retry))
        }
    }
}

@Composable
@Preview(showBackground = true)
fun TickersScreenPreview() {
    TickersScreen(
        tickers = listOf(
            UiTicker(
                name = "Bitcoin",
                symbol = "BTC",
                icon = R.drawable.ic_bitcoin,
                lastPrice = 50000.0,
                dailyChangePercentage = 0.5
            ),
            UiTicker(
                name = "Ethereum",
                symbol = "ETH",
                icon = R.drawable.ic_eth,
                lastPrice = 3000.0,
                dailyChangePercentage = -0.5
            )
        ),
        search = "",
        isLoading = false,
        onSearchChange = {},
        error = TickersError.None,
        onRefresh = {},
        sortOption = SortOption.PriceAscending,
        onSortOptionChange = {}
    )
}

@Composable
@Preview(showBackground = true)
fun TickersScreenEmptyPreview() {
    TickersScreen(
        tickers = emptyList(),
        search = "",
        isLoading = false,
        onSearchChange = {},
        error = TickersError.None,
        onRefresh = {},
        sortOption = SortOption.PriceAscending,
        onSortOptionChange = {}
    )
}

@Composable
@Preview(showBackground = true)
fun TickersScreenErrorPreview() {
    TickersScreen(
        tickers = listOf(
            UiTicker(
                name = "Bitcoin",
                symbol = "BTC",
                icon = R.drawable.ic_bitcoin,
                lastPrice = 50000.0,
                dailyChangePercentage = 0.5
            ),
        ),
        search = "",
        isLoading = false,
        onSearchChange = {},
        error = TickersError.Network,
        onRefresh = {},
        sortOption = SortOption.PriceAscending,
        onSortOptionChange = {}
    )
}

@Composable
@Preview(showBackground = true)
fun TickersScreenLoadingPreview() {
    TickersScreen(
        tickers = emptyList(),
        search = "",
        isLoading = true,
        onSearchChange = {},
        error = TickersError.None,
        onRefresh = {},
        sortOption = SortOption.PriceAscending,
        onSortOptionChange = {}
    )
}