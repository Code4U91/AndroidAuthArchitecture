package com.androidautharchitecture

import android.content.Intent
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
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.androidautharchitecture.domain.auth.manager.FacebookAuthManager
import com.androidautharchitecture.navigation.Destination
import com.androidautharchitecture.presentation.RootViewModel
import com.androidautharchitecture.presentation.auth.LoginScreen
import com.androidautharchitecture.presentation.main.HomeScreen
import com.androidautharchitecture.ui.theme.AndroidAuthArchitectureTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var facebookAuthManager: FacebookAuthManager

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
                            entryProvider = entryProvider {
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

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        /**
         * FACEBOOK AUTH BRIDGE:
         * Although onActivityResult is deprecated in favor of the Activity Result API,
         * the Facebook SDK (as of v18+) still internally relies on this legacy pattern 
         * to process its login results. 
         * 
         * We pass the result to the [facebookAuthManager] which delegtes it to the 
         * Facebook SDK's CallbackManager to resume the login coroutine.
         */
        facebookAuthManager.handleActivityResult(requestCode, resultCode, data)
    }
}
