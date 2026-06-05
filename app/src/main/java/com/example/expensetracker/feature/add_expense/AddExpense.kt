@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.expensetracker.feature.add_expense

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.expensetracker.R
import com.example.expensetracker.base.AddExpenseNavigationEvent
import com.example.expensetracker.base.NavigationEvent
import com.example.expensetracker.data.model.ExpenseEntity
import com.example.expensetracker.ui.theme.InterFontFamily
import com.example.expensetracker.ui.theme.LightGrey
import com.example.expensetracker.ui.theme.Typography
import com.example.expensetracker.utils.Utils
import com.example.expensetracker.widget.ExpenseTextView
import java.util.Locale

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun AddExpense(
    navController: NavController,
    isIncome: Boolean,
    viewModel: AddExpenseViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val activity = context as? Activity
    val windowSizeClass = activity?.let { calculateWindowSizeClass(it) }

    val isTabletOrLandscape = windowSizeClass?.widthSizeClass != WindowWidthSizeClass.Compact

    val menuExpanded = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                NavigationEvent.NavigateBack -> navController.popBackStack()
                AddExpenseNavigationEvent.MenuOpenedClicked -> {
                    menuExpanded.value = true
                }
                else -> {}
            }
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (isTabletOrLandscape) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ActionHeaderRow(
                            isIncome = isIncome,
                            menuExpanded = menuExpanded,
                            onBackClick = { viewModel.onEvent(AddExpenseUiEvent.OnBackPressed) },
                            onMenuClick = { viewModel.onEvent(AddExpenseUiEvent.OnMenuClicked) }
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    Box(
                        modifier = Modifier
                            .weight(1.5f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        DataForm(
                            modifier = Modifier.fillMaxWidth(),
                            onAddExpenseClick = {
                                viewModel.onEvent(AddExpenseUiEvent.OnAddExpenseClicked(it))
                            },
                            isIncome = isIncome
                        )
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_topbar),
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth()
                        )
                        ActionHeaderRow(
                            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
                            isIncome = isIncome,
                            menuExpanded = menuExpanded,
                            onBackClick = { viewModel.onEvent(AddExpenseUiEvent.OnBackPressed) },
                            onMenuClick = { viewModel.onEvent(AddExpenseUiEvent.OnMenuClicked) }
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        DataForm(
                            modifier = Modifier.offset(y = (-60).dp),
                            onAddExpenseClick = {
                                viewModel.onEvent(AddExpenseUiEvent.OnAddExpenseClicked(it))
                            },
                            isIncome = isIncome
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ActionHeaderRow(
    modifier: Modifier = Modifier,
    isIncome: Boolean,
    menuExpanded: MutableState<Boolean>,
    onBackClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_back),
            contentDescription = stringResource(R.string.back_accessibility),
            modifier = Modifier.clickable { onBackClick() }
        )

        ExpenseTextView(
            text = stringResource(if (isIncome) R.string.add_income else R.string.add_expense),
            style = Typography.titleLarge,
            color = Color.Black,
            modifier = Modifier
                .padding(16.dp)
        )
    }
}

