package com.cofee4you.models

data class User (
    val userId: String,
    val phoneNumber: String,
    var referralCode: String
)