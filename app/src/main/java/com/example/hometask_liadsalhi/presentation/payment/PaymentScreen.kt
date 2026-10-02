package com.example.hometask_liadsalhi.presentation.payment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hometask_liadsalhi.domain.model.Currency

//  route: connects the screen to the ViewModel

@Composable
fun PaymentRoute(
    onOpenSettings: () -> Unit,
    onPaymentCreated: () -> Unit,
    viewModel: PaymentViewModel = viewModel(factory = PaymentViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    PaymentScreen(
        state = state,
        onAmountChange = viewModel::onAmountChange,
        onCurrencySelected = viewModel::onCurrencySelected,
        onInstallmentsToggled = viewModel::onInstallmentsToggled,
        onInstallmentsSelected = viewModel::onInstallmentsSelected,
        onContinue = { if (viewModel.onContinue()) onPaymentCreated() },
        onCancel = viewModel::onCancel,
        onOpenSettings = onOpenSettings,
        // the clock reads the time on its own, so only the clock redraws every second
        clock = {
            val time by viewModel.currentTime.collectAsStateWithLifecycle()
            LiveClock(time = time)
        }
    )
}

//  screen: only draws the state it receives

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    state: PaymentUiState,
    onAmountChange: (String) -> Unit,
    onCurrencySelected: (Currency) -> Unit,
    onInstallmentsToggled: (Boolean) -> Unit,
    onInstallmentsSelected: (Int) -> Unit,
    onContinue: () -> Unit,
    onCancel: () -> Unit,
    onOpenSettings: () -> Unit,
    clock: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payment") },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                clock()
            }

            AmountField(
                amountText = state.amountText,
                error = state.amountError,
                onAmountChange = onAmountChange
            )

            // rows that can be hidden from the settings screen
            if (state.settings.showInstallments) {
                InstallmentsRow(
                    enabled = state.installmentsEnabled,
                    count = state.installments,
                    onToggle = onInstallmentsToggled,
                    onCountSelected = onInstallmentsSelected
                )
            }
            if (state.settings.showCurrency) {
                CurrencySelector(
                    selected = state.currency,
                    onSelected = onCurrencySelected
                )
            }

            Text(
                text = "Summary: ${state.summary}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) {
                    Text("Cancel")
                }
                Button(onClick = onContinue, modifier = Modifier.weight(1f)) {
                    Text("Continue")
                }
            }
        }
    }
}