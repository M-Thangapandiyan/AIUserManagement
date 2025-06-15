package com.example.usermanagement.strategy

import com.example.usermanagement.data.User
import com.example.usermanagement.repository.IUserRepository
import com.example.usermanagement.util.Result

interface UserOperationStrategy {
    /**
     * Executes a specific user operation.
     * @param user The [User] object on which the operation is to be performed.
     * @param repository The [IUserRepository] to interact with the data source.
     * @return A [Result] indicating the success or failure of the operation.
     */
    suspend fun execute(user: User, repository: IUserRepository): Result<Unit>
}

/**
 * Strategy for adding a new user.
 */
class AddUserStrategy : UserOperationStrategy {
    /**
     * Inserts the given [user] into the repository.
     * @param user The [User] to be added.
     * @param repository The [IUserRepository] to perform the insertion.
     * @return [Result.Success] if the user is added successfully, otherwise [Result.Error].
     */
    override suspend fun execute(user: User, repository: IUserRepository): Result<Unit> {
        return try {
            repository.insertUser(user)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

/**
 * Strategy for updating an existing user.
 */
class UpdateUserStrategy : UserOperationStrategy {
    /**
     * Updates the given [user] in the repository.
     * @param user The [User] to be updated.
     * @param repository The [IUserRepository] to perform the update.
     * @return [Result.Success] if the user is updated successfully, otherwise [Result.Error].
     */
    override suspend fun execute(user: User, repository: IUserRepository): Result<Unit> {
        return try {
            repository.updateUser(user)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

/**
 * Strategy for deleting a user.
 */
class DeleteUserStrategy : UserOperationStrategy {
    /**
     * Deletes the given [user] from the repository.
     * @param user The [User] to be deleted.
     * @param repository The [IUserRepository] to perform the deletion.
     * @return [Result.Success] if the user is deleted successfully, otherwise [Result.Error].
     */
    override suspend fun execute(user: User, repository: IUserRepository): Result<Unit> {
        return try {
            repository.deleteUser(user)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
} 