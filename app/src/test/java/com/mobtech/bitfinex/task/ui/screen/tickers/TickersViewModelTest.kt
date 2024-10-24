package com.mobtech.bitfinex.task.ui.screen.tickers

import com.mobtech.bitfinex.task.data.network.exceptions.NetworkErrorException
import com.mobtech.bitfinex.task.data.network.exceptions.ServerErrorException
import com.mobtech.bitfinex.task.domain.ticker.GetTickersUseCase
import com.mobtech.bitfinex.task.domain.ticker.mocks.mockUiTicker
import com.mobtech.bitfinex.task.domain.ticker.model.UiTicker
import com.mobtech.bitfinex.task.util.NetworkConnectivityObserver
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue

class TickersViewModelTest {

    private lateinit var viewModel: TickersViewModel
    private val getTickersUseCase: GetTickersUseCase = mockk()
    private val networkConnectivityObserver: NetworkConnectivityObserver = mockk()

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { networkConnectivityObserver.networkStatus } returns MutableStateFlow(NetworkConnectivityObserver.Status.Available)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() = testScope.runTest {
        initViewModel()

        val initialState = viewModel.uiState.value
        assertTrue(initialState.tickers.isEmpty())
        assertTrue(initialState.filteredTickers.isEmpty())
        assertTrue(initialState.isLoading)
        assertEquals("", initialState.searchQuery)
        assertEquals(TickersError.None, initialState.error)
    }

    @Test
    fun `fetchTickersPeriodically fetches tickers when network is available`() = testScope.runTest {
        val tickers = listOf(mockUiTicker("BTCUSD"), mockUiTicker("ETHUSD"))
        every { getTickersUseCase.invoke() } returns flowOf(tickers)
        initViewModel()

        viewModel.refreshTickers()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(tickers, state.tickers)
        assertEquals(tickers, state.filteredTickers)
        assertFalse(state.isLoading)
        assertEquals(TickersError.None, state.error)
    }

    @Test
    fun `fetchTickersPeriodically handles ServerErrorException correctly`() = testScope.runTest {
        every { getTickersUseCase.invoke() } returns flow { throw ServerErrorException() }
        initViewModel()

        viewModel.refreshTickers()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(TickersError.Server, state.error)
    }

    @Test
    fun `fetchTickersPeriodically handles NetworkErrorException correctly`() = testScope.runTest {
        every { getTickersUseCase.invoke() } returns flow { throw NetworkErrorException() }
        initViewModel()

        viewModel.refreshTickers()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(TickersError.Network, state.error)
    }

    @Test
    fun `onSearchQueryChanged filters tickers correctly`() = testScope.runTest {
        val tickers = listOf(
            mockUiTicker("BTCUSD"),
            mockUiTicker("ETHUSD"),
            mockUiTicker("LTCUSD")
        )
        every { getTickersUseCase.invoke() } returns flowOf(tickers)
        initViewModel()

        viewModel.onSearchQueryChanged("ETH")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.filteredTickers.size)
        assertEquals("ETHUSD", state.filteredTickers.first().name)
        assertEquals("ETH", state.searchQuery)
    }

    @Test
    fun `network status changes affect ticker fetching`() = testScope.runTest {
        val networkStatusFlow = MutableStateFlow(NetworkConnectivityObserver.Status.Unavailable)
        every { networkConnectivityObserver.networkStatus } returns networkStatusFlow
        every { getTickersUseCase.invoke() } returns flowOf(listOf(mockUiTicker("BTCUSD")))
        initViewModel()

        networkStatusFlow.value = NetworkConnectivityObserver.Status.Available
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(TickersError.None, state.error)
        assertEquals(1, state.tickers.size)
    }

    private fun initViewModel() {
        viewModel = TickersViewModel(getTickersUseCase, networkConnectivityObserver)
    }
}