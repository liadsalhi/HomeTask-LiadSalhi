package com.example.hometask_liadsalhi.data.repository

import com.example.hometask_liadsalhi.data.remote.CurrencyApi
import com.example.hometask_liadsalhi.domain.repository.ExchangeRateRepository
import java.math.BigDecimal
import kotlin.coroutines.cancellation.CancellationException

// gets live rates from freecurrencyapi
class RemoteExchangeRateRepository(
    private val api: CurrencyApi,
    private val apiKey: String
) : ExchangeRateRepository {

    override suspend fun getRates(
        baseCurrency: String,
        targetCurrencies: List<String>
    ): Result<Map<String, BigDecimal>> {
        return try {
            val response = api.getLatestRates(
                apiKey = apiKey,
                baseCurrency = baseCurrency,
                currencies = targetCurrencies.joinToString(",")
            )
            Result.success(response.data.orEmpty())
        } catch (e: CancellationException) {
            // the user left the screen - let the coroutine stop normally
            throw e
        } catch (e: Exception) {
            // no internet, server error, bad api key - returned as a failure, not a crash
            Result.failure(e)
        }
    }
}