package com.cofee4you.models

data class VerifyRequest (
    val phoneNumber: String,
    val code: String
)