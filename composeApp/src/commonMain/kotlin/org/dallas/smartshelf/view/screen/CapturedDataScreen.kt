package org.dallas.smartshelf.view.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.toLocalDateTime
import org.dallas.smartshelf.model.ProductCategory
import org.dallas.smartshelf.theme.dimens
import org.dallas.smartshelf.util.ReceiptItem
import org.dallas.smartshelf.util.toCurrencyString
import org.dallas.smartshelf.util.toUnitPriceString
import org.dallas.smartshelf.util.toWeightString
import org.dallas.smartshelf.view.component.InputFieldComponent
import org.dallas.smartshelf.view.component.LoadingComponent
import org.dallas.smartshelf.view.component.Spacer16
import org.dallas.smartshelf.view.component.Spacer4
import org.dallas.smartshelf.view.component.Spacer8
import org.dallas.smartshelf.view.component.dialog.ErrorDialogComponent
import org.dallas.smartshelf.viewmodel.CapturedDataViewModel
import org.jetbrains.compose.resources.painterResource
import smartshelf.composeapp.generated.resources.Res
import smartshelf.composeapp.generated.resources.add_icon
import smartshelf.composeapp.generated.resources.remove_icon
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@Composable
fun CapturedDataScreen(
    viewState: CapturedDataViewModel.ViewState,
    onAction: (CapturedDataViewModel.Action) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.dimens.dp16)
        ) {
            Text(
                text = "Receipt Items",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Receipt Items Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                viewState.receiptItems.forEach { item ->
                    ReceiptItemCard(item)
                }
            }

            Spacer16()

            // Add Product Section
            Text(
                text = "Add Product",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer16()

            viewState.barcode?.let { barcode ->
                Text(
                    text = "Barcode: $barcode",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer16()
            }

            InputFieldComponent(
                onValueChange = { onAction(CapturedDataViewModel.Action.UpdateProductName(it)) },
                label = "Product Name",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer16()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Quantity",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = {
                            if (viewState.quantity > 1) {
                                onAction(CapturedDataViewModel.Action.UpdateQuantity(viewState.quantity - 1))
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.remove_icon),
                            contentDescription = "Decrease quantity"
                        )
                    }

                    Text(
                        text = viewState.quantity.toString(),
                        style = MaterialTheme.typography.titleLarge
                    )

                    IconButton(
                        onClick = {
                            if (viewState.quantity < 999) {
                                onAction(CapturedDataViewModel.Action.UpdateQuantity(viewState.quantity + 1))
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.add_icon),
                            contentDescription = "Increase quantity"
                        )
                    }
                }
            }

            Spacer16()

            Text(
                text = "Category",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer8()

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(ProductCategory.entries) { category ->
                    FilterChip(
                        selected = viewState.category == category,
                        onClick = { onAction(CapturedDataViewModel.Action.UpdateCategory(category)) },
                        label = { Text(category.displayName) }
                    )
                }
            }

            Spacer16()

            var showDatePicker by remember { mutableStateOf(false) }

            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = viewState.expiryDate?.let {
                        "${it.year}-${it.monthNumber.toString().padStart(2, '0')}-${it.dayOfMonth.toString().padStart(2, '0')}"
                    } ?: "Set Expiry Date"
                )

            }

            if (showDatePicker) {
                DatePicker(
                    onDismissRequest = { showDatePicker = false },
                    onDateSelected = { date ->
                        onAction(CapturedDataViewModel.Action.UpdateExpiryDate(date))
                        showDatePicker = false
                    }
                )
            }

            Spacer16()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { onAction(CapturedDataViewModel.Action.Cancel) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = { onAction(CapturedDataViewModel.Action.SaveProduct) },
                    enabled = viewState.productName.isNotBlank(),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Save")
                }
            }
        }
    }

    if (viewState.isLoading) {
        LoadingComponent()
    }

    viewState.error?.let {
        ErrorDialogComponent()
    }

    LaunchedEffect(Unit) {
        onAction(CapturedDataViewModel.Action.StopProcessing)
    }
}

@Composable
fun ReceiptItemCard(item: ReceiptItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (item.name.isNotEmpty()) item.name else "Item",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                item.totalPrice?.let { price ->
                    Text(
                        text = price.toCurrencyString(),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            if (item.quantity != null) {
                Spacer4()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = item.quantity.toWeightString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    item.unitPrice?.let { price ->
                        Text(
                            text = price.toUnitPriceString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePicker(
    onDismissRequest: () -> Unit,
    onDateSelected: (LocalDateTime) -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val instant = Instant.fromEpochMilliseconds(millis)
                        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
                        onDateSelected(localDateTime)
                    }
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}
