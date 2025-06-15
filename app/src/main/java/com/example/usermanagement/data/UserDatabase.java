package com.example.usermanagement.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * Main database class for the application
 */
@Database(entities = {User.class}, version = 7, exportSchema = false)
public abstract class UserDatabase extends RoomDatabase {
    private static volatile UserDatabase INSTANCE;

    /**
     * Provides the Data Access Object for the User entity.
     * @return An instance of {@link UserDao}.
     */
    public abstract UserDao userDao();

    /**
     * Returns the singleton instance of the UserDatabase.
     * If the instance does not exist, it creates one using Room's database builder.
     * @param context The application context.
     * @return The singleton instance of UserDatabase.
     */
    public static UserDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (UserDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            UserDatabase.class,
                            "user_database"
                        )
                        .addMigrations(MIGRATION_6_7)
                        .build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Defines a database migration from version 5 to 6.
     * This migration currently uses destructive migration for simplicity. For production apps with data persistence needs,
     * actual schema alteration statements should be added here if schema changes occurred between versions.
     */
    static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Since fallbackToDestructiveMigration() is used, this migration
            // will effectively destroy and recreate the database if a migration
            // from 5 to 6 is needed. If you had actual schema changes between
            // version 5 and 6 that you wanted to preserve data for,
            // you would add ALTER TABLE SQL statements here.
        }
    };

    /**
     * Defines a database migration from version 6 to 7.
     * This migration adds a unique index on the email field.
     */
    public static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // First, create a temporary table with the new schema
            database.execSQL("CREATE TABLE IF NOT EXISTS users_new (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "firstName TEXT NOT NULL, " +
                "lastName TEXT NOT NULL, " +
                "email TEXT NOT NULL, " +
                "phone TEXT NOT NULL)");

            // Copy data from old table to new table, keeping only one record per email
            database.execSQL("INSERT INTO users_new (id, firstName, lastName, email, phone) " +
                "SELECT MIN(id), firstName, lastName, email, phone " +
                "FROM users " +
                "GROUP BY email");

            // Drop the old table
            database.execSQL("DROP TABLE users");

            // Rename the new table to the original name
            database.execSQL("ALTER TABLE users_new RENAME TO users");

            // Add the unique index
            database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_users_email` ON `users` (`email`)");
        }
    };
} 