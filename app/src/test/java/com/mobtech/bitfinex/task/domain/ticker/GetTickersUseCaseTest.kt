package com.mobtech.bitfinex.task.domain.ticker

import com.mobtech.bitfinex.task.data.ticker.repository.TickersRepository
import com.mobtech.bitfinex.task.domain.ticker.mapper.TickerMapper
import com.mobtech.bitfinex.task.domain.ticker.mocks.mockTicker
import com.mobtech.bitfinex.task.domain.ticker.mocks.mockUiTicker
import com.mobtech.bitfinex.task.domain.ticker.model.UiTicker
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetTickersUseCaseTest {

    private lateinit var useCase: GetTickersUseCase
    private val repository: TickersRepository = mockk()
    private val mapper: TickerMapper = mockk()
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private val testFetchDelay = 5000L

    @Before
    fun setup() {
        useCase = GetTickersUseCase(repository, mapper, testDispatcher)
    }

    @Test
    fun `invoke emits mapped data from repository`() = testScope.runTest {
        val tickers = listOf(mockTicker("BTCUSD"), mockTicker("ETHUSD"))
        val uiTickers = listOf(mockUiTicker("BTCUSD_UI"), mockUiTicker("ETHUSD_UI"))

        coEvery { repository.getTickers() } returns tickers
        every { mapper.mapTickerToUi(tickers[0]) } returns uiTickers[0]
        every { mapper.mapTickerToUi(tickers[1]) } returns uiTickers[1]

        val emissions = mutableListOf<List<UiTicker>>()
        val job = launch {
            useCase().take(1).collect { data ->
                emissions.add(data)
            }
        }

        // Advance time to allow emission
        advanceUntilIdle()

        assertEquals(1, emissions.size)
        assertEquals(uiTickers, emissions[0])

        job.cancel()
    }

    @Test
    fun `invoke delays between emissions`() = testScope.runTest {
        val tickers1 = listOf(mockTicker("BTCUSD"))
        val tickers2 = listOf(mockTicker("ETHUSD"))
        val uiTickers1 = listOf(mockUiTicker("BTCUSD_UI"))
        val uiTickers2 = listOf(mockUiTicker("ETHUSD_UI"))

        coEvery { repository.getTickers() } returnsMany listOf(tickers1, tickers2)
        every { mapper.mapTickerToUi(tickers1[0]) } returns uiTickers1[0]
        every { mapper.mapTickerToUi(tickers2[0]) } returns uiTickers2[0]

        val emissions = mutableListOf<List<UiTicker>>()
        val job = launch {
            useCase().take(2).collect { data ->
                emissions.add(data)
            }
        }

        // Advance time to the first emission
        advanceTimeBy(testFetchDelay)
        assertEquals(1, emissions.size)
        assertEquals(uiTickers1, emissions[0])

        // Advance time by fetch delay to get the second emission
        advanceTimeBy(testFetchDelay)
        advanceUntilIdle()

        // Assert second emission
        assertEquals(2, emissions.size)
        assertEquals(uiTickers2, emissions[1])

        job.cancel()
    }

    @Test
    fun `invoke propagates exceptions from repository`() = testScope.runTest {
        val exception = Exception("Repository error")
        coEvery { repository.getTickers() } throws exception

        var caughtException: Throwable? = null
        val job = launch {
            try {
                useCase().collect()
            } catch (e: Throwable) {
                caughtException = e
            }
        }

        // Advance time to allow exception to be thrown
        advanceUntilIdle()

        assertNotNull(caughtException)
        assertEquals(exception, caughtException)

        job.cancel()
    }

    @Test
    fun `invoke propagates exceptions from mapper`() = testScope.runTest {
        val tickers = listOf(mockTicker("BTCUSD"))
        val exception = Exception("Mapper error")

        coEvery { repository.getTickers() } returns tickers
        every { mapper.mapTickerToUi(any()) } throws exception

        var caughtException: Throwable? = null
        val job = launch {
            try {
                useCase().collect()
            } catch (e: Throwable) {
                caughtException = e
            }
        }

        // Advance time to allow exception to be thrown
        advanceUntilIdle()

        assertNotNull(caughtException)
        assertEquals(exception, caughtException)

        job.cancel()
    }
}