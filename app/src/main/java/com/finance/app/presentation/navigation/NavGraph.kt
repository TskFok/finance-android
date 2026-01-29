package com.finance.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.finance.app.presentation.screen.auth.LoginScreen
import com.finance.app.presentation.screen.home.HomeScreen
import com.finance.app.presentation.viewmodel.AuthViewModel

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val authViewModel = remember { AuthViewModel() }
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    
    // 根据登录状态导航
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn && navController.currentDestination?.route != "home") {
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        } else if (!isLoggedIn && navController.currentDestination?.route != "login") {
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        }
    }
    
    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) "home" else "login"
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }
        
        composable("register") {
            // RegisterScreen()
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }
        
        composable("home") {
            HomeScreen(
                onLogout = {
                    authViewModel.logout()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                authViewModel = authViewModel
            )
        }
    }
}
