package com.example.usermanagement.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.usermanagement.R
import com.example.usermanagement.data.User
import com.example.usermanagement.util.ValidationUtils
import kotlinx.coroutines.launch

data class FormState(
    val user: User = User(),
    val errors: Map<String, String?> = emptyMap(),
    val isLoading: Boolean = false
)

enum class UserField {
    FIRST_NAME, LAST_NAME, EMAIL, PHONE
}

/**
 * A composable function that displays a form for creating or editing user details.
 * It includes input fields for first name, last name, email, and phone number, along with validation feedback.
 * @param initialState The initial state of the form, including the user data and any existing errors.
 * @param onUserChange Callback function invoked when any user field is changed, providing the updated [User] object.
 * @param onSubmit Callback function invoked when the form is submitted (e.g., Save button clicked),
 *   but only if all form validations pass.
 * @param modifier The [Modifier] to be applied to the layout.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserForm(
    initialState: User = User(),
    onUserChange: (User) -> Unit,
    onSubmit: suspend () -> Unit,
    modifier: Modifier = Modifier
) {
    var formState by remember { mutableStateOf(FormState(user = initialState)) }
    val scope = rememberCoroutineScope()
    
    fun validateField(field: UserField, value: String): String? {
        return when (field) {
            UserField.FIRST_NAME -> if (ValidationUtils.isFieldBlank(value)) 
                "First name is required" else null
            UserField.LAST_NAME -> if (ValidationUtils.isFieldBlank(value)) 
                "Last name is required" else null
            UserField.EMAIL -> when {
                ValidationUtils.isFieldBlank(value) -> "Email is required"
                !ValidationUtils.isValidEmail(value) -> "Invalid email format"
                else -> null
            }
            UserField.PHONE -> when {
                ValidationUtils.isFieldBlank(value) -> "Phone is required"
                !ValidationUtils.isValidPhone(value) -> "Invalid phone format"
                else -> null
            }
        }
    }

    fun updateField(field: UserField, value: String) {
        val updatedUser = when (field) {
            UserField.FIRST_NAME -> formState.user.copy(firstName = value)
            UserField.LAST_NAME -> formState.user.copy(lastName = value)
            UserField.EMAIL -> formState.user.copy(email = value)
            UserField.PHONE -> formState.user.copy(phone = value)
        }
        
        val errors = validateField(field, value)
        formState = formState.copy(
            user = updatedUser,
            errors = formState.errors + (field.name to errors)
        )
        onUserChange(updatedUser)
    }

    fun validateForm(): Boolean {
        val errors = UserField.values().associateWith { field ->
            validateField(field, when (field) {
                UserField.FIRST_NAME -> formState.user.firstName
                UserField.LAST_NAME -> formState.user.lastName
                UserField.EMAIL -> formState.user.email
                UserField.PHONE -> formState.user.phone
            })
        }
        formState = formState.copy(errors = errors.mapKeys { it.key.name })
        return errors.values.all { it == null }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = formState.user.firstName,
            onValueChange = { updateField(UserField.FIRST_NAME, it) },
            label = { Text(stringResource(R.string.label_first_name)) },
            isError = formState.errors[UserField.FIRST_NAME.name] != null,
            supportingText = { formState.errors[UserField.FIRST_NAME.name]?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = formState.user.lastName,
            onValueChange = { updateField(UserField.LAST_NAME, it) },
            label = { Text(stringResource(R.string.label_last_name)) },
            isError = formState.errors[UserField.LAST_NAME.name] != null,
            supportingText = { formState.errors[UserField.LAST_NAME.name]?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = formState.user.email,
            onValueChange = { updateField(UserField.EMAIL, it) },
            label = { Text(stringResource(R.string.label_email)) },
            isError = formState.errors[UserField.EMAIL.name] != null,
            supportingText = { formState.errors[UserField.EMAIL.name]?.let { Text(it) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = formState.user.phone,
            onValueChange = { updateField(UserField.PHONE, it) },
            label = { Text(stringResource(R.string.label_phone)) },
            isError = formState.errors[UserField.PHONE.name] != null,
            supportingText = { formState.errors[UserField.PHONE.name]?.let { Text(it) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (validateForm()) {
                    formState = formState.copy(isLoading = true)
                    scope.launch {
                        onSubmit()
                    }
                }
            },
            enabled = !formState.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (formState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(stringResource(R.string.button_save))
            }
        }
    }
}