package com.mobtech.bitfinex.task.domain.ticker

import com.mobtech.bitfinex.task.data.ticker.repository.TickersRepository
import com.mobtech.bitfinex.task.domain.ticker.mapper.TickerMapper

class GetTickersUseCase(
    private val repository: TickersRepository,
    private val mapper: TickerMapper
) {
    suspend operator fun invoke() = repository.getTickers().map { mapper.mapTickerToUi(it) }
}