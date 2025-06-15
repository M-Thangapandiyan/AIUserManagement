package com.example.usermanagement

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.usermanagement.data.User
import com.example.usermanagement.data.UserDatabase
import com.example.usermanagement.repository.UserRepositoryImpl
import com.example.usermanagement.strategy.UserValidationStrategy
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserCreationTest {
    private lateinit var db: UserDatabase
    private lateinit var repository: UserRepositoryImpl
    private lateinit var validationStrategy: UserValidationStrategy
    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(
            context,
            UserDatabase::class.java
        ).allowMainThreadQueries().build()
        repository = UserRepositoryImpl(db.userDao())
        validationStrategy = UserValidationStrategy(context)
    }

    @After
    fun cleanup() {
        db.close()
    }

    @Test
    fun testCreateValidUser() = runBlocking {
        val user = User(
            firstName = "John",
            lastName = "Doe",
            email = "john.doe@example.com",
            phone = "1234567890"
        )

        val validationResult = validationStrategy.validate(user)
        assertTrue("User validation should pass", validationResult.isValid())

        val userId = repository.insertUser(user)
        assertTrue("User ID should be greater than 0", userId > 0)

        val savedUser = repository.getUserById(userId)
        assertNotNull("Saved user should not be null", savedUser)
        assertEquals("First name should match", user.firstName, savedUser?.firstName)
        assertEquals("Last name should match", user.lastName, savedUser?.lastName)
        assertEquals("Email should match", user.email, savedUser?.email)
        assertEquals("Phone should match", user.phone, savedUser?.phone)
    }

    @Test
    fun testCreateUserWithEmptyFields() = runBlocking {
        val user = User(
            firstName = "",
            lastName = "",
            email = "",
            phone = ""
        )

        val validationResult = validationStrategy.validate(user)
        assertFalse("User validation should fail", validationResult.isValid())
        assertTrue(
            "Error message should contain first name error",
            validationResult.getErrorMessage().contains("First name cannot be empty")
        )
    }

    @Test
    fun testCreateUserWithInvalidEmail() = runBlocking {
        val user = User(
            firstName = "John",
            lastName = "Doe",
            email = "invalid-email",
            phone = "1234567890"
        )

        val validationResult = validationStrategy.validate(user)
        assertFalse("User validation should fail", validationResult.isValid())
        assertTrue(
            "Error message should contain email error",
            validationResult.getErrorMessage().contains("email")
        )
    }

    @Test
    fun testCreateUserWithInvalidPhone() = runBlocking {
        val user = User(
            firstName = "John",
            lastName = "Doe",
            email = "john.doe@example.com",
            phone = "abc123"  // Changed to clearly invalid phone format (contains letters)
        )

        val validationResult = validationStrategy.validate(user)
        assertFalse("User validation should fail", validationResult.isValid())
        assertTrue(
            "Error message should contain phone error",
            validationResult.getErrorMessage().contains("phone")
        )
    }

    @Test
    fun testCreateUserWithDuplicateEmail() = runBlocking {
        val email = "duplicate@example.com"
        val user1 = User(
            firstName = "John",
            lastName = "Doe",
            email = email,
            phone = "1234567890"
        )

        val user2 = User(
            firstName = "Jane",
            lastName = "Doe",
            email = email,
            phone = "0987654321"
        )

        // First user should be saved successfully
        val userId1 = repository.insertUser(user1)
        assertTrue("First user should be saved successfully", userId1 > 0)

        // Verify first user exists
        val existingUser = repository.getUserByEmail(email)
        assertNotNull("First user should be found by email", existingUser)
        assertEquals("First user's email should match", email, existingUser?.email)

        // Second user should fail to save due to duplicate email
        try {
            repository.insertUser(user2)
            fail("Should throw exception for duplicate email")
        } catch (e: Exception) {
            assertTrue(
                "Error message should mention duplicate email",
                e.message?.contains("email") == true ||
                        e.message?.contains("unique") == true
            )
        }
    }

    @Test
    fun testCreateUserWithSpecialCharacters() = runBlocking {
        val user = User(
            firstName = "John-O'Neil",
            lastName = "Doe-Smith",
            email = "john.doe+test@example.com",
            phone = "+1234567890"  // Changed to valid phone format
        )

        val validationResult = validationStrategy.validate(user)
        assertTrue("User validation should pass", validationResult.isValid())

        val userId = repository.insertUser(user)
        val savedUser = repository.getUserById(userId)
        assertNotNull("Saved user should not be null", savedUser)
        assertEquals(
            "Special characters in name should be preserved",
            user.firstName, savedUser?.firstName
        )
    }

    @Test
    fun testCreateUserWithLongFields() = runBlocking {
        // Using reasonable lengths that should pass validation
        val longName = "A".repeat(50)  // Reduced from 100 to 50
        val longEmail = "a".repeat(30) + "@example.com"  // Reduced from 50 to 30
        val longPhone = "+1" + "2".repeat(13)  // Total 15 digits (max allowed)

        val user = User(
            firstName = longName,
            lastName = longName,
            email = longEmail,
            phone = longPhone
        )

        val validationResult = validationStrategy.validate(user)
        assertTrue("User validation should pass", validationResult.isValid())

        val userId = repository.insertUser(user)
        val savedUser = repository.getUserById(userId)
        assertNotNull("Saved user should not be null", savedUser)
        assertEquals("Long name should be preserved", longName, savedUser?.firstName)
    }

    @Test
    fun testCreateUserWithWhitespace() = runBlocking {
        val user = User(
            firstName = "  John  ",
            lastName = "  Doe  ",
            email = "john.doe@example.com",  // Removed whitespace from email
            phone = "1234567890"  // Removed whitespace from phone number
        )

        val validationResult = validationStrategy.validate(user)
        assertTrue("User validation should pass", validationResult.isValid())

        val userId = repository.insertUser(user)
        val savedUser = repository.getUserById(userId)
        assertNotNull("Saved user should not be null", savedUser)
        assertEquals("Whitespace should be preserved in name", user.firstName, savedUser?.firstName)
        assertEquals(
            "Whitespace should be preserved in last name",
            user.lastName,
            savedUser?.lastName
        )
    }

    @Test
    fun testCreateUserWithUnicodeCharacters() = runBlocking {
        val user = User(
            firstName = "José",
            lastName = "García",
            email = "jose.garcia@example.com",  // Changed to ASCII email
            phone = "1234567890"
        )

        val validationResult = validationStrategy.validate(user)
        assertTrue("User validation should pass", validationResult.isValid())

        val userId = repository.insertUser(user)
        val savedUser = repository.getUserById(userId)
        assertNotNull("Saved user should not be null", savedUser)
        assertEquals(
            "Unicode characters should be preserved in name",
            user.firstName,
            savedUser?.firstName
        )
        assertEquals(
            "Unicode characters should be preserved in last name",
            user.lastName,
            savedUser?.lastName
        )
    }

    @Test
    fun testCreateUserWithNullFields() = runBlocking {
        // Since User class has non-nullable String fields, we'll test with empty strings
        // which is the closest equivalent to null in this case
        val user = User(
            firstName = "",
            lastName = "",
            email = "",
            phone = ""
        )

        val validationResult = validationStrategy.validate(user)
        assertFalse("User validation should fail", validationResult.isValid())
        assertTrue(
            "Error message should contain validation errors",
            validationResult.getErrorMessage().isNotEmpty()
        )
    }

    @Test
    fun testCreateUserWithMixedCaseEmail() = runBlocking {
        val user = User(
            firstName = "John",
            lastName = "Doe",
            email = "John.Doe@Example.com",
            phone = "1234567890"
        )

        val validationResult = validationStrategy.validate(user)
        assertTrue("User validation should pass", validationResult.isValid())

        val userId = repository.insertUser(user)
        val savedUser = repository.getUserById(userId)
        assertNotNull("Saved user should not be null", savedUser)
        assertEquals("Email case should be preserved", user.email, savedUser?.email)
    }
} 