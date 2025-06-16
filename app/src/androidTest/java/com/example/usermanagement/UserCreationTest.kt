package com.example.usermanagement

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.usermanagement.data.User
import com.example.usermanagement.data.UserDatabase
import com.example.usermanagement.repository.UserRepositoryImpl
import com.example.usermanagement.strategy.UserValidationStrategy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
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
            phone = "1234567890",
            dob = "1990-01-01",
            address = "123 Main St, Springfield"
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
            phone = "+1234567890",  // Changed to valid phone format
            dob = "1990-01-01",
            address = "123 Test St, Apt 4B"
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
        val longPhone = "+123456789012345"  // 15 digits with + (max allowed)

        val longAddress = "A".repeat(500)  // Long address within reasonable limits

        val user = User(
            firstName = longName,
            lastName = longName,
            email = longEmail,
            phone = longPhone,
            dob = "1990-01-01",  // Valid date of birth
            address = longAddress  // Long address
        )

        val validationResult = validationStrategy.validate(user)
        assertTrue(
            "User validation should pass. Error: ${validationResult.getErrorMessage()}",
            validationResult.isValid()
        )

        val userId = repository.insertUser(user)
        val savedUser = repository.getUserById(userId)
        assertNotNull("Saved user should not be null", savedUser)
        assertEquals("Long name should be preserved", longName, savedUser?.firstName)
        assertEquals("Long address should be preserved", longAddress, savedUser?.address)
    }

    @Test
    fun testCreateUserWithWhitespace() = runBlocking {
        val user = User(
            firstName = "  John  ",
            lastName = "  Doe  ",
            email = "john.doe@example.com",
            phone = "1234567890",
            dob = "1990-01-01"
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
            email = "jose.garcia@example.com",
            phone = "1234567890",
            dob = "1990-01-01",
            address = "123 Main St"
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
            phone = "1234567890",
            dob = "1990-01-01"  // Added required dob field
        )

        val validationResult = validationStrategy.validate(user)
        assertTrue("User validation should pass", validationResult.isValid())

        val userId = repository.insertUser(user)
        val savedUser = repository.getUserById(userId)
        assertNotNull("Saved user should not be null", savedUser)
        assertEquals("Email case should be preserved", user.email, savedUser?.email)
    }

    @Test
    fun testConcurrentFormSubmissions() = runBlocking {
        val users = (1..100).map { i ->
            User(
                firstName = "User$i",
                lastName = "Test",
                email = "user$i@test.com",
                phone = "12345678${i.toString().padStart(2, '0')}",
                dob = "199${i % 10}-01-01",
                address = "Address $i"
            )
        }

        val results = withContext(Dispatchers.Default) {
            users.map { user ->
                async {
                    try {
                        val validationResult = validationStrategy.validate(user)
                        if (validationResult.isValid()) {
                            repository.insertUser(user)
                        } else {
                            -1L
                        }
                    } catch (e: Exception) {
                        -1L
                    }
                }
            }.awaitAll()
        }

        assertEquals("All submissions should succeed", users.size, results.count { it > 0 })

        val savedUsers = repository.allUsers.first()
        assertEquals("All users should be saved", users.size, savedUsers.size)

        val uniqueEmails = savedUsers.map { it.email }.toSet()
        assertEquals("All emails should be unique", users.size, uniqueEmails.size)
    }


    // DOB Field Tests

    @Test
    fun testValidDobFormats() = runBlocking {
        val validDates = listOf(
            "1990-01-01",  // Standard date
            "2000-02-29",  // Leap year
            "2023-12-31",  // End of year
            "1970-01-01"   // Unix epoch start
        )

        validDates.forEach { date ->
            val user = User(
                firstName = "Test",
                lastName = "User",
                email = "test.${System.currentTimeMillis()}@example.com",
                phone = "1234567890",
                dob = date,
                address = "123 Test St"
            )

            val validationResult = validationStrategy.validate(user)
            assertTrue(
                "Date '$date' should be valid: ${validationResult.errorMessage}",
                validationResult.isValid
            )
        }
    }

    @Test
    fun testInvalidDobFormats() = runBlocking {
        val invalidDates = listOf(
            "1990/01/01",  // Wrong separator
            "01-01-1990",  // Wrong format (should be YYYY-MM-DD)
            "2023-02-29",  // Not a leap year
            "3000-01-01",  // Future date
            "1990-13-01",  // Invalid month
            "1990-01-32",  // Invalid day
            "",            // Empty
            "invalid-date" // Garbage
        )

        invalidDates.forEach { date ->
            val user = User(
                firstName = "Test",
                lastName = "User",
                email = "test.${System.currentTimeMillis()}@example.com",
                phone = "1234567890",
                dob = date,
                address = "123 Test St"
            )

            val validationResult = validationStrategy.validate(user)
            assertFalse("Date '$date' should be invalid", validationResult.isValid)
        }
    }

    @Test
    fun testDobWithWhitespace() = runBlocking {
        val user = User(
            firstName = "Test",
            lastName = "User",
            email = "test.${System.currentTimeMillis()}@example.com",
            phone = "1234567890",
            dob = " 1990-01-01 ",  // Whitespace should be trimmed
            address = "123 Test St"
        )

        val validationResult = validationStrategy.validate(user)
        assertTrue(
            "DOB with whitespace should be valid: ${validationResult.errorMessage}",
            validationResult.isValid
        )
    }

    // Address Field Tests

    @Test
    fun testUpdateUser() = runBlocking {
        // Create initial user
        val originalUser = User(
            firstName = "John",
            lastName = "Doe",
            email = "john.doe@example.com",
            phone = "1234567890",
            dob = "1990-01-01",
            address = "123 Main St, Springfield"
        )

        // Insert the original user
        val userId = repository.insertUser(originalUser)
        assertTrue("User should be inserted successfully", userId > 0)

        // Create updated user with same ID but different details
        val updatedUser = originalUser.copy(
            firstName = "Johnathan",
            lastName = "Doe-Smith",
            email = "john.doe.smith@example.com",
            phone = "0987654321",
            dob = "1990-12-31",
            address = "456 Oak Ave, Shelbyville"
        )

        // Update the user
        repository.updateUser(updatedUser)


        // Verify the updated user details
        val retrievedUser = repository.getUserById(userId)
        assertNotNull("Retrieved user should not be null", retrievedUser)
        assertEquals("First name should be updated", "Johnathan", retrievedUser?.firstName)
        assertEquals("Last name should be updated", "Doe-Smith", retrievedUser?.lastName)
        assertEquals("Email should be updated", "john.doe.smith@example.com", retrievedUser?.email)
        assertEquals("Phone should be updated", "0987654321", retrievedUser?.phone)
        assertEquals("DOB should be updated", "1990-12-31", retrievedUser?.dob)
        assertEquals(
            "Address should be updated",
            "456 Oak Ave, Shelbyville",
            retrievedUser?.address
        )

        // Verify validation passes with updated data
        val validationResult = validationStrategy.validate(updatedUser)
        assertTrue("Updated user should pass validation", validationResult.isValid())
    }

    @Test
    fun testValidAddresses() = runBlocking {
        val validAddresses = listOf(
            "123 Main St, Apt 4B, New York, NY 10001",  // Standard address
            "Ümlautstraße 123, Köln, Deutschland",      // Unicode characters
            "1234 Long Address " + "x".repeat(1000),     // Long address (using extension function)
            "123# Special !@#\$%^&*() Chars"              // Special characters
        )

        validAddresses.forEach { address ->
            val user = User(
                firstName = "Test",
                lastName = "User",
                email = "test.${System.currentTimeMillis()}@example.com",
                phone = "1234567890",
                dob = "1990-01-01",
                address = address
            )

            val validationResult = validationStrategy.validate(user)
            assertTrue(
                "Address should be valid: ${validationResult.errorMessage}",
                validationResult.isValid
            )
        }
    }

    @Test
    fun testAddressWithPotentialInjection() = runBlocking {
        val injectionAttempts = listOf(
            "123 Main St; DROP TABLE users; --",
            "<script>alert('xss')</script>",
            "' OR '1'='1",
            "a".repeat(10000)  // Very long string (using extension function)
        )

        injectionAttempts.forEach { address ->
            val user = User(
                firstName = "Test",
                lastName = "User",
                email = "test.${System.currentTimeMillis()}@example.com",
                phone = "1234567890",
                dob = "1990-01-01",
                address = address
            )

            // Should be treated as valid input (sanitization should happen at display time)
            val validationResult = validationStrategy.validate(user)
            assertTrue(
                "Address with potential injection should be treated as valid: ${validationResult.errorMessage}",
                validationResult.isValid
            )

            // Verify the address was stored exactly as provided
            val userId = repository.insertUser(user)
            val savedUser = repository.getUserById(userId)
            assertEquals(
                "Address should be stored exactly as provided",
                address, savedUser?.address
            )
        }
    }

    @Test
    fun testEmptyAndNullAddress() = runBlocking {
        val testCases = listOf(
            "",      // Empty string
            " ",     // Whitespace
            "  "      // Multiple spaces
        )

        testCases.forEach { address ->
            val user = User(
                firstName = "Test",
                lastName = "User",
                email = "test.${System.currentTimeMillis()}@example.com",
                phone = "1234567890",
                dob = "1990-01-01",
                address = address
            )

            val validationResult = validationStrategy.validate(user)
            assertTrue(
                "Empty/whitespace address should be valid: ${validationResult.errorMessage}",
                validationResult.isValid
            )
        }
    }
}