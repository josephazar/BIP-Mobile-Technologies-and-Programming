package com.authcraftandroid.models

data class RegistrationRequest(
    var name: String,
    var mobile_number: String,
    var email_address: String,
    var password: String,
    var confirmPassword: String
)

data class UpdateRequest(
    var name: String,
    var mobile_number: String,
    var email_address: String,
)

data class PasswordChangeRequest(
    var old_password: String,
    var password: String,
    var confPassword: String
)
