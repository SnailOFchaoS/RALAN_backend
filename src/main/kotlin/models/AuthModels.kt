package pro.ralan.models

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val success: Boolean,
    val message: String,
    val token: String? = null
)

@Serializable
data class User(
    val id: String,
    val username: String,
    val createdAt: String
)
