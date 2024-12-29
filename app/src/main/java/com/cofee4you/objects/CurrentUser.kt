package com.cofee4you.objects

import com.cofee4you.models.User

object CurrentUser {
    private var user: User? = null

    fun getUser(): User? {
        return user
    }

    fun setUser(newUser: User) {
        user = newUser
    }

    fun clearUser() {
        user = null
    }
}
