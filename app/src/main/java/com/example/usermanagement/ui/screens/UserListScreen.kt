package com.example.usermanagement.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.usermanagement.R
import com.example.usermanagement.data.User
import com.example.usermanagement.ui.components.ConfirmationDialog
import com.example.usermanagement.ui.components.UserCard
import com.example.usermanagement.util.Result
import com.example.usermanagement.viewmodel.UserViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

/**
 * A composable function that displays the list of users.
 * It includes a search bar, a list of [UserCard]s, and handles user interactions like adding, editing, and deleting users.
 * It also supports pull-to-refresh to reload the user list.
 * @param viewModel The [UserViewModel] instance providing user data and handling user-related logic.
 * @param onAddClick Callback function invoked when the "Add User" button is clicked.
 * @param onEditClick Callback function invoked when a user card's "Edit" button is clicked, providing the ID of the user to edit.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListScreen(
    viewModel: UserViewModel,
    onAddClick: () -> Unit,
    onEditClick: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var userToDelete by remember { mutableStateOf<User?>(null) }
    val searchQuery by viewModel.searchQuery.collectAsState()
    var isSearchActive by remember { mutableStateOf(false) }

    // Determine if the refresh indicator should be shown based on UI state and search query.
    val isRefreshing = uiState is Result.Loading && searchQuery.isBlank()
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)

    // SwipeRefresh component enables pull-to-refresh functionality.
    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = { viewModel.refreshUsers() },
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(PaddingValues(0.dp)) // Reset padding to match MainScreen's scaffold
        ) {
            // Search input field for filtering users.
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    viewModel.updateSearchQuery(it)
                    isSearchActive = true
                },
                label = { Text(stringResource(R.string.search_hint)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        // Clear search button visible when query is not empty.
                        IconButton(onClick = {
                            viewModel.updateSearchQuery("")
                            isSearchActive = false
                        }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear search")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("search_bar")
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // Handle different UI states (Success, Error, Loading).
                when (val state = uiState) {
                    is Result.Success -> {
                        // Display message if no users are found.
                        if (state.data.isEmpty()) {
                            Text(
                                text = if (isSearchActive)
                                    stringResource(R.string.no_users_found)
                                else
                                    stringResource(R.string.no_users_found),
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(16.dp)
                                    .testTag("empty_state_message")
                            )
                        } else {
                            // Display the list of users using LazyColumn.
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(state.data) { user ->
                                    // Individual user card with edit and delete options.
                                    UserCard(
                                        user = user,
                                        onEditClick = { onEditClick(user.id) },
                                        onDeleteClick = { userToDelete = user }
                                    )
                                }
                            }
                        }
                    }

                    is Result.Error -> {
                        // Display error message.
                        Text(
                            text = state.exception.message
                                ?: stringResource(R.string.error_unknown),
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp)
                        )
                    }

                    is Result.Loading -> {
                        // Show loading indicator only when not in a refresh state from search (i.e., initial load or full refresh).
                        if (!isRefreshing) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
            }
        }
    }

    // Confirmation dialog for user deletion.
    userToDelete?.let { user ->
        ConfirmationDialog(
            message = stringResource(R.string.dialog_delete_confirmation, user.fullName),
            onConfirm = {
                viewModel.deleteUser(user)
                userToDelete = null
            },
            onDismiss = { userToDelete = null }
        )
    }
} 