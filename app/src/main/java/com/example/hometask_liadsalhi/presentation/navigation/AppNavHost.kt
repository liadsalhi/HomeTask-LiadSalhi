package com.example.hometask_liadsalhi.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hometask_liadsalhi.presentation.convert.ConvertRoute
import com.example.hometask_liadsalhi.presentation.payment.PaymentRoute
import com.example.hometask_liadsalhi.presentation.receipt.ReceiptRoute
import com.example.hometask_liadsalhi.presentation.settings.SettingsRoute

// the names of the screens in the app
object Routes {
    const val PAYMENT = "payment"
    const val SETTINGS = "settings"
    const val RECEIPT = "receipt"
    const val CONVERT = "convert"
}

// the map of the app: which screens exist and how to move between them
@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.PAYMENT) {

        composable(Routes.PAYMENT) {
            PaymentRoute(
                onOpenSettings = {
                    navController.navigate(Routes.SETTINGS) { launchSingleTop = true }
                },
                onPaymentCreated = {
                    navController.navigate(Routes.RECEIPT) { launchSingleTop = true }
                }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsRoute(
                onBack = { navController.navigateUp() }
            )
        }

        composable(Routes.RECEIPT) {
            ReceiptRoute(
                onConvert = {
                    navController.navigate(Routes.CONVERT) { launchSingleTop = true }
                },
                onFinish = {
                    // remove the old form and the receipt, then open a new clean form
                    navController.navigate(Routes.PAYMENT) {
                        popUpTo(Routes.PAYMENT) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.CONVERT) {
            ConvertRoute(
                onBack = { navController.navigateUp() }
            )
        }
    }
}