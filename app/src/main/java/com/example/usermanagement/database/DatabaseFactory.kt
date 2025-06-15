package com.example.usermanagement.database

import android.content.Context
import androidx.room.Room
import com.example.usermanagement.data.UserDatabase

interface DatabaseFactory {
    /**
     * Creates and returns an instance of [UserDatabase].
     * @param context The application context needed to build the database.
     * @return An instance of [UserDatabase].
     */
    fun createDatabase(context: Context): UserDatabase
}

/**
 * Implementation of [DatabaseFactory] that provides a Room database instance.
 */
class RoomDatabaseFactory : DatabaseFactory {
    /**
     * Creates and returns a persistent Room database instance named "user_database".
     * @param context The application context.
     * @return A Room [UserDatabase] instance.
     */
    override fun createDatabase(context: Context): UserDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            UserDatabase::class.java,
            "user_database"
        )
        .addMigrations(UserDatabase.MIGRATION_6_7)
        .build()
    }
}

/**
 * Implementation of [DatabaseFactory] that provides an in-memory Room database instance, typically for testing purposes.
 */
class InMemoryDatabaseFactory : DatabaseFactory {
    /**
     * Creates and returns an in-memory Room database instance.
     * This database is cleared when the process is killed.
     * @param context The application context.
     * @return An in-memory [UserDatabase] instance.
     */
    override fun createDatabase(context: Context): UserDatabase {
        return Room.inMemoryDatabaseBuilder(
            context.applicationContext,
            UserDatabase::class.java
        )
        .allowMainThreadQueries()
        .build()
    }
} 