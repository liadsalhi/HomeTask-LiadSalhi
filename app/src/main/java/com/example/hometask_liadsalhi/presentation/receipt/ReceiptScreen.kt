package com.example.hometask_liadsalhi.presentation.receipt

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

// route: connects the screen to the viewmodel
@Composable
fun ReceiptRoute(
    onConvert: () -> Unit,
    onFinish: () -> Unit,
    viewModel: ReceiptViewModel = viewModel(factory = ReceiptViewModel.Factory)
) {
    ReceiptScreen(
        state = viewModel.uiState,
        onConvert = onConvert,
        onFinish = onFinish
    )
}

// screen: only draws the receipt it receives
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptScreen(
    state: ReceiptUiState?,
    onConvert: () -> Unit,
    onFinish: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Receipt") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
        ) {
            if (state == null) {
                Text("No payment to show", style = MaterialTheme.typography.bodyLarge)
            } else {
                // only rows that are not null are shown
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ReceiptRow(label = "Amount", value = state.amount)
                        state.currency?.let { ReceiptRow(label = "Currency", value = it) }
                        state.installments?.let { ReceiptRow(label = "Installments", value = it.toString()) }
                    }
                }
            }

            // pushes the buttons to the bottom of the screen
            Spacer(Modifier.weight(1f))

            if (state != null) {
                OutlinedButton(onClick = onConvert, modifier = Modifier.fillMaxWidth()) {
                    Text("Convert")
                }
                Spacer(Modifier.height(12.dp))
            }
            Button(onClick = onFinish, modifier = Modifier.fillMaxWidth()) {
                Text("Finish")
            }
        }
    }
}

// one receipt line: label on the left, value on the right
@Composable
private fun ReceiptRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}