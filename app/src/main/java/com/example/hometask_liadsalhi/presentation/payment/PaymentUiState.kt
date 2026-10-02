package com.example.hometask_liadsalhi.presentation.payment

import com.example.hometask_liadsalhi.domain.model.AmountError
import com.example.hometask_liadsalhi.domain.model.AppSettings
import com.example.hometask_liadsalhi.domain.model.Currency
import com.example.hometask_liadsalhi.domain.model.Payment

// everything the payment screen needs to draw itself
data class PaymentUiState(
    val amountText: String = "",
    val amountError: AmountError? = null,
    val currency: Currency = Currency.ILS,
    val installmentsEnabled: Boolean = false,
    val installments: Int = Payment.MIN_INSTALLMENTS,
    val settings: AppSettings = AppSettings()
) {
    val summary: String
        get() {
            val amount = amountText.trim().ifEmpty { "0" }
            val currencyPart = if (settings.showCurrency) " ${currency.name}" else ""
            val paymentsPart = when {
                !settings.showInstallments -> ""
                installmentsEnabled -> " / $installments payments"
                else -> " / single payment"
            }
            return amount + currencyPart + paymentsPart
        }
}