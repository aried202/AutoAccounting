package com.autoaccounting.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.autoaccounting.feature.addtransaction.AddTransactionScreen
import com.autoaccounting.feature.bills.BillsScreen
import com.autoaccounting.feature.home.HomeScreen
import com.autoaccounting.feature.profile.ProfileScreen
import com.autoaccounting.feature.statistics.StatisticsScreen
import com.autoaccounting.ui.theme.CardWhite
import com.autoaccounting.ui.theme.PageBackground
import com.autoaccounting.ui.theme.PrimaryGreen
import com.autoaccounting.ui.theme.TextSecondary

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Statistics : Screen("statistics")
    data object AddTransaction : Screen("add_transaction")
    data object Bills : Screen("bills")
    data object Profile : Screen("profile")
    data object EditTransaction : Screen("edit_transaction/{transactionId}") {
        fun createRoute(transactionId: Long) = "edit_transaction/$transactionId"
    }
}

data class NavItem(
    val screen: Screen,
    val title: String,
    val icon: ImageVector,
    val isCenter: Boolean = false
)

val navItems = listOf(
    NavItem(Screen.Home, "首页", Icons.Default.Home),
    NavItem(Screen.Statistics, "统计", Icons.Default.BarChart),
    NavItem(Screen.AddTransaction, "记账", Icons.Default.Receipt, isCenter = true),
    NavItem(Screen.Bills, "账单", Icons.Default.Description),
    NavItem(Screen.Profile, "我的", Icons.Default.Person)
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = navItems.any { it.screen.route == currentRoute }

    Scaffold(
        containerColor = PageBackground,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = CardWhite,
                    tonalElevation = 8.dp
                ) {
                    navItems.forEach { item ->
                        if (item.isCenter) {
                            NavigationBarItem(
                                icon = {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(PrimaryGreen),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            item.icon,
                                            contentDescription = item.title,
                                            tint = CardWhite,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                },
                                label = { Text(item.title, style = MaterialTheme.typography.labelSmall) },
                                selected = false,
                                onClick = {
                                    navController.navigate(item.screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = CardWhite
                                )
                            )
                        } else {
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        item.icon,
                                        contentDescription = item.title,
                                        modifier = Modifier.size(24.dp)
                                    )
                                },
                                label = { Text(item.title, style = MaterialTheme.typography.labelSmall) },
                                selected = currentRoute == item.screen.route,
                                onClick = {
                                    navController.navigate(item.screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = PrimaryGreen,
                                    selectedTextColor = PrimaryGreen,
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextSecondary,
                                    indicatorColor = PrimaryGreen.copy(alpha = 0.1f)
                                )
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onAddTransaction = {
                        navController.navigate(Screen.AddTransaction.route)
                    },
                    onNavigateToBills = {
                        navController.navigate(Screen.Bills.route)
                    }
                )
            }

            composable(Screen.AddTransaction.route) {
                AddTransactionScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.EditTransaction.route,
                arguments = listOf(navArgument("transactionId") { type = NavType.LongType })
            ) {
                AddTransactionScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Statistics.route) {
                StatisticsScreen()
            }

            composable(Screen.Bills.route) {
                BillsScreen()
            }

            composable(Screen.Profile.route) {
                ProfileScreen()
            }
        }
    }
}
