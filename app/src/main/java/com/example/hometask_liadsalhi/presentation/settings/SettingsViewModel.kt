package com.example.hometask_liadsalhi.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.hometask_liadsalhi.PaymentApp
import com.example.hometask_liadsalhi.domain.model.AppSettings
import com.example.hometask_liadsalhi.domain.repository.PaymentRepository
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel(
    private val repository: PaymentRepository
) : ViewModel() {

    // the settings come straight from the repository (shared with the payment screen)
    val settings: StateFlow<AppSettings> = repository.settings

    // each switch saves a copy of the settings with one field changed
    fun onShowInstallmentsChange(show: Boolean) {
        repository.updateSettings(settings.value.copy(showInstallments = show))
    }

    fun onShowCurrencyChange(show: Boolean) {
        repository.updateSettings(settings.value.copy(showCurrency = show))
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as PaymentApp
                SettingsViewModel(app.container.paymentRepository)
            }
        }
    }
}