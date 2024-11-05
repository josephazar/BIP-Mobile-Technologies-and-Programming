package com.authcraftandroid.models

data class LoginRequest(val email_address: String, val password: String)
data class LoginWithMobileRequest(val mobile_number: String, val otp: String)
data class ForgotPasswordRequest(val email_address: String, val password_reset_code: String)
data class ResetPasswordRequest(val email_address: String, val password_reset_code: String, val password: String, val confPassword: String)
