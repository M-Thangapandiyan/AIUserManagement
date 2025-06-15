package com.example.usermanagement.repository

import com.example.usermanagement.data.User
import kotlinx.coroutines.flow.Flow

interface IUserRepository {
    /**
     * A [Flow] that emits a [List] of all [User] objects from the data source.
     */
    val allUsers: Flow<List<User>>

    /**
     * Retrieves a [User] by their unique [id].
     * @param id The ID of the user to retrieve.
     * @return The [User] object if found, or `null` otherwise.
     */
    suspend fun getUserById(id: Long): User?

    /**
     * Retrieves a [User] by their unique [email].
     * @param email The email of the user to retrieve.
     * @return The [User] object if found, or `null` otherwise.
     */
    suspend fun getUserByEmail(email: String): User?

    /**
     * Inserts a new [User] into the data source.
     * @param user The [User] object to insert.
     * @return The ID of the newly inserted user.
     */
    suspend fun insertUser(user: User): Long

    /**
     * Updates an existing [User] in the data source.
     * @param user The [User] object to update.
     */
    suspend fun updateUser(user: User)

    /**
     * Deletes a [User] from the data source.
     * @param user The [User] object to delete.
     */
    suspend fun deleteUser(user: User)

    /**
     * Searches for users whose first or last name matches the given [query].
     * @param query The search string.
     * @return A [Flow] emitting a [List] of [User] objects that match the query.
     */
    fun searchUsers(query: String): Flow<List<User>>
} 