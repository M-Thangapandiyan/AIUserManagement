package com.example.usermanagement.strategy

import android.content.Context
import com.example.usermanagement.R
import com.example.usermanagement.data.User
import com.example.usermanagement.util.ValidationResult
import com.example.usermanagement.util.ValidationUtils
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle

interface ValidationStrategy {
    fun validate(user: User): ValidationResult
}

class UserValidationStrategy(private val context: Context) : ValidationStrategy {
    override fun validate(user: User): ValidationResult {
        return when {
            ValidationUtils.isFieldBlank(user.firstName) -> 
                ValidationResult.error(context.getString(R.string.error_first_name_empty))
            ValidationUtils.isFieldBlank(user.lastName) -> 
                ValidationResult.error(context.getString(R.string.error_last_name_empty))
            ValidationUtils.isFieldBlank(user.email) -> 
                ValidationResult.error(context.getString(R.string.error_email_empty))
            !ValidationUtils.isValidEmail(user.email) -> 
                ValidationResult.error(context.getString(R.string.error_email_invalid))
            ValidationUtils.isFieldBlank(user.phone) -> 
                ValidationResult.error(context.getString(R.string.error_phone_empty))
            !ValidationUtils.isValidPhone(user.phone) -> 
                ValidationResult.error(context.getString(R.string.error_phone_invalid))
            user.dob.isBlank() ->
                ValidationResult.error(context.getString(R.string.error_dob_empty))
            !ValidationUtils.isValidDate(user.dob) ->
                ValidationResult.error(context.getString(R.string.error_dob_invalid))
            else -> {
                val trimmedDob = user.dob.trim()
                val dob = LocalDate.parse(trimmedDob, DateTimeFormatter.ISO_LOCAL_DATE)
                val today = LocalDate.now()
                if (dob.isAfter(today)) {
                    ValidationResult.error(context.getString(R.string.error_dob_future))
                } else {
                    ValidationResult.success()
                }
            }
        }
    }
} 