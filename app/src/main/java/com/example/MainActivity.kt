package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.repository.HavenRepository
import com.example.ui.HavenApp
import com.example.ui.MainNavTab
import com.example.ui.screens.LoginScreen
import com.example.ui.theme.ProjectXTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProjectXTheme {
                val repository = remember { HavenRepository(applicationContext) }
                val navController = rememberNavController()

                Surface(modifier = Modifier.fillMaxSize()) {
                    NavHost(
                        navController = navController,
                        startDestination = "home"
                    ) {
                        composable("login") {
                            LoginScreen(
                                repository = repository,
                                onLoginSuccess = {
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onContinueAsGuest = {
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("home") {
                            HavenApp(
                                repository = repository,
                                onNavigateToLogin = {
                                    navController.navigate("login")
                                },
                                initialTab = MainNavTab.EXPLORE
                            )
                        }

                        composable("dashboard") {
                            HavenApp(
                                repository = repository,
                                onNavigateToLogin = {
                                    navController.navigate("login")
                                },
                                initialTab = MainNavTab.DASHBOARD
                            )
                        }
                    }
                }
            }
        }
    }
}
