package com.mobtech.bitfinex.task.domain.ticker

import com.mobtech.bitfinex.task.data.ticker.repository.TickersRepository
import com.mobtech.bitfinex.task.domain.ticker.mapper.TickerMapper
import com.mobtech.bitfinex.task.domain.ticker.model.UiTicker
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class GetTickersUseCase(
    private val repository: TickersRepository,
    private val mapper: TickerMapper,
    private val dispatcher: CoroutineDispatcher
) {
    companion object {
        const val FETCH_DELAY = 5000L
    }

    operator fun invoke(): Flow<List<UiTicker>> = flow {
        while (true) {
            try {
                val data = repository.getTickers().map(mapper::mapTickerToUi)
                emit(data)
                delay(FETCH_DELAY)
            } catch (e: Exception) {
                // To avoid swallowed exception
                throw e
            }
        }
    }.flowOn(dispatcher)
}