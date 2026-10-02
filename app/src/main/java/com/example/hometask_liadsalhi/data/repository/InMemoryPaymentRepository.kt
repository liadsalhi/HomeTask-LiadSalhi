package com.example.hometask_liadsalhi.data.repository

import com.example.hometask_liadsalhi.domain.model.AppSettings
import com.example.hometask_liadsalhi.domain.model.Payment
import com.example.hometask_liadsalhi.domain.repository.PaymentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// keeps all data in memory it lives as long as the app is running
class InMemoryPaymentRepository : PaymentRepository {

    // settings: private writable flow, public read-only view
    private val _settings = MutableStateFlow(AppSettings())
    override val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    // the last payment, for the receipt screen
    private var lastPayment: Payment? = null

    override fun updateSettings(settings: AppSettings) {
        _settings.value = settings
    }

    override fun saveLastPayment(payment: Payment) {
        lastPayment = payment
    }

    override fun getLastPayment(): Payment? = lastPayment
}