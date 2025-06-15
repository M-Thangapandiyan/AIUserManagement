package com.example.usermanagement.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

/**
 * A composable function that represents the application's navigation drawer content.
 * It provides options for user actions like adding a user and logging out.
 * @param onLogout Callback function invoked when the 'Logout' button is clicked.
 * @param onAddUser Callback function invoked when the 'Add User' button is clicked.
 * @param modifier The [Modifier] to be applied to the layout.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDrawer(
    onLogout: () -> Unit,
    onAddUser: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(modifier = modifier) {
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "User Management",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.headlineSmall
        )
        Divider()
        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Add, contentDescription = null) },
            label = { Text("Add User", modifier = Modifier.testTag("drawer_add_user_button_text")) },
            selected = false,
            onClick = onAddUser,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        NavigationDrawerItem(
            icon = { Icon(Icons.Default.ExitToApp, contentDescription = null) },
            label = { Text("Logout", modifier = Modifier.testTag("drawer_logout_button_text")) },
            selected = false,
            onClick = onLogout,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
} 