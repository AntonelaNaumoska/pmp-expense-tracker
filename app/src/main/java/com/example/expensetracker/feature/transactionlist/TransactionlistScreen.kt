@file:OptIn(ExperimentalFoundationApi::class)

package com.example.expensetracker.feature.transactionlist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.expensetracker.R
import com.example.expensetracker.feature.add_expense.CategoryUiModel
import com.example.expensetracker.feature.add_expense.ExpenseDropDown
import com.example.expensetracker.feature.home.TransactionItem
import com.example.expensetracker.feature.home.HomeViewModel
import com.example.expensetracker.utils.Utils
import com.example.expensetracker.utils.getLocalizedCategoryName
import com.example.expensetracker.widget.ExpenseTextView

@Composable
fun TransactionListScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state = viewModel.expenses.collectAsState(initial = emptyList())
    var menuExpanded by remember { mutableStateOf(false) }

    val filterTypeOptions = remember {
        listOf(
            CategoryUiModel("All", R.string.filter_all),
            CategoryUiModel("Expense", R.string.expense),
            CategoryUiModel("Income", R.string.income)
        )
    }

    val dateRangeOptions = remember {
        listOf(
            CategoryUiModel("All Time", R.string.filter_all_time),
            CategoryUiModel("Today", R.string.filter_today),
            CategoryUiModel("Yesterday", R.string.filter_yesterday),
            CategoryUiModel("Last 30 Days", R.string.filter_30_days),
            CategoryUiModel("Last 90 Days", R.string.filter_90_days),
            CategoryUiModel("Last Year", R.string.filter_last_year)
        )
    }

    var selectedFilterType by remember { mutableStateOf(filterTypeOptions[0]) }
    var selectedDateRange by remember { mutableStateOf(dateRangeOptions[0]) }

    val filteredTransactions = when (selectedFilterType.dbKey) {
        "Expense" -> state.value.filter { it.type == "Expense" }
        "Income" -> state.value.filter { it.type == "Income" }
        else -> state.value
    }

    val currentTime = System.currentTimeMillis()

    val filteredByDateRange = filteredTransactions.filter { transaction ->
        val transactionTime = Utils.getMillisFromDate(transaction.date)

        when (selectedDateRange.dbKey) {
            "Today" -> {
                currentTime - transactionTime <= 24 * 60 * 60 * 1000L
            }

            "Yesterday" -> {
                val diff = currentTime - transactionTime
                diff in (24 * 60 * 60 * 1000L)..(48 * 60 * 60 * 1000L)
            }

            "Last 30 Days" -> {
                currentTime - transactionTime <= 30L * 24 * 60 * 60 * 1000
            }

            "Last 90 Days" -> {
                currentTime - transactionTime <= 90L * 24 * 60 * 60 * 1000
            }

            "Last Year" -> {
                currentTime - transactionTime <= 365L * 24 * 60 * 60 * 1000
            }

            else -> true
        }
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = stringResource(R.string.back_accessibility),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clickable { navController.popBackStack() },
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.Black)
                )

                ExpenseTextView(
                    text = stringResource(R.string.recent_transactions),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.Center)
                )

                Image(
                    painter = painterResource(id = R.drawable.ic_filter),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clickable { menuExpanded = !menuExpanded },
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.Black)
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    AnimatedVisibility(
                        visible = menuExpanded,
                        enter = slideInVertically(initialOffsetY = { -it / 2 }),
                        exit = slideOutVertically(targetOffsetY = { -it }),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Column {
                            ExpenseDropDown(
                                listOfItems = filterTypeOptions,
                                selectedItem = selectedFilterType,
                                onItemSelected = { selected ->
                                    selectedFilterType = selected
                                    menuExpanded = false
                                }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            ExpenseDropDown(
                                listOfItems = dateRangeOptions,
                                selectedItem = selectedDateRange,
                                onItemSelected = { selected ->
                                    selectedDateRange = selected
                                    menuExpanded = false
                                }
                            )
                        }
                    }
                }

                items(filteredByDateRange) { item ->
                    val amountText = if (item.type == "Income") item.amount else item.amount * -1

                    TransactionItem(
                        title = getLocalizedCategoryName(dbKey = item.title),
                        amount = Utils.formatCurrency(amountText),
                        isIncome= item.type == "Income",
                        date = Utils.formatStringDateToMonthDayYear(item.date),
                        color = if (item.type == "Income") Color.Green else Color.Red,
                        modifier = Modifier.animateItem(
                            fadeInSpec = null,
                            fadeOutSpec = null,
                            placementSpec = tween(100)
                        )
                    )
                }
            }
        }
    }
}