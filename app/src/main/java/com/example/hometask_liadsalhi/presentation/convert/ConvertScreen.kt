package com.example.hometask_liadsalhi.presentation.convert

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

// route: connects the screen to the viewmodel
@Composable
fun ConvertRoute(
    onBack: () -> Unit,
    viewModel: ConvertViewModel = viewModel(factory = ConvertViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ConvertScreen(
        baseText = viewModel.baseText,
        state = state,
        onRetry = viewModel::loadRates,
        onBack = onBack
    )
}

// screen: base amount on top, then loading / error / rates table
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConvertScreen(
    baseText: String,
    state: ConvertUiState,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Convert") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text("Base amount", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(baseText, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(16.dp))

            // one of three states, the compiler makes sure all of them are handled
            when (state) {
                ConvertUiState.Loading -> LoadingContent(Modifier.weight(1f))
                is ConvertUiState.Error -> ErrorContent(state.message, state.canRetry, onRetry, Modifier.weight(1f))
                is ConvertUiState.Success -> RatesTable(state.rows, Modifier.weight(1f))
            }

            Text(
                text = "Rates by freecurrencyapi.com",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// spinner in the middle while the rates are loading
@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(Modifier.height(12.dp))
            Text("Loading live rates...", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

// error message + retry button (if retry can help)
@Composable
private fun ErrorContent(
    message: String,
    canRetry: Boolean,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                // screen readers announce the error as soon as it appears
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
            )
            if (canRetry) {
                Spacer(Modifier.height(16.dp))
                Button(onClick = onRetry) { Text("Retry") }
            }
        }
    }
}

// header row + one row per currency
@Composable
private fun RatesTable(rows: List<ConvertRowUi>, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        RateRow(currency = "Currency", rate = "Rate", amount = "Amount", isHeader = true)
        HorizontalDivider()
        LazyColumn {
            items(rows, key = { it.currencyCode }) { row ->
                RateRow(currency = row.currencyCode, rate = row.rate, amount = row.amount)
                HorizontalDivider()
            }
        }
    }
}

// one line of the table: currency | rate | converted amount
@Composable
private fun RateRow(currency: String, rate: String, amount: String, isHeader: Boolean = false) {
    val style = if (isHeader) MaterialTheme.typography.labelLarge else MaterialTheme.typography.bodyLarge
    val color = if (isHeader) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Text(currency, style = style, color = color, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
        Text(rate, style = style, color = color, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
        Text(amount, style = style, color = color, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.End, modifier = Modifier.weight(1.2f))
    }
}