package com.cofee4you.models

data class PaymentResponse(
    val payment_data: String,
    val checkout_url: String
)