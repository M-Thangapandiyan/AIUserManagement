package com.example.usermanagement

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.usermanagement.ui.screens.MainScreen
import com.example.usermanagement.ui.theme.UserManagementTheme
import com.example.usermanagement.viewmodel.UserViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UserManagementTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val repository = (application as UserManagementApplication).repository
                    val viewModel: UserViewModel = viewModel(
                        factory = UserViewModel.UserViewModelFactory(application, repository)
                    )

                    MainScreen(
                        viewModel = viewModel,
                        onLogout = {
                            // TODO: Implement proper logout functionality (e.g., clear user session, navigate to login screen)
                            finish()
                        }
                    )
                }
            }
        }
    }
} 