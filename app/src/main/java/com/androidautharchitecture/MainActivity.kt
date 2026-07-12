package com.androidautharchitecture

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.androidautharchitecture.navigation.Destination
import com.androidautharchitecture.presentation.RootViewModel
import com.androidautharchitecture.presentation.auth.LoginScreen
import com.androidautharchitecture.presentation.main.HomeScreen
import com.androidautharchitecture.ui.theme.AndroidAuthArchitectureTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: RootViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidAuthArchitectureTheme {
                val isSessionExists by viewModel.isSessionExists.collectAsState()

                // Use 'key' to force re-creation of the navigation state when session status changes
                key(isSessionExists) {
                    val startRoute = if (isSessionExists) Destination.Home else Destination.Login
                    val backStack = rememberNavBackStack(startRoute)


                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        NavDisplay(
                            backStack = backStack,
                            modifier = Modifier.padding(innerPadding),
                            onBack = {
                                if (backStack.size > 1) {
                                    backStack.removeAt(backStack.size - 1)
                                }
                            },
                            entryDecorators = listOf(
                                // handles rememberSavable + state across config changes
                                rememberSaveableStateHolderNavEntryDecorator(),

                                // scopes viewmodel() to each NavEntry
                                rememberViewModelStoreNavEntryDecorator()
                            ),
                            entryProvider = entryProvider<NavKey> {
                                entry<Destination.Login> {
                                    LoginScreen()
                                }
                                entry<Destination.Home> {
                                    HomeScreen()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
