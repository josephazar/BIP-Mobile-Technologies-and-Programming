package com.authcraftandroid.models

data class LoginResponse(
    val success: Boolean,
    val error: String,
    val message: String,
    val result: Result,
)

data class Result(
    val id: Int,
    val name: String,
    val mobile_number: String,
    val email_address: String,
    val active: Int,
    val status: Int,
    val otp: String,
)
