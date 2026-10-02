package com.example.hometask_liadsalhi.domain.usecase

import com.example.hometask_liadsalhi.domain.model.ConvertedAmount
import com.example.hometask_liadsalhi.domain.model.Currency
import com.example.hometask_liadsalhi.domain.repository.ExchangeRateRepository
import java.math.BigDecimal
import java.math.RoundingMode

// gets live rates and converts the payment amount to the other currencies
class ConvertAmountUseCase(
    private val repository: ExchangeRateRepository
) {

    suspend operator fun invoke(amount: BigDecimal, base: Currency): Result<List<ConvertedAmount>> {
        // all target currencies except the base itself
        val targets = TARGET_CURRENCIES.filter { it != base.name }

        return repository.getRates(base.name, targets).mapCatching { rates ->
            val rows = targets.mapNotNull { code ->
                rates[code]?.let { rate ->
                    ConvertedAmount(
                        currencyCode = code,
                        rate = rate,
                        amount = (amount * rate).setScale(2, RoundingMode.HALF_UP)
                    )
                }
            }
            // an empty answer from the server is treated as an error, not as an empty screen
            check(rows.isNotEmpty()) { "No exchange rates received" }
            rows
        }
    }

    companion object {
        val TARGET_CURRENCIES = listOf(
            "USD", // us dollar
            "ILS", // israeli new shekel
            "EUR", // euro
            "GBP", // british pound
            "JPY", // japanese yen
            "CHF"  // swiss franc
        )
    }
}