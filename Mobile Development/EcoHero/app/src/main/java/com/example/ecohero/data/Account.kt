package com.example.ecohero.data

data class Account(
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val uid: String = "",
    val history: History? = null,
)

data class History(
    val photo: String? = null,
    val result: String? = null,
    val date: Long? = null
)