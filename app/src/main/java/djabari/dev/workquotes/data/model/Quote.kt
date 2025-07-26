package djabari.dev.workquotes.data.model

import android.os.Parcelable
import djabari.dev.workquotes.data.db.room.entity.QuoteEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class Quote(
    val id: String,
    val content: String,
    val author: String,
    val tags: List<String>
) : Parcelable

internal fun Quote.toEntity(): QuoteEntity {
    return QuoteEntity(
        id = id,
        content = content,
        author = author,
        tags = tags
    )
}