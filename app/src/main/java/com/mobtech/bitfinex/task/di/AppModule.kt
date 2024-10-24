package com.mobtech.bitfinex.task.di

import com.mobtech.bitfinex.task.data.network.BitfinexApiService
import com.mobtech.bitfinex.task.data.network.BitfinexApiServiceImpl
import com.mobtech.bitfinex.task.data.ticker.repository.TickersRepository
import com.mobtech.bitfinex.task.data.ticker.repository.TickersRepositoryImpl
import com.mobtech.bitfinex.task.domain.ticker.GetTickersUseCase
import com.mobtech.bitfinex.task.domain.ticker.mapper.TickerMapper
import com.mobtech.bitfinex.task.domain.ticker.mapper.TickerMapperImpl
import com.mobtech.bitfinex.task.ui.screen.tickers.TickersViewModel
import com.mobtech.bitfinex.task.util.NetworkConnectivityObserver
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val networkModule = module {
    singleOf(::BitfinexApiServiceImpl) bind BitfinexApiService::class
    single {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
            engine {
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
    single(named("io")) { Dispatchers.IO }
    single(named("default")) { Dispatchers.Default }
    single(named("main")) { Dispatchers.Main }
    single(named("unconfined")) { Dispatchers.Unconfined }

    factory { GetTickersUseCase(get(), get(), get(named("io"))) }
}

val viewModelModule = module {
    single { NetworkConnectivityObserver(androidContext()) }
    viewModelOf(::TickersViewModel)
}