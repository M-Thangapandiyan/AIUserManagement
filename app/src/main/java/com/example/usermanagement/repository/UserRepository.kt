package com.example.usermanagement.repository

import com.example.usermanagement.data.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class UserRepository : IUserRepository {

    private val _users = MutableStateFlow<List<User>>(emptyList())

    override val allUsers: Flow<List<User>> = _users.asStateFlow()

    override suspend fun getUserById(id: Long): User? {
        return _users.value.find { it.id == id }
    }

    override suspend fun getUserByEmail(email: String): User? {
        return _users.value.find { it.email == email }
    }

    override suspend fun insertUser(user: User): Long {
        // In a real scenario, you'd assign a new ID. For a fake, a simple increment or fixed value might suffice.
        val newId = (_users.value.maxOfOrNull { it.id } ?: 0L) + 1
        _users.value = _users.value + user.copy(id = newId)
        return newId
    }

    override suspend fun updateUser(user: User) {
        _users.value = _users.value.map { if (it.id == user.id) user else it }
    }

    override suspend fun deleteUser(user: User) {
        _users.value = _users.value.filter { it.id != user.id }
    }

    override fun searchUsers(query: String): Flow<List<User>> {
        return _users.map { userList ->
            if (query.isBlank()) {
                userList
            } else {
                userList.filter {
                    it.firstName.contains(query, ignoreCase = true) ||
                            it.lastName.contains(query, ignoreCase = true) ||
                            it.email.contains(query, ignoreCase = true)
                }
            }
        }
    }

    // Helper function to set the current list of users for testing scenarios
    fun setUsers(users: List<User>) {
        _users.value = users
    }

    // Helper function to clear all users
    fun clearUsers() {
        _users.value = emptyList()
    }
} 