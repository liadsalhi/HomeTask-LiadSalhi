package com.example.hometask_liadsalhi.presentation.payment

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.hometask_liadsalhi.domain.model.AmountError
import com.example.hometask_liadsalhi.domain.model.Currency
import com.example.hometask_liadsalhi.domain.model.Payment

//  Amount

// text field for the amount: decimal keyboard, always shown with 2 decimals, error under it
@Composable
fun AmountField(
    amountText: String,
    error: AmountError?,
    onAmountChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val hintColor = MaterialTheme.colorScheme.onSurfaceVariant

    OutlinedTextField(
        value = amountText,
        onValueChange = onAmountChange,
        label = { Text("Amount") },
        placeholder = { Text("0.00") },
        singleLine = true,
        visualTransformation = remember(hintColor) { MoneyVisualTransformation(hintColor) },
        isError = error != null,
        supportingText = { if (error != null) Text(error.toMessage()) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = ImeAction.Done
        ),
        // "Done" on the keyboard closes it
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        modifier = modifier.fillMaxWidth()
    )
}

// a clear message for each validation error
private fun AmountError.toMessage(): String = when (this) {
    AmountError.EMPTY -> "Please enter an amount"
    AmountError.NOT_A_NUMBER -> "Please enter a valid number"
    AmountError.NOT_POSITIVE -> "Amount must be greater than 0"
    AmountError.TOO_MANY_DECIMALS -> "Use up to 2 digits after the decimal point"
}

// shows the amount with 2 decimals while typing (2 -> 2.00, 2.5 -> 2.50).
// the real text is not changed - the missing part is only drawn, in a lighter color.
private class MoneyVisualTransformation(private val hintColor: Color) : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val typed = text.text
        val missing = missingDecimals(typed)

        val shown = buildAnnotatedString {
            append(typed)
            withStyle(SpanStyle(color = hintColor)) { append(missing) }
        }

        // the cursor can only stand inside what the user really typed
        val cursorMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int) = offset
            override fun transformedToOriginal(offset: Int) = offset.coerceAtMost(typed.length)
        }
        return TransformedText(shown, cursorMapping)
    }

    // what is missing to show 2 decimals: "2" -> ".00", "2." -> "00", "2.5" -> "0", "2.55" -> ""
    private fun missingDecimals(typed: String): String {
        if (typed.isEmpty()) return ""
        val dotIndex = typed.indexOfFirst { it == '.' || it == ',' }
        if (dotIndex == -1) return ".00"
        val decimalsTyped = typed.length - dotIndex - 1
        return "0".repeat((2 - decimalsTyped).coerceAtLeast(0))
    }
}

//  Currency

// two joined buttons: ILS | USD
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencySelector(
    selected: Currency,
    onSelected: (Currency) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier.fillMaxWidth()) {
        Text("Currency", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))

        SingleChoiceSegmentedButtonRow {
            Currency.entries.forEachIndexed { index, currency ->
                SegmentedButton(
                    selected = currency == selected,
                    onClick = { onSelected(currency) },
                    shape = SegmentedButtonDefaults.itemShape(index, Currency.entries.size)
                ) {
                    Text(currency.name)
                }
            }
        }
    }
}

//  Installments

// switch to turn installments on/off + a picker (2-12) that is disabled when off
@Composable
fun InstallmentsRow(
    enabled: Boolean,
    count: Int,
    onToggle: (Boolean) -> Unit,
    onCountSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // is the dropdown open? Pure UI state, so it lives here and not in the ViewModel
    var menuOpen by remember { mutableStateOf(false) }

    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier.fillMaxWidth()) {
        Text("Installments", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Switch(checked = enabled, onCheckedChange = onToggle)
        Spacer(Modifier.width(12.dp))

        Box {
            OutlinedButton(onClick = { menuOpen = true }, enabled = enabled) {
                Text(count.toString())
                Icon(Icons.Filled.ArrowDropDown, contentDescription = "Choose number of installments")
            }
            DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                for (number in Payment.MIN_INSTALLMENTS..Payment.MAX_INSTALLMENTS) {
                    DropdownMenuItem(
                        text = { Text(number.toString()) },
                        onClick = {
                            onCountSelected(number)
                            menuOpen = false
                        }
                    )
                }
            }
        }
    }
}