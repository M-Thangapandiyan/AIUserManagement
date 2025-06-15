package com.example.usermanagement.ui.state

import com.example.usermanagement.data.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class UserUiState {
    object Loading : UserUiState()
    data class Success(val users: List<User>) : UserUiState()
    data class Error(val message: String) : UserUiState()
}

class UserStateHolder {
    private val _uiState = MutableStateFlow<UserUiState>(UserUiState.Loading)
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()

    fun updateUsers(users: List<User>) {
        _uiState.value = UserUiState.Success(users)
    }

    fun setError(message: String) {
        _uiState.value = UserUiState.Error(message)
    }

    fun setLoading() {
        _uiState.value = UserUiState.Loading
    }
} 