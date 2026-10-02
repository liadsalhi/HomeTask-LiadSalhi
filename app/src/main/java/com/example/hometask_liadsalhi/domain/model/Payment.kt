package com.example.hometask_liadsalhi.domain.model

import java.math.BigDecimal

// a completed mock payment  this is what the receipt shows
data class Payment(
    val amount: BigDecimal,      // 'double` does not represent decimal fractions precisely
    val currency: Currency,      // ILS when currency is hidden in settings
    val installments: Int,       // 1 = single payment
    val showCurrency: Boolean    // false = omit currency from the receipt
) {
    val hasInstallments: Boolean
        get() = installments > 1

    companion object {
        const val MIN_INSTALLMENTS = 2
        const val MAX_INSTALLMENTS = 12
    }
}