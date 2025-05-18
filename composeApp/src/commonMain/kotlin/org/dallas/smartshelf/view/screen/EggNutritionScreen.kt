package org.dallas.smartshelf.view.screen

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
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.dallas.smartshelf.model.EggNutrition
import org.dallas.smartshelf.model.EggType
import org.dallas.smartshelf.util.ApiResult
import org.jetbrains.compose.resources.painterResource
import smartshelf.composeapp.generated.resources.Res
import smartshelf.composeapp.generated.resources.back_icon
import smartshelf.composeapp.generated.resources.refresh_icon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EggNutritionScreen(
    onNavigateBack: () -> Unit,
    nutritionData: ApiResult<List<EggNutrition>>,
    getEggNutritionByType: (EggType) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var expanded by remember { mutableStateOf(false) }
    var selectedEggType by remember { mutableStateOf(EggType.GRADE_A_LARGE) }

    LaunchedEffect(key1 = selectedEggType) {
        getEggNutritionByType(selectedEggType)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Egg Nutrition") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(Res.drawable.back_icon),
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { getEggNutritionByType(selectedEggType) }) {
                        Icon(
                            painter = painterResource(Res.drawable.refresh_icon),
                            contentDescription = "Refresh"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Egg Type Selector
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedEggType.toString().replace("_", " "),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Egg Type") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    EggType.entries.forEach { eggType ->
                        DropdownMenuItem(
                            text = {
                                Text(eggType.toString().replace("_", " "))
                            },
                            onClick = {
                                selectedEggType = eggType
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nutrition Content
            when (val data = nutritionData) {
                is ApiResult.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is ApiResult.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
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
                            text = data.exception.message ?: "Unknown error occurred",
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                is ApiResult.Success -> {
                    NutritionContent(data.data)
                }
            }
        }
    }
}

@Composable
fun NutritionContent(nutritionList: List<EggNutrition>) {
    if (nutritionList.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No nutrition data available for this egg type",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn {
            items(nutritionList) { nutrition ->
                NutritionCard(nutrition)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun NutritionCard(nutrition: EggNutrition) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = nutrition.description,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            NutritionStat("Serving Size", "${nutrition.servingSize} ${nutrition.servingSizeUnit}")
            NutritionStat("Calories", "${nutrition.calories} kcal")
            NutritionStat("Protein", "${nutrition.protein} g")
            NutritionStat("Total Fat", "${nutrition.totalFat} g")
            NutritionStat("Cholesterol", "${nutrition.cholesterol} mg")

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Additional Nutrients",
                style = MaterialTheme.typography.titleSmall
            )

            Divider(modifier = Modifier.padding(vertical = 4.dp))

            nutrition.nutrients.entries.take(5).forEach { (name, nutrient) ->
                NutritionStat(name, "${nutrient.amount} ${nutrient.unitName}")
            }

            if (nutrition.nutrients.size > 5) {
                Text(
                    text = "... and ${nutrition.nutrients.size - 5} more nutrients",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Last Updated: ${nutrition.lastUpdated}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun NutritionStat(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}