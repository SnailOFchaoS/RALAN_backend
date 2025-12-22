package pro.ralan.models

import kotlinx.serialization.Serializable

@Serializable
data class ContactFormRequest(
    val fullName: String,
    val phone: String,
    val email: String
)

@Serializable
data class ContactFormResponse(
    val success: Boolean,
    val message: String
)

