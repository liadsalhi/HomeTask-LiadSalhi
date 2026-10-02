package com.example.hometask_liadsalhi.domain.repository

import com.example.hometask_liadsalhi.domain.model.AppSettings
import com.example.hometask_liadsalhi.domain.model.Payment
import kotlinx.coroutines.flow.StateFlow

// the contract for storing data. Domain only defines it the Data layer implements it.
interface PaymentRepository {

    // settings for the current session screens listen and update immediately
    val settings: StateFlow<AppSettings>
    fun updateSettings(settings: AppSettings)

    // the last payment, read by the receipt screen
    fun saveLastPayment(payment: Payment)
    fun getLastPayment(): Payment?
}