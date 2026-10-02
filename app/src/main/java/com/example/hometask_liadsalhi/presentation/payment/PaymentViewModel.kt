package com.example.hometask_liadsalhi.presentation.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.hometask_liadsalhi.PaymentApp
import com.example.hometask_liadsalhi.domain.model.Currency
import com.example.hometask_liadsalhi.domain.repository.PaymentRepository
import com.example.hometask_liadsalhi.domain.usecase.CreatePaymentResult
import com.example.hometask_liadsalhi.domain.usecase.CreatePaymentUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime

// allowed shape while typing: digits, then optionally one dot and up to 2 digits
private val AmountInputPattern = Regex("""\d*([.,]\d{0,2})?""")

class PaymentViewModel(
    repository: PaymentRepository,
    private val createPayment: CreatePaymentUseCase
) : ViewModel() {

    // form state: private writable, public read-only
    private val _uiState = MutableStateFlow(PaymentUiState(settings = repository.settings.value))
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    // live clock: ticks every second, only while the screen is collecting it
    val currentTime: StateFlow<LocalTime> = flow {
        while (true) {
            emit(LocalTime.now())
            delay(1000L - System.currentTimeMillis() % 1000)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), LocalTime.now())

    // settings changes are copied into the form state immediately
    init {
        viewModelScope.launch {
            repository.settings.collect { settings ->
                _uiState.update { it.copy(settings = settings) }
            }
        }
    }

    // user actions from the screen
    fun onAmountChange(text: String) {
        // ignore a key that breaks the shape, e.g. a letter or a 3rd digit after the dot
        if (!AmountInputPattern.matches(text)) return
        _uiState.update { it.copy(amountText = text, amountError = null) }
    }

    fun onCurrencySelected(currency: Currency) {
        _uiState.update { it.copy(currency = currency) }
    }

    fun onInstallmentsToggled(enabled: Boolean) {
        _uiState.update { it.copy(installmentsEnabled = enabled) }
    }

    fun onInstallmentsSelected(count: Int) {
        _uiState.update { it.copy(installments = count) }
    }

    // cancel: back to a clean form, keep the current settings
    fun onCancel() {
        _uiState.update { PaymentUiState(settings = it.settings) }
    }

    // continue: returns true if the payment was created (screen then opens the receipt)
    fun onContinue(): Boolean {
        val state = _uiState.value
        val result = createPayment(
            amountText = state.amountText,
            currency = state.currency,
            installmentsEnabled = state.installmentsEnabled,
            installments = state.installments
        )
        return when (result) {
            is CreatePaymentResult.Success -> true
            is CreatePaymentResult.Error -> {
                _uiState.update { it.copy(amountError = result.error) }
                false
            }
        }
    }

    // tells Android how to create this ViewModel with objects from the AppContainer
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as PaymentApp
                PaymentViewModel(
                    repository = app.container.paymentRepository,
                    createPayment = app.container.createPaymentUseCase
                )
            }
        }
    }
}