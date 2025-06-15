package com.example.usermanagement.repository

import android.content.Context
import com.example.usermanagement.database.DatabaseFactory
import com.example.usermanagement.database.RoomDatabaseFactory

interface RepositoryFactory {
    fun createUserRepository(context: Context): IUserRepository
}

class DefaultRepositoryFactory(
    private val databaseFactory: DatabaseFactory = RoomDatabaseFactory()
) : RepositoryFactory {
    override fun createUserRepository(context: Context): IUserRepository {
        val database = databaseFactory.createDatabase(context)
        return UserRepositoryImpl(database.userDao())
    }
} 