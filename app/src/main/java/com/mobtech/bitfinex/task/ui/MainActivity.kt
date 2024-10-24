package com.mobtech.bitfinex.task.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mobtech.bitfinex.task.ui.screen.tickers.TickersRoute
import com.mobtech.bitfinex.task.ui.theme.BitfinextaskTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BitfinextaskTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BitfinexNavHost(modifier = Modifier.padding(innerPadding), navController = rememberNavController())
                }
            }
        }
    }
}

@Composable
fun BitfinexNavHost(
    modifier: Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.Tickers.route
) {
    NavHost(modifier = modifier, navController = navController, startDestination = startDestination) {
        composable(Routes.Tickers.route) {
            TickersRoute(navController = navController) // Market screen route
        }
        // Add more routes as needed
    }
}