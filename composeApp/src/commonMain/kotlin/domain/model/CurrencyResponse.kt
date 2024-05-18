package domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CurrencyApiResponse(
    @SerialName("meta")
    val meta: MetaData,
    @SerialName("data")
    val data: Map<String, CurrencyItemResponse>
)