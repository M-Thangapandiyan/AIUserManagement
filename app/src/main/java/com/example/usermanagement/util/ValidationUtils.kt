package com.example.usermanagement.util

/**
 * Utility object for common validation functions.
 */
object ValidationUtils {
    /**
     * Checks if the given email string is valid.
     * @param email The email string to validate.
     * @return `true` if the email is valid, `false` otherwise.
     */
    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Checks if the given phone number string is valid.
     * This regex allows for an optional leading '+' and 1 to 14 digits.
     * @param phone The phone number string to validate.
     * @return `true` if the phone number is valid, `false` otherwise.
     */
    fun isValidPhone(phone: String): Boolean {
        return phone.matches(Regex("^\\+?[1-9]\\d{1,14}$"))
    }

    /**
     * Checks if the given string is blank (empty or contains only whitespace characters).
     * @param value The string to check.
     * @return `true` if the string is blank, `false` otherwise.
     */
    fun isFieldBlank(value: String): Boolean {
        return value.isBlank()
    }
} 