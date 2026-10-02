package com.example.hometask_liadsalhi.domain.usecase

import com.example.hometask_liadsalhi.domain.model.AmountError
import com.example.hometask_liadsalhi.domain.model.AmountValidation
import com.example.hometask_liadsalhi.domain.model.Currency
import com.example.hometask_liadsalhi.domain.model.Payment
import com.example.hometask_liadsalhi.domain.repository.PaymentRepository
import java.math.BigDecimal

// the result of trying to create a payment
sealed interface CreatePaymentResult {
    data class Success(val payment: Payment) : CreatePaymentResult
    data class Error(val error: AmountError) : CreatePaymentResult
}

// validates the form, builds the payment by the settings rules, and saves it for the receipt
class CreatePaymentUseCase(
    private val repository: PaymentRepository,
    private val validateAmount: ValidateAmountUseCase
) {

    operator fun invoke(
        amountText: String,
        currency: Currency,
        installmentsEnabled: Boolean,
        installments: Int
    ): CreatePaymentResult {
        return when (val validation = validateAmount(amountText)) {
            is AmountValidation.Invalid -> CreatePaymentResult.Error(validation.error)
            is AmountValidation.Valid -> {
                val payment = buildPayment(validation.amount, currency, installmentsEnabled, installments)
                repository.saveLastPayment(payment)
                CreatePaymentResult.Success(payment)
            }
        }
    }

    // settings rules: hidden currency = ILS, hidden or disabled installments = single payment
    private fun buildPayment(
        amount: BigDecimal,
        currency: Currency,
        installmentsEnabled: Boolean,
        installments: Int
    ): Payment {
        val settings = repository.settings.value
        val useInstallments = settings.showInstallments && installmentsEnabled

        return Payment(
            amount = amount,
            currency = if (settings.showCurrency) currency else Currency.ILS,
            installments = if (useInstallments) {
                installments.coerceIn(Payment.MIN_INSTALLMENTS, Payment.MAX_INSTALLMENTS)
            } else {
                1
            },
            showCurrency = settings.showCurrency
        )
    }
}