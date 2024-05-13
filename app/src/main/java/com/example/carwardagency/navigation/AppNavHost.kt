package com.example.carwardagency.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.carwardagency.models.Mechanics
import com.example.carwardagency.ui.theme.screens.home.HomeScreen
import com.example.carwardagency.ui.theme.screens.login.LoginScreen
import com.example.carwardagency.ui.theme.screens.services.AddMechanicsScreen
import com.example.carwardagency.ui.theme.screens.signup.SignupScreen
import com.example.carwardagency.ui.theme.screens.splashscreen.SplashScreen

@Composable
fun AppNavHost(modifier: Modifier =Modifier, navController: NavHostController = rememberNavController(), startDestination:String= ROUTE_LOGIN) {

    BackHandler {
        navController.popBackStack()

    }

    NavHost(
        navController = navController,
        modifier = modifier,
        startDestination = startDestination
    ) {
        composable(ROUTE_SPLASH) {
            SplashScreen(navController)
        }
        composable(ROUTE_HOME) {
            HomeScreen(navController)
        }
        composable(ROUTE_LOGIN) {
            LoginScreen(navController)
        }
        composable(ROUTE_SIGNUP) {
            SignupScreen(navController)
        }
        composable(ROUTE_MECHANICS){
            AddMechanicsScreen(navController)
        }
    }
}