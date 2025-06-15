package com.example.usermanagement.util;

/**
 * Represents the result of a validation operation
 */
public final class ValidationResult {
    private final boolean isValid;
    private final String errorMessage;

    private ValidationResult(boolean isValid, String errorMessage) {
        this.isValid = isValid;
        this.errorMessage = errorMessage;
    }

    /**
     * Creates a successful validation result
     * @return A ValidationResult indicating success
     */
    public static ValidationResult success() {
        return new ValidationResult(true, null);
    }

    /**
     * Creates a validation result with an error message
     * @param message The error message
     * @return A ValidationResult containing the error message
     */
    public static ValidationResult error(String message) {
        return new ValidationResult(false, message);
    }

    /**
     * Checks if the validation was successful
     * @return true if validation was successful, false otherwise
     */
    public boolean isValid() {
        return isValid;
    }

    /**
     * Gets the error message if validation failed
     * @return The error message, or null if validation was successful
     */
    public String getErrorMessage() {
        return errorMessage;
    }
} 