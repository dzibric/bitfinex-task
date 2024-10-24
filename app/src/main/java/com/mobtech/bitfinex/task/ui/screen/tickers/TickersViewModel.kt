package com.mobtech.bitfinex.task.ui.screen.tickers

import com.mobtech.bitfinex.task.R
import com.mobtech.bitfinex.task.data.network.exceptions.NetworkErrorException
import com.mobtech.bitfinex.task.data.network.exceptions.ServerErrorException
import com.mobtech.bitfinex.task.domain.ticker.GetTickersUseCase
import com.mobtech.bitfinex.task.domain.ticker.model.UiTicker
import com.mobtech.bitfinex.task.ui.screen.core.CoreEvent
import com.mobtech.bitfinex.task.ui.screen.core.CoreState
import com.mobtech.bitfinex.task.ui.screen.core.CoreViewModel
import com.mobtech.bitfinex.task.util.NetworkConnectivityObserver
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import java.nio.channels.UnresolvedAddressException

data class TickersState(
    val tickers: List<UiTicker> = emptyList(),
    val filteredTickers: List<UiTicker> = emptyList(),
    val sortOption: SortOption = SortOption.PriceDescending,
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val error: TickersError = TickersError.None
) : CoreState()

sealed class TickersEvent : CoreEvent() {
    data class TickerClicked(val ticker: UiTicker) : TickersEvent()
}

sealed class TickersError {
    data object None : TickersError()
    data object Network : TickersError()
    data object Server : TickersError()
}

enum class SortOption(val titleRes: Int) {
    PriceAscending(R.string.price_ascending),
    PriceDescending(R.string.price_descending),
    PercentageChangeAscending(R.string.price_change_ascending),
    PercentageChangeDescending(R.string.price_change_descending)
}

class TickersViewModel(
    private val getTickersUseCase: GetTickersUseCase,
    private val networkConnectivityObserver: NetworkConnectivityObserver
) : CoreViewModel<TickersState, TickersEvent>(TickersState()) {

    private var fetchJob: Job? = null

    init {
        observeNetworkChanges()
    }

    fun refreshTickers() {
        fetchJob?.cancel()
        fetchTickersPeriodically()
    }

    fun onTickerClicked(ticker: UiTicker) = emitEvent(TickersEvent.TickerClicked(ticker))

    fun onSearchQueryChanged(query: String) {
        updateState { currentState ->
            currentState.copy(
                searchQuery = query,
                filteredTickers = filterTickers(currentState.tickers, query, currentState.sortOption)
            )
        }
    }

    fun onSortOptionChanged(sortOption: SortOption) {
        updateState { currentState ->
            currentState.copy(
                sortOption = sortOption,
                filteredTickers = filterTickers(currentState.tickers, currentState.searchQuery, sortOption)
            )
        }
    }

    private fun observeNetworkChanges() = launch {
        networkConnectivityObserver.networkStatus
            .distinctUntilChanged()
            .collect { status ->
                when (status) {
                    NetworkConnectivityObserver.Status.Available -> {
                        fetchTickersPeriodically()
                    }

                    else -> {
                        stopFetchingTickers()
                        // Optionally immediately show error upon connection loss. Not recommended for user experience since data is available.
                        updateState { it.copy(isLoading = false, error = TickersError.Network) }
                    }
                }
            }
    }

    private fun fetchTickersPeriodically() {
        if (fetchJob?.isActive == true) return
        updateState { it.copy(isLoading = true) }
        fetchJob = launch {
            getTickersUseCase()
                .onEach { data ->
                    updateState {
                        it.copy(
                            tickers = data,
                            filteredTickers = filterTickers(data, it.searchQuery, it.sortOption),
                            isLoading = false,
                            error = TickersError.None
                        )
                    }
                }
                .catch { e ->
                    when (e) {
                        is ServerErrorException -> updateState { it.copy(isLoading = false, error = TickersError.Server) }
                        is NetworkErrorException -> updateState { it.copy(isLoading = false, error = TickersError.Network) }
                        is UnresolvedAddressException -> updateState { it.copy(isLoading = false, error = TickersError.Network) }
                        else -> updateState { it.copy(isLoading = false, error = TickersError.None) }
                    }
                }
                .collect()
        }
    }

    private fun stopFetchingTickers() {
        fetchJob?.cancel()
        fetchJob = null
    }

    private fun filterTickers(tickers: List<UiTicker>, query: String, sortOption: SortOption): List<UiTicker> {
        val filteredTickers = if (query.isEmpty()) {
            tickers
        } else {
            tickers.filter { it.name.contains(query, ignoreCase = true) }
        }
        return when (sortOption) {
            SortOption.PriceAscending -> filteredTickers.sortedBy { it.lastPrice }
            SortOption.PriceDescending -> filteredTickers.sortedByDescending { it.lastPrice }
            SortOption.PercentageChangeAscending -> filteredTickers.sortedBy { it.dailyChangePercentage }
            SortOption.PercentageChangeDescending -> filteredTickers.sortedByDescending { it.dailyChangePercentage }
        }
    }
}