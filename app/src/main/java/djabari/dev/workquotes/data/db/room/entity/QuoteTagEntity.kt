package djabari.dev.workquotes.data.db.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quote_tag")
data class QuoteTagEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tag: String
)

internal fun QuoteTagEntity.toTag(): String {
    return tag
}
