package com.mobtech.bitfinex.task.di

import com.mobtech.bitfinex.task.data.network.BitfinexApiService
import com.mobtech.bitfinex.task.data.network.BitfinexApiServiceImpl
import com.mobtech.bitfinex.task.data.ticker.repository.TickersRepository
import com.mobtech.bitfinex.task.data.ticker.repository.TickersRepositoryImpl
import com.mobtech.bitfinex.task.domain.ticker.GetTickersUseCase
import com.mobtech.bitfinex.task.domain.ticker.mapper.TickerMapper
import com.mobtech.bitfinex.task.domain.ticker.mapper.TickerMapperImpl
import com.mobtech.bitfinex.task.ui.screen.tickers.TickersViewModel
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val networkModule = module {
    singleOf(::BitfinexApiServiceImpl) bind BitfinexApiService::class
    single {
        HttpClient(CIO) {
            engine {
                // Set custom settings here
            }
        }
    }
}

val repositoryModule = module {
    singleOf(::TickersRepositoryImpl) bind TickersRepository::class
}

val mapperModule = module {
    singleOf(::TickerMapperImpl) bind TickerMapper::class
}

val useCaseModule = module {
    factoryOf(::GetTickersUseCase)
}

val viewModelModule = module {
    viewModelOf(::TickersViewModel)
}