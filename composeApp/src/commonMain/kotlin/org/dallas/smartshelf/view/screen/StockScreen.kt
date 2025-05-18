package org.dallas.smartshelf.view.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.dallas.smartshelf.model.Product
import org.dallas.smartshelf.model.ProductCategory
import org.dallas.smartshelf.model.ProductSortOption
import org.dallas.smartshelf.view.component.InputFieldComponent
import org.dallas.smartshelf.view.component.LoadingComponent
import org.dallas.smartshelf.view.component.Spacer16
import org.dallas.smartshelf.view.component.Spacer4
import org.dallas.smartshelf.view.component.Spacer8
import org.dallas.smartshelf.view.component.dialog.ErrorDialogComponent
import org.dallas.smartshelf.theme.dimens
import org.dallas.smartshelf.util.formatToIsoDate
import org.dallas.smartshelf.viewmodel.StockViewModel

@Composable
fun StockScreen(
    viewState: StockViewModel.ViewState,
    onAction: (StockViewModel.Action) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(MaterialTheme.dimens.dp16)
    ) {
        // Search Bar
        InputFieldComponent(
            onValueChange = { onAction(StockViewModel.Action.UpdateSearchQuery(it)) },
            label = "Search Products",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer16()

        // Filter Chips
        FilterSection(
            selectedCategories = viewState.filter.categories,
            onCategoryToggle = { onAction(StockViewModel.Action.ToggleCategory(it)) }
        )

        Spacer8()

        // Sort Dropdown
        SortDropdown(
            currentSort = viewState.filter.sortBy,
            onSortSelected = { onAction(StockViewModel.Action.UpdateSortOption(it)) }
        )

        Spacer16()

        if (viewState.isLoading) {
            LoadingComponent()
        } else {
            LazyColumn {
                items(viewState.filteredProducts) { product ->
                    ProductItem(product = product)
                    Spacer8()
                }
            }
        }
    }

    viewState.error?.let {
        ErrorDialogComponent()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterSection(
    selectedCategories: Set<ProductCategory>,
    onCategoryToggle: (ProductCategory) -> Unit
) {
    Column {
        Text(
            text = "Categories",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer8()

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalArrangement = Arrangement.Center
        ) {
            ProductCategory.entries.forEach { category ->
                FilterChip(
                    modifier = Modifier.padding(end = 8.dp, bottom = 8.dp),
                    selected = selectedCategories.contains(category),
                    onClick = { onCategoryToggle(category) },
                    label = { Text(category.displayName) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortDropdown(
    currentSort: ProductSortOption,
    onSortSelected: (ProductSortOption) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            value = "Sort by: ${currentSort.displayName}",
            onValueChange = { },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            ProductSortOption.values().forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.displayName) },
                    onClick = {
                        onSortSelected(option)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

@Composable
fun ProductItem(
    product: Product,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { /* TODO: Handle click */ },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                CategoryChip(category = product.category)
            }

            Spacer8()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Quantity: ${product.quantity}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                product.expiryDate?.let { expiryDate ->
                    val textColor = when {
                        product.isExpired() -> MaterialTheme.colorScheme.error
                        product.isExpiringSoon() -> MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }

                    Text(
                        text = "Expires: ${expiryDate.formatToIsoDate()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = textColor
                    )
                }
            }

            if (product.barcode != null) {
                Spacer4()
                Text(
                    text = "Barcode: ${product.barcode}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun CategoryChip(category: ProductCategory) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = category.displayName,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

// Add this to ProductSortOption enum
val ProductSortOption.displayName: String
    get() = when (this) {
        ProductSortOption.NAME -> "Name"
        ProductSortOption.EXPIRY_DATE -> "Expiry Date"
        ProductSortOption.PURCHASE_DATE -> "Purchase Date"
        ProductSortOption.QUANTITY -> "Quantity"
    }