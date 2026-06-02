package com.example.expensetracker.feature.home

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.activity.ComponentActivity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.navigation.compose.rememberNavController
import com.example.expensetracker.R
import com.example.expensetracker.base.HomeNavigationEvent
import com.example.expensetracker.base.NavigationEvent
import com.example.expensetracker.data.model.ExpenseEntity
import com.example.expensetracker.feature.auth.AuthViewModel
import com.example.expensetracker.localization.LocaleManager
import com.example.expensetracker.ui.theme.*
import com.example.expensetracker.utils.Utils
import com.example.expensetracker.utils.getLocalizedCategoryName
import com.example.expensetracker.widget.ExpenseTextView
import com.example.expensetracker.widget.TransactionIcon
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val activity = context as? ComponentActivity
    var menuExpanded by remember { mutableStateOf(false) }

    val lang = LocaleManager.currentLanguage.value

    val user = FirebaseAuth.getInstance().currentUser
    val userName = user?.displayName ?: "User"

    val state = viewModel.expenses.collectAsState(initial = emptyList())

    val formattedBalance = Utils.formatCurrency(viewModel.getBalance(state.value))
    val formattedIncome = Utils.formatCurrency(viewModel.getTotalIncome(state.value))
    val formattedExpense = Utils.formatCurrency(viewModel.getTotalExpense(state.value))

    val windowSizeClass = activity?.let { calculateWindowSizeClass(it) }
    val isTabletOrLandscape =
        windowSizeClass?.widthSizeClass == WindowWidthSizeClass.Expanded ||
                windowSizeClass?.widthSizeClass == WindowWidthSizeClass.Medium

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

    Box(modifier = Modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(12.dp, 0.dp)
                .zIndex(10f)
        ) {

            IconButton(
                onClick = { menuExpanded = true },
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Menu",
                    tint = Color.White
                )
            }

            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false }
            ) {

                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(
                                R.string.language_label,
                                lang.uppercase()
                            )
                        )
                    },
                    onClick = {
                        menuExpanded = false
                        val newLang = if (lang == "en") "mk" else "en"
                        LocaleManager.setLanguage(context, newLang)
                        (context as Activity).recreate()
                    }
                )

                HorizontalDivider()

                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(R.string.logout),
                            color = Color.Red
                        )
                    },
                    onClick = {
                        menuExpanded = false
                        authViewModel.logout {
                            navController.navigate("/login") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                )
            }
        }

        if (isTabletOrLandscape) {

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    HeaderSection(userName = userName)

                    Spacer(Modifier.height(16.dp))

                    CardItem(
                        balance = formattedBalance,
                        income = formattedIncome,
                        expense = formattedExpense
                    )
                }

                TransactionList(
                    modifier = Modifier.weight(1f),
                    list = state.value,
                    onSeeAllClicked = {
                        navController.navigate("/all_transactions")
                    }
                )
            }

        } else {
            Column(modifier = Modifier.fillMaxSize()) {

                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.ic_topbar),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth()
                    )

                    HeaderSection(
                        modifier = Modifier.padding(24.dp),
                        userName = userName
                    )

                    CardItem(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = 80.dp),
                        balance = formattedBalance,
                        income = formattedIncome,
                        expense = formattedExpense
                    )
                }

                Spacer(modifier = Modifier.height(90.dp))

                TransactionList(
                    modifier = Modifier.weight(1f),
                    list = state.value,
                    onSeeAllClicked = {
                        navController.navigate("/all_transactions")
                    }
                )
            }
        }

        MultiFloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            onAddExpenseClicked = { navController.navigate("/add_exp") },
            onAddIncomeClicked = { navController.navigate("/add_income") }
        )
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
                text = stringResource(R.string.hello),
                style = Typography.bodyMedium,
                color = Color.White
            )
            ExpenseTextView(
                text = userName,
                style = Typography.titleLarge,
                color = Color.White
            )
        }


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

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Zinc)
                        .clickable { onAddIncomeClicked() }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_expense),
                        contentDescription = stringResource(R.string.add_income),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    ExpenseTextView(
                        text = stringResource(R.string.income),
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Zinc)
                        .clickable { onAddExpenseClicked() }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_income),
                        contentDescription = stringResource(R.string.add_expense),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    ExpenseTextView(
                        text = stringResource(R.string.expense),
                        color = Color.White,
                        fontSize = 14.sp
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

        items(list) { item ->
            val amount = if (item.type == "Income") item.amount else item.amount * -1

            TransactionItem(
                title = getLocalizedCategoryName(dbKey = item.title),
                amount = Utils.formatCurrency(amount),
                isIncome = item.type == "Income",
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
    isIncome: Boolean,
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

            TransactionIcon(isIncome = isIncome)

            Spacer(modifier = Modifier.size(12.dp))

            Column {
                ExpenseTextView(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.size(4.dp))
                ExpenseTextView(
                    text = date,
                    fontSize = 13.sp,
                    color = LightGrey
                )
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