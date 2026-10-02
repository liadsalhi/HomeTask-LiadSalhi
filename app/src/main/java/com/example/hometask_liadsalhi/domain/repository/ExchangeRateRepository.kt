package com.example.hometask_liadsalhi.domain.repository

import java.math.BigDecimal

// the contract for getting live exchange rates - the data layer implements it
interface ExchangeRateRepository {

    suspend fun getRates(
        baseCurrency: String,
        targetCurrencies: List<String>
    ): Result<Map<String, BigDecimal>>
}