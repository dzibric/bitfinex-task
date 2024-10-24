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
            tickers = state.filteredTickers,
            search = state.searchQuery,
            isLoading = state.isLoading,
            error = state.error,
            onSearchChange = viewModel::onSearchQueryChanged,
            onRefresh = viewModel::refreshTickers,
            sortOption = state.sortOption,
            onSortOptionChange = viewModel::onSortOptionChanged,
        )
    }
}