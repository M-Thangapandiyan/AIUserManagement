package com.example.usermanagement.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.usermanagement.data.User
import com.example.usermanagement.filter.UserFilter
import com.example.usermanagement.repository.IUserRepository
import com.example.usermanagement.strategy.*
import com.example.usermanagement.util.Result
import com.example.usermanagement.util.ValidationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.Flow

/**
 * [UserViewModel] manages the UI-related data and logic for user operations.
 * It interacts with the [IUserRepository] to fetch, add, update, and delete user data,
 * and exposes the UI state through [StateFlow]s.
 * @param application The application instance.
 * @param repository The repository responsible for user data operations.
 */
open class UserViewModel(
    application: Application,
    private val repository: IUserRepository
) : AndroidViewModel(application) {
    /**
     * [StateFlow] representing the current UI state of the user list.
     * It emits [Result]s, indicating loading, success with a list of users, or error.
     */
    private val _uiState = MutableStateFlow<Result<List<User>>>(Result.loading())
    val uiState: StateFlow<Result<List<User>>> = _uiState.asStateFlow()

    /**
     * [MutableStateFlow] holding the current search query string.
     */
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val validationStrategy = UserValidationStrategy(application)

    private val _filteredUsers = MutableStateFlow<List<User>>(emptyList())
    val filteredUsers: StateFlow<List<User>> = _filteredUsers.asStateFlow()

    private var currentFilterFirstName: String? = null

    init {
        // Initialize the user list and start observing search queries.
        loadUsers()
        observeSearch()
        viewModelScope.launch {
            repository.allUsers.collect { users ->
                _uiState.value = Result.Success(users)
                applyFilters(users)
            }
        }
    }

    /**
     * Fetches users from the provided [Flow] and updates the [_uiState].
     * This is a generalized function to reduce duplication in data fetching logic.
     * @param usersFlow The [Flow] of user lists to collect from (e.g., all users or search results).
     */
    private fun _fetchUsers(usersFlow: Flow<List<User>>) {
        viewModelScope.launch {
            try {
                _uiState.value = Result.loading()
                usersFlow.collect { userList ->
                    _uiState.value = Result.success(userList)
                }
            } catch (e: Exception) {
                _uiState.value = Result.error(e)
            }
        }
    }

    /**
     * Loads all users from the repository and updates the UI state.
     */
    private fun loadUsers() {
        _fetchUsers(repository.allUsers)
    }

    /**
     * Observes changes in the [_searchQuery] and triggers a user search after a debounce period.
     * If the query is blank, it loads all users; otherwise, it searches for matching users.
     */
    private fun observeSearch() {
        viewModelScope.launch {
            searchQuery
                .debounce(300)
                .collect { query ->
                    if (query.isBlank()) {
                        _fetchUsers(repository.allUsers)
                    } else {
                        _fetchUsers(repository.searchUsers(query))
                    }
                }
        }
    }

    /**
     * Updates the current search query.
     * @param query The new search query string.
     */
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    /**
     * Refreshes the list of all users. This is typically called for pull-to-refresh functionality.
     */
    fun refreshUsers() {
        loadUsers()
    }

    /**
     * Adds a new user to the data source after validating their details.
     * @param user The [User] object to add.
     */
    fun addUser(user: User) {
        performUserOperation(user, AddUserStrategy()) {
            repository.insertUser(it)
            loadUsers()
        }
    }

    /**
     * Updates an existing user in the data source after validating their details.
     * @param user The [User] object to update.
     */
    fun updateUser(user: User) {
        performUserOperation(user, UpdateUserStrategy()) {
            repository.updateUser(it)
            loadUsers()
        }
    }

    /**
     * Deletes a user from the data source.
     * Validation is skipped for delete operations.
     * @param user The [User] object to delete.
     */
    fun deleteUser(user: User) {
        performUserOperation(user, DeleteUserStrategy(), performValidation = false) {
            repository.deleteUser(it)
            loadUsers()
        }
    }

    /**
     * A generic function to perform user operations (add, update, delete) with optional validation.
     * @param user The [User] object involved in the operation.
     * @param strategy The [UserOperationStrategy] to execute the specific operation.
     * @param performValidation A boolean indicating whether to perform validation before the operation. Defaults to `true`.
     * @param onSuccess A suspending lambda to be executed upon successful completion of the operation, receiving the [User] object.
     */
    private fun performUserOperation(
        user: User,
        strategy: UserOperationStrategy,
        performValidation: Boolean = true,
        onSuccess: suspend (User) -> Unit
    ) {
        viewModelScope.launch {
            try {
                if (performValidation) {
                    val validationResult = validationStrategy.validate(user)
                    if (!validationResult.isValid()) {
                        _uiState.value = Result.error(Exception(validationResult.getErrorMessage()))
                        return@launch
                    }
                    // Duplicate email check (only for add operation)
                    if (strategy is AddUserStrategy) {
                        val existingUser = repository.getUserByEmail(user.email)
                        if (existingUser != null) {
                            _uiState.value = Result.error(Exception("A user with this email already exists."))
                            return@launch
                        }
                    }
                }

                when (val result = strategy.execute(user, repository)) {
                    is Result.Success -> {
                        onSuccess(user)
                    }
                    is Result.Error -> {
                        _uiState.value = Result.error(result.exception)
                    }
                    Result.Loading -> { /* Should not happen for one-shot operations */ }
                }
            } catch (e: Exception) {
                _uiState.value = Result.error(e)
            }
        }
    }

    /**
     * Applies filters to the user list
     * @param users The list of users to filter
     */
    private fun applyFilters(users: List<User>) {
        _filteredUsers.value = if (currentFilterFirstName.isNullOrEmpty()) {
            users
        } else {
            UserFilter.filterByFirstName(users, currentFilterFirstName!!)
        }
    }

    /**
     * Sets the first name filter
     * @param firstName The first name to filter by
     */
    fun setFirstNameFilter(firstName: String?) {
        currentFilterFirstName = firstName
        _uiState.value.let { state ->
            if (state is Result.Success) {
                applyFilters(state.data)
            }
        }
    }

    /**
     * Clears all filters
     */
    fun clearFilters() {
        currentFilterFirstName = null
        _uiState.value.let { state ->
            if (state is Result.Success) {
                applyFilters(state.data)
            }
        }
    }

    /**
     * Factory for creating instances of [UserViewModel].
     * @param application The application instance.
     * @param repository The repository for user data.
     */
    class UserViewModelFactory(
        private val application: Application,
        private val repository: IUserRepository
    ) : ViewModelProvider.Factory {
        /**
         * Creates a new instance of the given [modelClass].
         * @param modelClass The class of the ViewModel to create.
         * @param <T> The type of the ViewModel.
         * @return A new instance of the ViewModel.
         * @throws IllegalArgumentException if the `modelClass` is not assignable from [UserViewModel].
         */
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return UserViewModel(application, repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

