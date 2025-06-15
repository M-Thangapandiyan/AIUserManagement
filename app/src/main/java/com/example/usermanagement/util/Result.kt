package com.example.usermanagement.util

/**
 * A sealed class that represents the result of an operation.
 * It can be one of three states: [Success], [Error], or [Loading].
 * @param <T> The type of the data returned in a [Success] state.
 */
sealed class Result<out T> {
    /**
     * Represents a successful operation with data.
     * @param data The data returned by the successful operation.
     */
    data class Success<T>(val data: T) : Result<T>()

    /**
     * Represents a failed operation with an exception.
     * @param exception The exception that caused the operation to fail.
     */
    data class Error(val exception: Exception) : Result<Nothing>()

    /**
     * Represents an ongoing loading operation.
     */
    object Loading : Result<Nothing>()

    companion object {
        /**
         * Creates a [Result.Success] instance.
         * @param data The data for the successful result.
         * @return A [Result.Success] instance.
         */
        fun <T> success(data: T): Result<T> = Success(data)

        /**
         * Creates a [Result.Error] instance.
         * @param exception The exception for the error result.
         * @return A [Result.Error] instance.
         */
        fun error(exception: Exception): Result<Nothing> = Error(exception)

        /**
         * Creates a [Result.Loading] instance.
         * @return A [Result.Loading] instance.
         */
        fun loading(): Result<Nothing> = Loading
    }
} 