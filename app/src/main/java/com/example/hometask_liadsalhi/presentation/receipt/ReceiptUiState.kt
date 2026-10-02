package com.example.hometask_liadsalhi.presentation.receipt

// what the receipt shows. null = this row is hidden.
data class ReceiptUiState(
    val amount: String,
    val currency: String?,
    val installments: Int?
)