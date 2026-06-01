package com.example.expensetracker

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val context = LocalContext.current
    val lang = LocaleManager.currentLanguage.value

    val startDestination = remember {
        if (authViewModel.isUserLoggedIn) "/home" else "/login"
    }

    var bottomBarVisibility by remember {
        mutableStateOf(authViewModel.isUserLoggedIn)
    }


    var menuExpanded by remember { mutableStateOf(false) }

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


        if (bottomBarVisibility) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(top = 38.dp, end = 16.dp)
            ) {

                IconButton(
                    onClick = { menuExpanded = true },
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Open Menu",
                        tint = Color.Black
                    )
                }


                MaterialTheme(
                    shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(16.dp))
                ) {
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier
                            .background(Color.White)
                            .width(190.dp)
                            .padding(vertical = 4.dp)
                    ) {

                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "Language: ${lang.uppercase()}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = Color(0xFF1F2937)
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = android.R.drawable.ic_menu_mapmode),
                                    contentDescription = null,
                                    tint = Zinc,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                            onClick = {
                                menuExpanded = false
                                val newLang = if (lang == "en") "mk" else "en"
                                LocaleManager.setLanguage(context, newLang)
                                (context as Activity).recreate()
                            }
                        )


                        HorizontalDivider(
                            color = Color.Gray.copy(alpha = 0.15f),
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )


                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "Logout",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color(0xFFEF4444)
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = android.R.drawable.ic_lock_power_off),
                                    contentDescription = null,
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
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
            }
        }
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
                        modifier = Modifier.size(24.dp)
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