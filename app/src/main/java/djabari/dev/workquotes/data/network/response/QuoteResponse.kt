package djabari.dev.workquotes.data.network.response

import djabari.dev.workquotes.data.model.Quote
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuoteResponse(
    @SerialName("id")
    val id: Int,
    @SerialName("quote")
    val quote: String,
    @SerialName("author")
    val author: String,
    @SerialName("tags")
    val tags: List<String>
)

internal fun QuoteResponse.toQuote(): Quote {
    return Quote(
        id = "$id",
        content = quote,
        author = author,
        tags = tags
    )
}