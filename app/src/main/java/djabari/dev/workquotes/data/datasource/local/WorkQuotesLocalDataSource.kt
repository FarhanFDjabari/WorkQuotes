package djabari.dev.workquotes.data.datasource.local

import androidx.paging.PagingSource
import djabari.dev.workquotes.data.db.room.dao.WorkQuotesDao
import djabari.dev.workquotes.data.db.room.entity.QuoteEntity
import djabari.dev.workquotes.data.db.room.entity.QuoteTagEntity
import djabari.dev.workquotes.data.db.room.entity.toTag
import me.tatarka.inject.annotations.Inject

interface WorkQuotesLocalDataSource {
    fun getQuotesHistory(
        search: String
    ): PagingSource<Int, QuoteEntity>
    suspend fun saveQuoteHistory(quote: QuoteEntity)
    suspend fun deleteQuote(id: String)
    suspend fun saveQuoteTags(tags: List<String>)
    suspend fun clearQuoteTags()
    fun getQuoteTags(): List<String>
}

@Inject
class WorkQuotesLocalDataSourceImpl(
    private val workQuotesDao: WorkQuotesDao
) : WorkQuotesLocalDataSource {
    override fun getQuotesHistory(
        search: String
    ): PagingSource<Int, QuoteEntity> = workQuotesDao.getQuoteHistory(
        query = search
    )

    override suspend fun saveQuoteHistory(quote: QuoteEntity) {
        workQuotesDao.insert(quote)
    }

    override suspend fun deleteQuote(id: String) {
        workQuotesDao.deleteQuoteById(id)
    }

    override suspend fun saveQuoteTags(tags: List<String>) {
        workQuotesDao.saveTags(
            tags = tags.map { tag ->
                QuoteTagEntity(tag = tag)
            }
        )
    }

    override suspend fun clearQuoteTags() {
        workQuotesDao.deleteAllTags()
    }

    override fun getQuoteTags(): List<String> {
        return workQuotesDao.getAvailableTags().map {
            it.toTag()
        }
    }
}