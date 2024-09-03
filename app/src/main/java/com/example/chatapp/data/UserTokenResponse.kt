package com.example.chatapp.data

data class UserTokenResponse(
    val user: UserData,
    val op: String,
    val result: Boolean
)

data class UserData(
    val uid: String,
    val token: String
)
