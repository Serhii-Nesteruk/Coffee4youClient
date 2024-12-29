package com.cofee4you.models

data class FacebookDTO(
    val email: String?,
    val name: String?,
    val facebookId: String?,
)

data class FacebookLoginRequest(
    val facebookDTO: FacebookDTO,
    val accessToken: String
)

