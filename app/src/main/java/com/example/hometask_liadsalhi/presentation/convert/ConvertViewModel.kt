package com.example.hometask_liadsalhi.presentation.convert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.hometask_liadsalhi.PaymentApp
import com.example.hometask_liadsalhi.domain.model.ConvertedAmount
import com.example.hometask_liadsalhi.domain.model.Payment
import com.example.hometask_liadsalhi.domain.repository.PaymentRepository
import com.example.hometask_liadsalhi.domain.usecase.ConvertAmountUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.math.BigDecimal
import java.math.RoundingMode

class ConvertViewModel(
    paymentRepository: PaymentRepository,
    private val convertAmount: ConvertAmountUseCase
) : ViewModel() {

    // the payment we convert, read once when the screen opens
    private val payment: Payment? = paymentRepository.getLastPayment()

    // shown at the top of the screen
    val baseText: String = payment?.let { "${it.amount.toMoneyText()} ${it.currency.name}" } ?: ""

    private val _uiState = MutableStateFlow<ConvertUiState>(ConvertUiState.Loading)
    val uiState: StateFlow<ConvertUiState> = _uiState.asStateFlow()

    // load the rates as soon as the screen opens
    init {
        loadRates()
    }

    // called when the screen opens and when the user taps retry
    fun loadRates() {
        val current = payment
        if (current == null) {
            _uiState.value = ConvertUiState.Error("No payment to convert", canRetry = false)
            return
        }

        _uiState.value = ConvertUiState.Loading
        viewModelScope.launch {
            convertAmount(current.amount, current.currency)
                .onSuccess { rows ->
                    _uiState.value = ConvertUiState.Success(rows.map { it.toRowUi() })
                }
                .onFailure { error ->
                    _uiState.value = ConvertUiState.Error(error.toMessage())
                }
        }
    }

    // format one domain row for the screen: rate with 4 decimals, amount with 2
    private fun ConvertedAmount.toRowUi() = ConvertRowUi(
        currencyCode = currencyCode,
        rate = rate.setScale(4, RoundingMode.HALF_UP).toPlainString(),
        amount = amount.toMoneyText()
    )

    // clear message for the user instead of a technical error
    private fun Throwable.toMessage(): String =
        if (this is IOException) {
            "No internet connection. Check your connection and try again."
        } else {
            "Could not load exchange rates. Please try again."
        }

    private fun BigDecimal.toMoneyText(): String =
        setScale(2, RoundingMode.HALF_UP).toPlainString()

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as PaymentApp
                ConvertViewModel(
                    paymentRepository = app.container.paymentRepository,
                    convertAmount = app.container.convertAmountUseCase
                )
            }
        }
    }
}