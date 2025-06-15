package com.example.usermanagement.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;
import kotlinx.coroutines.flow.Flow;

/**
 * Data Access Object for User entity
 */
@Dao
public interface UserDao {
    /**
     * Retrieves all users from the database.
     * @return A Flow emitting a list of all users.
     */
    @Query("SELECT * FROM users")
    Flow<List<User>> getAllUsers();

    /**
     * Retrieves a specific user by their ID.
     * @param userId The ID of the user to retrieve.
     * @return The User object corresponding to the given ID, or null if not found.
     */
    @Query("SELECT * FROM users WHERE id = :userId")
    User getUserById(long userId);

    /**
     * Inserts a new user into the database.
     * @param user The User object to insert.
     * @return The row ID of the newly inserted user.
     */
    @Insert
    long insertUser(User user);

    /**
     * Updates an existing user in the database.
     * @param user The User object to update.
     */
    @Update
    void updateUser(User user);

    /**
     * Deletes a user from the database.
     * @param user The User object to delete.
     */
    @Delete
    void deleteUser(User user);

    /**
     * Searches for users whose first name or last name matches the given query.
     * The search is case-insensitive and matches partial strings.
     * @param query The search query string.
     * @return A Flow emitting a list of users matching the query.
     */
    @Query("SELECT * FROM users WHERE firstName LIKE '%' || :query || '%' OR lastName LIKE '%' || :query || '%'")
    Flow<List<User>> searchUsers(String query);

    /**
     * Retrieves a user by their email address.
     * @param email The email to search for.
     * @return The User object if found, or null otherwise.
     */
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    User getUserByEmail(String email);
} 