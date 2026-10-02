package com.example.hometask_liadsalhi.domain.model

import java.math.BigDecimal

// why an entered amount is not valid
enum class AmountError { EMPTY, NOT_A_NUMBER, NOT_POSITIVE, TOO_MANY_DECIMALS }

// the result of checking the amount: either valid (with the number) or invalid (with the reason)
sealed interface AmountValidation {
    data class Valid(val amount: BigDecimal) : AmountValidation
    data class Invalid(val error: AmountError) : AmountValidation
}