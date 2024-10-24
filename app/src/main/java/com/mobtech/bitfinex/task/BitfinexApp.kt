package com.mobtech.bitfinex.task

import android.app.Application
import com.mobtech.bitfinex.task.di.mapperModule
import com.mobtech.bitfinex.task.di.networkModule
import com.mobtech.bitfinex.task.di.repositoryModule
import com.mobtech.bitfinex.task.di.useCaseModule
import com.mobtech.bitfinex.task.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class BitfinexApp : Application() {
    override fun onCreate() {
        super.onCreate()
        setupKoin()
    }

    private fun setupKoin() = startKoin {
        androidContext(this@BitfinexApp)
        modules(networkModule, repositoryModule, useCaseModule, mapperModule, viewModelModule)
    }
}