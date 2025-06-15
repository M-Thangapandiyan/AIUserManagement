package com.example.usermanagement.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.usermanagement.R
import com.example.usermanagement.ui.components.AppDrawer
import com.example.usermanagement.ui.components.UserFilterDialog
import com.example.usermanagement.viewmodel.UserViewModel
import kotlinx.coroutines.launch

/**
 * A composable function that represents the main screen of the User Management application.
 * It provides a [ModalNavigationDrawer] for global navigation (e.g., Logout, Add User),
 * a [TopAppBar] with a menu icon to open the drawer, and manages the navigation between different user-related screens
 * like [UserListScreen] and [UserFormScreen].
 * @param viewModel The [UserViewModel] instance, providing user data and handling user-related logic.
 * @param onLogout Callback function invoked when the 'Logout' action is triggered from the drawer.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: UserViewModel,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                onLogout = onLogout,
                onAddUser = {
                    scope.launch {
                        drawerState.close()
                        navController.navigate("addUser")
                    }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("User Management", modifier = Modifier.testTag("main_screen_title")) },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    if (drawerState.isClosed) drawerState.open() else drawerState.close()
                                }
                            }
                        ) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },  
                    actions = {
                        IconButton(
                            onClick = {
                                UserFilterDialog(context, viewModel).show()
                            },
                            modifier = Modifier.testTag("filter_button")
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_filter),
                                contentDescription = "Filter"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = "userList",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                composable("userList") {
                    UserListScreen(
                        viewModel = viewModel,
                        onAddClick = { navController.navigate("addUser") },
                        onEditClick = { userId ->
                            navController.navigate("editUser/$userId")
                        }
                    )
                }
                composable("addUser") {
                    UserFormScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable("editUser/{userId}") { backStackEntry ->
                    val userId = backStackEntry.arguments?.getString("userId")?.toLongOrNull()
                    if (userId != null) {
                        UserFormScreen(
                            viewModel = viewModel,
                            userId = userId,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}

