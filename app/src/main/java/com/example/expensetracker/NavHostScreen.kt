package com.example.expensetracker

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.expensetracker.feature.add_expense.AddExpense
import com.example.expensetracker.feature.auth.AuthViewModel
import com.example.expensetracker.feature.auth.LoginScreen
import com.example.expensetracker.feature.auth.RegisterScreen
import com.example.expensetracker.feature.home.HomeScreen
import com.example.expensetracker.feature.stats.StatsScreen
import com.example.expensetracker.feature.transactionlist.TransactionListScreen
import com.example.expensetracker.localization.LocaleManager
import com.example.expensetracker.ui.theme.Zinc

@Composable
fun NavHostScreen(
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val navController = rememberNavController()

    val startDestination = remember {
        if (authViewModel.isUserLoggedIn) "/home" else "/login"
    }

    var bottomBarVisibility by remember {
        mutableStateOf(authViewModel.isUserLoggedIn)
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
                startDestination = startDestination,
                modifier = Modifier.padding(padding)
            ) {
                composable("/login") {
                    bottomBarVisibility = false
                    LoginScreen(
                        onNavigateToHome = {
                            navController.navigate("/home") {
                                popUpTo("/login") { inclusive = true }
                            }
                        },
                        onNavigateToRegister = {
                            navController.navigate("/register")
                        }
                    )
                }

                composable("/register") {
                    bottomBarVisibility = false
                    RegisterScreen(
                        onNavigateToHome = {
                            navController.navigate("/home") {
                                popUpTo("/login") { inclusive = true }
                            }
                        },
                        onNavigateBackToLogin = {
                            navController.popBackStack()
                        }
                    )
                }

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

        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(top = 12.dp, end = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (bottomBarVisibility) {
                LogoutFloatingButton(onLogoutClicked = {
                    authViewModel.logout {
                        // Clear backstack and go straight back to login screen
                        navController.navigate("/login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                })
            }

            LanguageFloatingButton()
        }
    }
}

@Composable
fun LogoutFloatingButton(onLogoutClicked: () -> Unit) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .background(Color.Red.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
            .clickable { onLogoutClicked() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = android.R.drawable.ic_lock_power_off), // Standard Android power/logout icon
            contentDescription = "Logout",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun LanguageFloatingButton() {
    val context = LocalContext.current
    val lang = LocaleManager.currentLanguage.value

    Box(
        modifier = Modifier
            .size(44.dp)
            .background(Zinc.copy(alpha = 0.9f), RoundedCornerShape(12.dp))
            .clickable {
                val newLang = if (lang == "en") "mk" else "en"
                LocaleManager.setLanguage(context, newLang)
                (context as Activity).recreate()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = lang.uppercase(),
            color = Color.White,
            style = MaterialTheme.typography.labelLarge
        )
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
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp) // Ensures clean alignment
                    )
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