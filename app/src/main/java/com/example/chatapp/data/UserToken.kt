package com.example.chatapp.data

data class UserToken(val op: String = "useradd", val token: String, val user: User)

data class User(val address: String, val token: Token)

data class Token(val apiId: String, val expiry:Int)
