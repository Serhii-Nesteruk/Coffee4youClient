package com.cofee4you.models

data class LoginResponse(
    val userId: String,
    val phoneNumber: String,
    val referralCode: String
)
