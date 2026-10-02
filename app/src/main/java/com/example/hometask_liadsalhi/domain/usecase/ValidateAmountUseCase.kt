package com.example.hometask_liadsalhi.domain.usecase

import com.example.hometask_liadsalhi.domain.model.AmountError
import com.example.hometask_liadsalhi.domain.model.AmountValidation
import java.math.BigDecimal

// checks the amount the user typed and converts it to a number
class ValidateAmountUseCase {

    operator fun invoke(amountText: String): AmountValidation {
        // Clean the input: remove spaces, accept "10,5" as "10.5"
        val text = amountText.trim().replace(',', '.')

        // Run the checks in order return on the first failure
        if (text.isEmpty()) {
            return AmountValidation.Invalid(AmountError.EMPTY)
        }
        val amount = text.toBigDecimalOrNull()
            ?: return AmountValidation.Invalid(AmountError.NOT_A_NUMBER)

        if (amount <= BigDecimal.ZERO) {
            return AmountValidation.Invalid(AmountError.NOT_POSITIVE)
        }
        if (amount.stripTrailingZeros().scale() > 2) {
            return AmountValidation.Invalid(AmountError.TOO_MANY_DECIMALS)
        }
        return AmountValidation.Valid(amount)
    }
}