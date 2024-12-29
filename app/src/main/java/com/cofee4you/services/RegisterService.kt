package com.cofee4you.services

import android.util.Patterns

class RegisterService {
    public fun validateInput(phoneNumber: String,
                              name: String,
                              lastName:String,
                              email: String,
                              password: String) : String? {
        return when {
            phoneNumber.isEmpty() -> {
                "Введіть номер телефону"
            }
            name.isEmpty() -> {
                "Введіть ім'я"
            }
            lastName.isEmpty() -> {
                "Введіть прізвище"
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                "Не правильний формат email"
            }
            password.length < 6 -> {
                "Пароль повинен містити більше ніж 6 символів"
            }
            else -> null
        }
    }
}