package com.example.usermanagement

import android.app.Application
import com.example.usermanagement.repository.DefaultRepositoryFactory
import com.example.usermanagement.repository.IUserRepository

/**
 * Custom [Application] class for the User Management application.
 * This class is responsible for initializing and providing access to the application's repository.
 */
class UserManagementApplication : Application() {
    private val repositoryFactory = DefaultRepositoryFactory()
    
    /**
     * Lazily initialized instance of [IUserRepository].
     * This ensures that the database and repository are only created when first accessed.
     */
    val repository: IUserRepository by lazy {
        repositoryFactory.createUserRepository(this)
    }
} 