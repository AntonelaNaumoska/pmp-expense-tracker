package com.example.expensetracker.feature.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.activity.ComponentActivity
import com.example.expensetracker.R
import com.example.expensetracker.base.HomeNavigationEvent
import com.example.expensetracker.base.NavigationEvent
import com.example.expensetracker.data.model.ExpenseEntity
import com.example.expensetracker.ui.theme.*
import com.example.expensetracker.utils.Utils
import com.example.expensetracker.widget.ExpenseTextView

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {
    val context = LocalContext.current

    val activity = context as? ComponentActivity
    val windowSizeClass = activity?.let { calculateWindowSizeClass(it) }

    val isExpanded = windowSizeClass?.widthSizeClass == WindowWidthSizeClass.Expanded
    val isMedium = windowSizeClass?.widthSizeClass == WindowWidthSizeClass.Medium
    val isTabletOrLandscape = isExpanded || isMedium

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                NavigationEvent.NavigateBack -> navController.popBackStack()
                HomeNavigationEvent.NavigateToSeeAll -> navController.navigate("/all_transactions")
                HomeNavigationEvent.NavigateToAddIncome -> navController.navigate("/add_income")
                HomeNavigationEvent.NavigateToAddExpense -> navController.navigate("/add_exp")
                else -> {}
            }
        }
    }

    val state = viewModel.expenses.collectAsState(initial = emptyList())
    val expense = viewModel.getTotalExpense(state.value)
    val income = viewModel.getTotalIncome(state.value)
    val balance = viewModel.getBalance(state.value)

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
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.Top
                    ) {
                        HeaderSection(userName = stringResource(R.string.user_name_placeholder))
                        Spacer(modifier = Modifier.height(16.dp))
                        CardItem(
                            modifier = Modifier.fillMaxWidth(),
                            balance = balance,
                            income = income,
                            expense = expense
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1.2f)
                            .fillMaxHeight()
                    ) {
                        TransactionList(
                            modifier = Modifier.fillMaxSize(),
                            list = state.value,
                            onSeeAllClicked = { viewModel.onEvent(HomeUiEvent.OnSeeAllClicked) }
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 100.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_topbar),
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth()
                        )

                        HeaderSection(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 20.dp),
                            userName = stringResource(R.string.user_name_placeholder)
                        )

                        CardItem(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .offset(y = 100.dp),
                            balance = balance,
                            income = income,
                            expense = expense
                        )
                    }

                    TransactionList(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        list = state.value,
                        onSeeAllClicked = { viewModel.onEvent(HomeUiEvent.OnSeeAllClicked) }
                    )
                }
            }

            MultiFloatingActionButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                onAddExpenseClicked = { viewModel.onEvent(HomeUiEvent.OnAddExpenseClicked) },
                onAddIncomeClicked = { viewModel.onEvent(HomeUiEvent.OnAddIncomeClicked) }
            )
        }
    }
}

@Composable
fun HeaderSection(modifier: Modifier = Modifier, userName: String) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            ExpenseTextView(
                text = stringResource(R.string.good_afternoon),
                style = Typography.bodyMedium,
                color = Color.White
            )
            ExpenseTextView(
                text = userName,
                style = Typography.titleLarge,
                color = Color.White
            )
        }
        Image(
            painter = painterResource(id = R.drawable.ic_notification),
            contentDescription = stringResource(R.string.notifications_accessibility)
        )
    }
}

@Composable
fun MultiFloatingActionButton(
    modifier: Modifier = Modifier,
    onAddExpenseClicked: () -> Unit,
    onAddIncomeClicked: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AnimatedVisibility(visible = expanded) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Zinc, RoundedCornerShape(12.dp))
                        .clickable { onAddIncomeClicked() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_income),
                        contentDescription = stringResource(R.string.add_income),
                        tint = Color.White
                    )
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Zinc, RoundedCornerShape(12.dp))
                        .clickable { onAddExpenseClicked() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_expense),
                        contentDescription = stringResource(R.string.add_expense),
                        tint = Color.White
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Zinc)
                .clickable { expanded = !expanded },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.ic_addbutton),
                contentDescription = stringResource(R.string.toggle_menu),
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

@Composable
fun CardItem(
    modifier: Modifier = Modifier,
    balance: String,
    income: String,
    expense: String
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(0.92f)
            .heightIn(min = 180.dp, max = 220.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Zinc)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                ExpenseTextView(
                    text = stringResource(R.string.total_balance),
                    style = Typography.titleMedium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.size(8.dp))
                ExpenseTextView(
                    text = balance,
                    style = Typography.headlineLarge,
                    color = Color.White
                )
            }
            Image(
                painter = painterResource(id = R.drawable.dots_menu),
                contentDescription = stringResource(R.string.menu_options)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            CardRowItem(
                title = stringResource(R.string.income),
                amount = income,
                imaget = R.drawable.ic_income
            )
            CardRowItem(
                title = stringResource(R.string.expense),
                amount = expense,
                imaget = R.drawable.ic_expense
            )
        }
    }
}

@Composable
fun TransactionList(
    modifier: Modifier = Modifier,
    list: List<ExpenseEntity>,
    title: String = stringResource(R.string.recent_transactions),
    onSeeAllClicked: () -> Unit
) {
    LazyColumn(
        modifier = modifier.padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                ExpenseTextView(
                    text = title,
                    style = Typography.titleLarge,
                )
                ExpenseTextView(
                    text = stringResource(R.string.see_all),
                    style = Typography.bodyMedium,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clickable { onSeeAllClicked() }
                )
            }
        }

        items(list, key = { it.id ?: 0 }) { item ->
            val icon = Utils.getItemIcon(item)
            val amount = if (item.type == "Income") item.amount else item.amount * -1

            TransactionItem(
                title = item.title,
                amount = Utils.formatCurrency(amount),
                icon = icon,
                date = Utils.formatStringDateToMonthDayYear(item.date),
                color = if (item.type == "Income") Green else Red,
                modifier = Modifier
            )
        }
    }
}

@Composable
fun TransactionItem(
    title: String,
    amount: String,
    icon: Int,
    date: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(51.dp)
            )
            Spacer(modifier = Modifier.size(12.dp))
            Column {
                ExpenseTextView(text = title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.size(4.dp))
                ExpenseTextView(text = date, fontSize = 13.sp, color = LightGrey)
            }
        }
        ExpenseTextView(
            text = amount,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.align(Alignment.CenterEnd),
            color = color
        )
    }
}

@Composable
fun CardRowItem(title: String, amount: String, imaget: Int, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = imaget),
                contentDescription = null,
            )
            Spacer(modifier = Modifier.size(8.dp))
            ExpenseTextView(text = title, style = Typography.bodyLarge, color = Color.White)
        }
        Spacer(modifier = Modifier.size(4.dp))
        ExpenseTextView(text = amount, style = Typography.titleLarge, color = Color.White)
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(rememberNavController())
}