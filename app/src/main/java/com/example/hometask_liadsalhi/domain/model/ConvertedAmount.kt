package com.example.hometask_liadsalhi.domain.model

import java.math.BigDecimal

// one row on the convert screen: the target currency, its rate from the base, and the converted amount
data class ConvertedAmount(
    val currencyCode: String,
    val rate: BigDecimal,
    val amount: BigDecimal
)