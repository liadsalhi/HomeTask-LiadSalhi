package com.example.hometask_liadsalhi

import android.app.Application
import com.example.hometask_liadsalhi.di.AppContainer

// runs once when the app starts holds the container for the whole session
class PaymentApp : Application() {
    val container = AppContainer()
}