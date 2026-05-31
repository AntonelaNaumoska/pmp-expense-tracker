package com.example.expensetracker

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.expensetracker.feature.add_expense.AddExpense
import com.example.expensetracker.feature.home.HomeScreen
import com.example.expensetracker.feature.stats.StatsScreen
import com.example.expensetracker.feature.transactionlist.TransactionListScreen
import com.example.expensetracker.ui.theme.Zinc
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import android.app.Activity
import com.example.expensetracker.localization.LocaleManager

@Composable
fun NavHostScreen() {

    val navController = rememberNavController()

    var bottomBarVisibility by remember {
        mutableStateOf(true)
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Scaffold(
            bottomBar = {
                AnimatedVisibility(visible = bottomBarVisibility) {
                    NavigationBottomBar(
                        navController = navController,
                        items = listOf(
                            NavItem(route = "/home", icon = R.drawable.ic_home),
                            NavItem(route = "/stats", icon = R.drawable.ic_stats)
                        )
                    )
                }
            }
        ) { padding ->

            NavHost(
                navController = navController,
                startDestination = "/home",
                modifier = Modifier.padding(padding)
            ) {

                composable("/home") {
                    bottomBarVisibility = true
                    HomeScreen(navController)
                }

                composable("/add_income") {
                    bottomBarVisibility = false
                    AddExpense(navController, isIncome = true)
                }

                composable("/add_exp") {
                    bottomBarVisibility = false
                    AddExpense(navController, isIncome = false)
                }

                composable("/stats") {
                    bottomBarVisibility = true
                    StatsScreen(navController)
                }

                composable("/all_transactions") {
                    bottomBarVisibility = true
                    TransactionListScreen(navController)
                }
            }
        }

        LanguageFloatingButton()
    }
}


data class NavItem(
    val route: String,
    val icon: Int
)

@Composable
fun NavigationBottomBar(
    navController: NavController,
    items: List<NavItem>
) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    BottomAppBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(painter = painterResource(id = item.icon), contentDescription = null)
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedTextColor = Zinc,
                    selectedIconColor = Zinc,
                    unselectedTextColor = Color.Gray,
                    unselectedIconColor = Color.Gray
                )
            )
        }
    }
}

@Composable
fun LanguageFloatingButton() {

    val context = LocalContext.current
    val lang = LocaleManager.currentLanguage.value

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .size(48.dp)
                .background(Zinc, RoundedCornerShape(12.dp))
                .clickable {

                    val newLang = if (lang == "en") "mk" else "en"

                    LocaleManager.setLanguage(context, newLang)

                    (context as Activity).recreate()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = lang.uppercase(),
                color = Color.White
            )
        }
    }
}