@Composable
fun DataForm(
    modifier: Modifier = Modifier,
    onAddExpenseClick: (model: ExpenseEntity) -> Unit,
    isIncome: Boolean
) {
    val amount = remember { mutableStateOf("") }
    val date = remember { mutableLongStateOf(0L) }
    val dateDialogVisibility = remember { mutableStateOf(false) }
    val type = remember { mutableStateOf(if (isIncome) "Income" else "Expense") }
    val currencySymbol = stringResource(R.string.currency_symbol)

    val categoryModels = remember(isIncome) {
        if (isIncome) {
            listOf(
                CategoryUiModel("salary", R.string.category_salary),
                CategoryUiModel("freelance", R.string.category_freelance),
                CategoryUiModel("investments", R.string.category_investments),
                CategoryUiModel("other", R.string.category_other)
            )
        } else {
            listOf(
                CategoryUiModel("grocery", R.string.category_grocery),
                CategoryUiModel("netflix", R.string.category_netflix),
                CategoryUiModel("rent", R.string.category_rent),
                CategoryUiModel("shopping", R.string.category_shopping),
                CategoryUiModel("transport", R.string.category_transport),
                CategoryUiModel("utilities", R.string.category_utilities),
                CategoryUiModel("dining_out", R.string.category_dining),
                CategoryUiModel("entertainment", R.string.category_entertainment),
                CategoryUiModel("healthcare", R.string.category_healthcare),
                CategoryUiModel("insurance", R.string.category_insurance),
                CategoryUiModel("subscriptions", R.string.category_subscriptions),
                CategoryUiModel("education", R.string.category_education),
                CategoryUiModel("debt_payments", R.string.category_debt),
                CategoryUiModel("gifts_donations", R.string.category_gifts),
                CategoryUiModel("travel", R.string.category_travel),
                CategoryUiModel("other", R.string.category_other)
            )
        }
    }

    val selectedCategory = remember(categoryModels) { mutableStateOf(categoryModels[0]) }

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(0.92f)
            .shadow(12.dp, shape = RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        TitleComponent(title = stringResource(R.string.name))

        ExpenseDropDown(
            listOfItems = categoryModels,
            selectedItem = selectedCategory.value,
            onItemSelected = { selectedCategory.value = it }
        )
        Spacer(modifier = Modifier.height(20.dp))

        TitleComponent(stringResource(R.string.amount))
        OutlinedTextField(
            value = amount.value,
            onValueChange = { newValue ->
                amount.value = newValue.filter { it.isDigit() || it == '.' }
            },
            textStyle = TextStyle(color = Color.Black),
            visualTransformation = { text ->
                val out = currencySymbol + text.text
                val currencyOffsetTranslator = object : OffsetMapping {
                    override fun originalToTransformed(offset: Int): Int = offset + currencySymbol.length
                    override fun transformedToOriginal(offset: Int): Int {
                        return if (offset >= currencySymbol.length) offset - currencySymbol.length else 0
                    }
                }
                TransformedText(AnnotatedString(out), currencyOffsetTranslator)
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            placeholder = { ExpenseTextView(text = stringResource(R.string.enter_amount)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )
        Spacer(modifier = Modifier.height(20.dp))

        TitleComponent(stringResource(R.string.date))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { dateDialogVisibility.value = true }
        ) {
            OutlinedTextField(
                value = if (date.longValue == 0L) "" else Utils.formatDateToHumanReadableForm(date.longValue),
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = Color.Black,
                    disabledTextColor = Color.Black,
                    disabledPlaceholderColor = Color.Gray
                ),
                placeholder = { ExpenseTextView(text = stringResource(R.string.select_date)) }
            )
        }
        Spacer(modifier = Modifier.height(28.dp))

        val isFormValid =
            amount.value.isNotBlank() &&
                    amount.value.toDoubleOrNull() != null &&
                    date.longValue != 0L

        Button(
            onClick = {
                val rawAmount = amount.value.toDoubleOrNull() ?: return@Button

                val currentLocale = Locale.getDefault()

                val amountInUsd = if (
                    currentLocale.language == "mk" ||
                    currentLocale.country == "MK"
                ) {
                    rawAmount / Utils.EUR_TO_MKD_RATE
                } else {
                    rawAmount
                }

                val model = ExpenseEntity(
                    id = null,
                    title = selectedCategory.value.dbKey,
                    amount = amountInUsd,
                    date = Utils.formatDateToHumanReadableForm(date.longValue),
                    type = type.value
                )

                onAddExpenseClick(model)
            },
            enabled = isFormValid,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black
            )
        ) {
            ExpenseTextView(
                text = stringResource(if (isIncome) R.string.add_income else R.string.add_expense),
                fontSize = 14.sp,
                color = Color.White,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }

    if (dateDialogVisibility.value) {
        ExpenseDatePickerDialog(
            onDateSelected = {
                date.longValue = it
                dateDialogVisibility.value = false
            },
            onDismiss = { dateDialogVisibility.value = false }
        )
    }
}

@Composable
fun ExpenseDatePickerDialog(
    onDateSelected: (date: Long) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()
    val selectedDate = datePickerState.selectedDateMillis ?: 0L
    DatePickerDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = { onDateSelected(selectedDate) }) {
                ExpenseTextView(text = stringResource(R.string.confirm), color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                ExpenseTextView(text = stringResource(R.string.cancel), color = Color.Gray)
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@Composable
fun TitleComponent(title: String) {
    ExpenseTextView(
        text = title.uppercase(),
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = LightGrey
    )
    Spacer(modifier = Modifier.size(8.dp))
}

@Composable
fun ExpenseDropDown(
    listOfItems: List<CategoryUiModel>,
    selectedItem: CategoryUiModel,
    onItemSelected: (item: CategoryUiModel) -> Unit
) {
    val expanded = remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded.value,
        onExpandedChange = { expanded.value = it }
    ) {
        OutlinedTextField(
            value = stringResource(id = selectedItem.resId),
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            textStyle = TextStyle(fontFamily = InterFontFamily, color = Color.Black),
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value) },
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black,
                unfocusedBorderColor = Color.Black,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )
        ExposedDropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false }
        ) {
            listOfItems.forEach { item ->
                DropdownMenuItem(
                    text = { ExpenseTextView(text = stringResource(id = item.resId)) },
                    onClick = {
                        onItemSelected(item)
                        expanded.value = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAddExpense() {
    AddExpense(rememberNavController(), true)
}

data class CategoryUiModel(
    val dbKey: String,
    val resId: Int
)