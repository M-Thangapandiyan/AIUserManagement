package com.example.usermanagement.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents a user in the system
 */
@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class User(
    @JvmField
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @JvmField
    var firstName: String = "",
    @JvmField
    var lastName: String = "",
    @JvmField
    var email: String = "",
    @JvmField
    var phone: String = "",
    @JvmField
    var dob: String = "",
    @JvmField
    var address: String = ""
) {
    /**
     * Gets the user's full name
     * @return The concatenated first and last name
     */
    val fullName: String
        get() = "$firstName $lastName"

    // Add Java-style getters for better Java interop
    fun getFirstName(): String = firstName
    fun getLastName(): String = lastName
    fun getEmail(): String = email
    fun getPhone(): String = phone
    fun getDob(): String = dob
    fun getAddress(): String = address
    fun getId(): Long = id
} 