package com.example.usermanagement.strategy

import android.content.Context
import com.example.usermanagement.R
import com.example.usermanagement.data.User
import com.example.usermanagement.util.ValidationResult
import com.example.usermanagement.util.ValidationUtils

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
            else -> ValidationResult.success()
        }
    }
} 