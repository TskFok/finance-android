package com.finance.app

import android.app.Application
import com.finance.app.di.AppContainer

class FinanceApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppContainer.initialize(this)
    }
}
