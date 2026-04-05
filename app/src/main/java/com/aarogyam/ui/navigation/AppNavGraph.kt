package com.aarogyam.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.aarogyam.ui.entry.WeightEntryScreen
import com.aarogyam.ui.goal.GoalScreen
import com.aarogyam.ui.history.HistoryScreen
import com.aarogyam.ui.theme.Amber400

sealed class Screen(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val iconOutlined: ImageVector
) {
    object Entry : Screen("entry", "Log", Icons.Default.Add, Icons.Outlined.Add)
    object History : Screen("history", "History", Icons.Default.List, Icons.Outlined.List)
    object Goal : Screen("goal", "Goal", Icons.Default.Star, Icons.Outlined.Star)
}

private val bottomNavItems = listOf(Screen.Entry, Screen.History, Screen.Goal)

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                if (currentRoute == screen.route) screen.icon else screen.iconOutlined,
                                contentDescription = screen.label
                            )
                        },
                        label = { Text(screen.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Amber400,
                            selectedTextColor = Amber400,
                            indicatorColor = Amber400.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Entry.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Entry.route) { WeightEntryScreen() }
            composable(Screen.History.route) { HistoryScreen() }
            composable(Screen.Goal.route) { GoalScreen() }
        }
    }
}
