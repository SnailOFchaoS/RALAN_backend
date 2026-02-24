package pro.ralan.models

import kotlinx.serialization.Serializable

@Serializable
data class Offer(
    val id: String,
    val discipline: List<String>,
    val offerName: String,
    val date: String,
    val price: Int,
    val time: String,
    val level: List<String>,
    val important: Boolean = false
)

@Serializable
data class CreateOfferRequest(
    val offerName: String,
    val price: Int,
    val discipline: List<String> = emptyList(),
    val date: String = "",
    val time: String = "",
    val level: List<String> = emptyList(),
    val important: Boolean = false
)

@Serializable
data class UpdateOfferRequest(
    val discipline: List<String>? = null,
    val offerName: String? = null,
    val date: String? = null,
    val price: Int? = null,
    val time: String? = null,
    val level: List<String>? = null,
    val important: Boolean? = null
)

@Serializable
data class OfferResponse(
    val success: Boolean,
    val message: String,
    val data: Offer? = null
)

@Serializable
data class OffersListResponse(
    val success: Boolean,
    val data: List<Offer>
)

