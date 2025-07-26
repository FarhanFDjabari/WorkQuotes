package djabari.dev.workquotes.data.db.room.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import djabari.dev.workquotes.data.db.room.entity.QuoteEntity
import djabari.dev.workquotes.data.db.room.entity.QuoteTagEntity

@Dao
interface WorkQuotesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(quote: QuoteEntity)

    @Query("SELECT * FROM quote WHERE id = :id")
    suspend fun getQuotesById(id: String): QuoteEntity?

    @Query("DELETE FROM quote WHERE id = :id")
    suspend fun deleteQuoteById(id: String)

    @Query("SELECT * FROM quote WHERE quote.content LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%'")
    fun getQuoteHistory(query: String): PagingSource<Int, QuoteEntity>

    @Query("SELECT * FROM quote_tag")
    fun getAvailableTags(): List<QuoteTagEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun saveTags(tags: List<QuoteTagEntity>)

    @Query("DELETE FROM quote_tag")
    suspend fun deleteAllTags()
}