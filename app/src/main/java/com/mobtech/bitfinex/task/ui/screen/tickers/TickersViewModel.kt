package com.mobtech.bitfinex.task.ui.screen.tickers

import com.mobtech.bitfinex.task.domain.ticker.GetTickersUseCase
import com.mobtech.bitfinex.task.domain.ticker.model.UiTicker
import com.mobtech.bitfinex.task.ui.screen.core.CoreEvent
import com.mobtech.bitfinex.task.ui.screen.core.CoreState
import com.mobtech.bitfinex.task.ui.screen.core.CoreViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

data class TickersState(
    val tickers: List<UiTicker> = emptyList(),
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
}

class TickersViewModel(
    private val getTickersUseCase: GetTickersUseCase
) : CoreViewModel<TickersState, TickersEvent>(TickersState()) {

    companion object {
        const val FETCH_DELAY = 5000L
    }

    init {
        fetchTickersPeriodically()
    }

    private fun fetchTickersPeriodically() = launch {
        while (isActive) {
            try {
                val data = getTickersUseCase()
                updateState { it.copy(tickers = data, isLoading = false, error = TickersError.None) }
            } catch (e: Exception) {
                updateState { it.copy(isLoading = false, error = TickersError.Network) }
            }
            delay(FETCH_DELAY)
        }
    }

    fun onTickerClicked(ticker: UiTicker) = emitEvent(TickersEvent.TickerClicked(ticker))

    fun onSearchQueryChanged(query: String) = updateState { it.copy(searchQuery = query) }
}