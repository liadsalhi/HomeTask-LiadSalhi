package com.example.hometask_liadsalhi.presentation.receipt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.hometask_liadsalhi.PaymentApp
import com.example.hometask_liadsalhi.domain.model.Payment
import com.example.hometask_liadsalhi.domain.repository.PaymentRepository
import java.math.RoundingMode

class ReceiptViewModel(
    repository: PaymentRepository
) : ViewModel() {

    // read the last payment once when the receipt opens, and prepare it for display
    val uiState: ReceiptUiState? = repository.getLastPayment()?.toUiState()

    // decide which rows to show and format the amount
    private fun Payment.toUiState() = ReceiptUiState(
        amount = amount.setScale(2, RoundingMode.HALF_UP).toPlainString(),
        currency = if (showCurrency) currency.name else null,
        installments = if (hasInstallments) installments else null
    )

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as PaymentApp
                ReceiptViewModel(app.container.paymentRepository)
            }
        }
    }
}