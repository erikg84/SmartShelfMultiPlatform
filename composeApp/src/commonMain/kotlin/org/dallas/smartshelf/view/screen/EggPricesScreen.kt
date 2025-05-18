package com.junevrtech.smartshelf.view.screen

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.junevrtech.smartshelf.model.EggPrice
import com.junevrtech.smartshelf.model.EggPriceHistory
import com.junevrtech.smartshelf.repository.ApiResult
import com.junevrtech.smartshelf.viewmodel.EggPricesViewModel
import java.math.BigDecimal
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EggPricesScreen(
    onNavigateBack: () -> Unit,
    viewModel: EggPricesViewModel = hiltViewModel()
) {
    val eggPrices by viewModel.eggPrices.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = Unit) {
        viewModel.loadNationalEggPrices()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Egg Prices") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadNationalEggPrices() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val pricesResult = eggPrices) {
                is ApiResult.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is ApiResult.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error Loading Data",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = pricesResult.exception.message ?: "Unknown error occurred",
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                is ApiResult.Success -> {
                    EggPricesContent(pricesResult.data)
                }
            }
        }
    }
}

@Composable
fun EggPricesContent(eggPriceHistory: EggPriceHistory) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        EggPriceSummary(eggPriceHistory)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Recent Prices",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (eggPriceHistory.prices.isEmpty()) {
            Text(
                text = "No price data available",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 16.dp)
            )
        } else {
            LazyColumn {
                items(eggPriceHistory.prices) { eggPrice ->
                    EggPriceItem(eggPrice)
                }
            }
        }
    }
}

@Composable
fun EggPriceSummary(eggPriceHistory: EggPriceHistory) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Price Summary",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PriceStat("Average", eggPriceHistory.averagePrice)
                PriceStat("Lowest", eggPriceHistory.lowestPrice)
                PriceStat("Highest", eggPriceHistory.highestPrice)
            }

            Spacer(modifier = Modifier.height(8.dp))

            val priceChangeColor = if (eggPriceHistory.priceChange30Days >= BigDecimal.ZERO) {
                Color(0xFF4CAF50) // Green
            } else {
                Color(0xFFF44336) // Red
            }

            Text(
                text = "30-Day Change: $${eggPriceHistory.priceChange30Days}",
                color = priceChangeColor
            )

            Text(
                text = "Last Updated: ${eggPriceHistory.lastUpdated.format(DateTimeFormatter.ISO_DATE)}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun PriceStat(label: String, price: BigDecimal) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "$$price",
            style = MaterialTheme.typography.titleSmall
        )
    }
}

@Composable
fun EggPriceItem(eggPrice: EggPrice) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = eggPrice.eggType.toString().replace("_", " "),
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = eggPrice.date.format(DateTimeFormatter.ISO_DATE),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Source: ${eggPrice.source}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Text(
                text = "$${eggPrice.price}",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}