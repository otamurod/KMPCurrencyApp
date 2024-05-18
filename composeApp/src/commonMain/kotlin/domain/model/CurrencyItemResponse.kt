package domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CurrencyItemResponse(
    @SerialName("code")
    val code: String,
    @SerialName("value")
    val value: Double
)
