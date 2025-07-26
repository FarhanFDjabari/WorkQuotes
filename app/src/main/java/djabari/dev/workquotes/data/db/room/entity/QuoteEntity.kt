package djabari.dev.workquotes.data.db.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import djabari.dev.workquotes.data.model.Quote
import kotlinx.serialization.SerialName

@Entity(tableName = "quote")
data class QuoteEntity(
    @PrimaryKey
    @SerialName("id")
    val id: String,
    @SerialName("content")
    val content: String,
    @SerialName("author")
    val author: String,
    @SerialName("tags")
    val tags: List<String>
)

internal fun QuoteEntity.toQuote(): Quote {
    return Quote(
        id = id,
        content = content,
        author = author,
        tags = tags
    )
}