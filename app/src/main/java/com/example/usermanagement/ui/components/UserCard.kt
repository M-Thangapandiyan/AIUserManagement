package com.example.usermanagement.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.usermanagement.R
import com.example.usermanagement.data.User
import java.text.SimpleDateFormat
import java.util.*

/**
 * A composable function that displays a single user's information in a card format.
 * It includes the user's full name, email, and phone number, along with action buttons for editing and deleting the user.
 * @param user The [User] data object to display.
 * @param onEditClick Callback function invoked when the "Edit" button is clicked, providing the [User] object.
 * @param onDeleteClick Callback function invoked when the "Delete" button is clicked, providing the [User] object.
 * @param modifier The [Modifier] to be applied to the card layout.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserCard(
    user: User,
    onEditClick: (User) -> Unit,
    onDeleteClick: (User) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onEditClick(user) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = user.fullName,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.testTag("user_card_name_${user.id}")
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = user.email,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = user.phone,
                style = MaterialTheme.typography.bodyMedium
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { onEditClick(user) }) {
                    Text(stringResource(R.string.button_edit))
                }
                TextButton(onClick = { onDeleteClick(user) }) {
                    Text(stringResource(R.string.button_delete))
                }
            }
        }
    }
} 