package com.authcraftandroid.models

data class RegistrationResponse(
    val success: Boolean,
    val error: String,
    val message: String,
    val result: UserResult
)

data class UserResult(
    val id: Int,
    val name: String,
    val mobile_number: String,
    val email_address: String,
    val active: Int,
    val status: Int,
    val otp: String,
)
