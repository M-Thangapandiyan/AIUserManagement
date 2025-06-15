package com.example.usermanagement.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.usermanagement.data.User
import com.example.usermanagement.ui.components.UserForm
import com.example.usermanagement.util.Result
import com.example.usermanagement.viewmodel.UserViewModel
import kotlinx.coroutines.launch

/**
 * A composable function that displays a form for adding or editing a user.
 * It fetches user data if in edit mode, handles user input, validates data, and submits changes to the [UserViewModel].
 * It also displays snackbar messages for operation results.
 * @param viewModel The [UserViewModel] instance to interact with user data and operations.
 * @param userId The ID of the user to edit. If `null`, it's considered an add user operation.
 * @param onNavigateBack Callback function invoked when the user navigates back from this screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserFormScreen(
    viewModel: UserViewModel,
    userId: Long? = null,
    onNavigateBack: () -> Unit
) {
    /**
     * The mutable state holder for the [User] object being edited or created.
     * Initialized with empty values for a new user, or loaded from the ViewModel for an existing user.
     */
    var user by remember {
        mutableStateOf(
            User().apply { // Create a new User instance using the no-argument constructor
                firstName = ""
                lastName = ""
                email = ""
                phone = ""
            }
        )
    }
    var isEditMode by remember { mutableStateOf(userId != null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    /**
     * [LaunchedEffect] to load user data when [userId] changes (i.e., when entering edit mode).
     * It finds the user from the current UI state of the ViewModel and updates the local [user] state.
     */
    LaunchedEffect(userId) {
        if (userId != null) {
            // Find the user in the current list
            val currentUser = viewModel.uiState.value.let { state ->
                when (state) {
                    is Result.Success -> state.data.find { it.id == userId }
                    else -> null
                }
            }
            currentUser?.let { user = it }
        }
    }

    /**
     * [LaunchedEffect] to observe the [viewModel.uiState] for operation results (errors).
     * Displays a snackbar message if an error occurs during user operations.
     */
    LaunchedEffect(viewModel.uiState) {
        viewModel.uiState.collect { state ->
            when (state) {
                is Result.Error -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = state.exception.message ?: "An unknown error occurred",
                            actionLabel = "Dismiss",
                            duration = SnackbarDuration.Long
                        )
                    }
                }

                is Result.Success -> {
                    // If it's a success after an add/update operation, navigate back.
                    // We need a more precise way to know if this success is from a form submission
                    // versus a background user list update.
                    // For now, we'll let onSubmit handle navigation if successful.
                }

                else -> { /* Loading state, no action needed here */
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit User" else "Add User", modifier = Modifier.testTag("user_form_title")) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .testTag("user_details_screen")
        ) {
            UserForm(
                initialState = user,
                onUserChange = { user = it },
                onSubmit = {
                    scope.launch {
                        if (isEditMode) {
                            viewModel.updateUser(user)
                        } else {
                            viewModel.addUser(user)
                        }
                        onNavigateBack()
                    }
                }
            )
        }
    }
} 