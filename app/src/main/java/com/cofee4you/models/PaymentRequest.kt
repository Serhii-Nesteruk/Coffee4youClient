package com.cofee4you.models

data class PaymentRequest(
    val amount: Double,
    val currency: String,
    val description: String
)