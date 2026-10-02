package com.example.hometask_liadsalhi.presentation.convert

// one row on the screen
data class ConvertRowUi(
    val currencyCode: String,
    val rate: String,
    val amount: String
)

// the three possible states of the convert screen
sealed interface ConvertUiState {
    data object Loading : ConvertUiState
    data class Error(val message: String, val canRetry: Boolean = true) : ConvertUiState
    data class Success(val rows: List<ConvertRowUi>) : ConvertUiState
}