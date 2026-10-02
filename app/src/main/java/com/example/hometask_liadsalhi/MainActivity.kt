package com.example.hometask_liadsalhi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.hometask_liadsalhi.presentation.navigation.AppNavHost
import com.example.hometask_liadsalhi.ui.theme.HomeTaskLiadSalhiTheme

//  shows the theme and the navigation, everything else is compose screens
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HomeTaskLiadSalhiTheme {
                AppNavHost()
            }
        }
    }
}