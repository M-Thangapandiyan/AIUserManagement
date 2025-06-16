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
     * Checks if the given date string is in valid format (yyyy-MM-dd) and represents a valid date.
     * @param date The date string to validate in format yyyy-MM-dd. Leading/trailing whitespace is trimmed.
     * @return `true` if the date is valid, `false` otherwise.
     */
    fun isValidDate(date: String): Boolean {
        if (date.isEmpty()) {
            return false
        }
        
        val trimmedDate = date.trim()
        // First check the format
        if (!trimmedDate.matches(Regex("^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$"))) {
            return false
        }
        
        // Parse the date components
        val parts = trimmedDate.split("-")
        val year = parts[0].toInt()
        val month = parts[1].toInt()
        val day = parts[2].toInt()
        
        // Check for valid month
        if (month < 1 || month > 12) return false
        
        // Check for valid day based on month
        return when (month) {
            2 -> {
                // February - check for leap year
                val isLeapYear = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
                day in 1..if (isLeapYear) 29 else 28
            }
            4, 6, 9, 11 -> day in 1..30  // April, June, September, November
            else -> day in 1..31  // All other months
        }
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