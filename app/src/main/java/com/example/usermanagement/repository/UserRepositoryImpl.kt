package com.example.usermanagement.repository

import com.example.usermanagement.data.User
import com.example.usermanagement.data.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class UserRepositoryImpl(private val userDao: UserDao) : IUserRepository {
    /**
     * Provides a [Flow] that emits a [List] of all [User] objects from the underlying data source.
     * This property directly exposes the [UserDao.getAllUsers] flow.
     */
    override val allUsers: Flow<List<User>> = userDao.getAllUsers() as Flow<List<User>>

    /**
     * Retrieves a [User] by their unique [id] from the data source.
     * The operation is performed on the IO dispatcher.
     * @param id The ID of the user to retrieve.
     * @return The [User] object if found, or `null` otherwise.
     */
    override suspend fun getUserById(id: Long): User? {
        return withContext(Dispatchers.IO) {
            userDao.getUserById(id)
        }
    }

    /**
     * Inserts a new [User] into the data source.
     * The operation is performed on the IO dispatcher.
     * @param user The [User] object to insert.
     * @return The ID of the newly inserted user.
     */
    override suspend fun insertUser(user: User): Long {
        return withContext(Dispatchers.IO) {
            userDao.insertUser(user) as Long
        }
    }

    /**
     * Updates an existing [User] in the data source.
     * The operation is performed on the IO dispatcher.
     * @param user The [User] object to update.
     */
    override suspend fun updateUser(user: User) {
        withContext(Dispatchers.IO) {
            userDao.updateUser(user)
        }
    }

    /**
     * Deletes a [User] from the data source.
     * The operation is performed on the IO dispatcher.
     * @param user The [User] object to delete.
     */
    override suspend fun deleteUser(user: User) {
        withContext(Dispatchers.IO) {
            userDao.deleteUser(user)
        }
    }

    /**
     * Searches for users whose first or last name matches the given [query].
     * The operation is performed on the IO dispatcher by the underlying DAO.
     * @param query The search string.
     * @return A [Flow] emitting a [List] of [User] objects that match the query.
     */
    override fun searchUsers(query: String): Flow<List<User>> {
        return userDao.searchUsers(query) as Flow<List<User>>
    }

    /**
     * Retrieves a [User] by their email from the data source.
     * The operation is performed on the IO dispatcher.
     * @param email The email of the user to retrieve.
     * @return The [User] object if found, or `null` otherwise.
     */
    override suspend fun getUserByEmail(email: String): User? {
        return withContext(Dispatchers.IO) {
            userDao.getUserByEmail(email)
        }
    }
} 