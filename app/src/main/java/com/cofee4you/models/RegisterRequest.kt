package com.cofee4you.models

data class RegisterRequest (
    val phoneNumber: String,
    val name: String,
    val lastName: String,
    val email: String,
    val password: String,
    val referralCode: String
)