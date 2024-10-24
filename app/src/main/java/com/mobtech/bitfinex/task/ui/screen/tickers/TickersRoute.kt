package com.mobtech.bitfinex.task.ui.screen.tickers

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.mobtech.bitfinex.task.ui.screen.core.CoreComposable
import org.koin.androidx.compose.koinViewModel

@Composable
fun TickersRoute(
    navController: NavController,
    viewModel: TickersViewModel = koinViewModel()
) {
    CoreComposable(viewModel) { state ->
        TickersScreen(
            tickers = state.tickers,
            search = state.searchQuery,
            onSearchChange = viewModel::onSearchQueryChanged
        )
    }
}