package djabari.dev.workquotes.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import djabari.dev.workquotes.data.datasource.local.WorkQuotesLocalDataSource
import djabari.dev.workquotes.data.db.room.entity.QuoteEntity

class FakeLocalDataSource : WorkQuotesLocalDataSource {
    override fun getQuotesHistory(search: String): PagingSource<Int, QuoteEntity> {
        return object : PagingSource<Int, QuoteEntity>() {
            override fun getRefreshKey(state: PagingState<Int, QuoteEntity>): Int? {
                // Return null to indicate no specific key for refreshing
                return null
            }

            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, QuoteEntity> {
                return LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null
                )
            }
        }
    }

    override suspend fun saveQuoteHistory(quote: QuoteEntity) {
        // No-op
    }

    override suspend fun deleteQuote(id: String) {
        // No-op
    }

    override suspend fun saveQuoteTags(tags: List<String>) {
        // No-op
    }

    override suspend fun clearQuoteTags() {
        // No-op
    }

    override fun getQuoteTags(): List<String> {
        return emptyList()
    }
}