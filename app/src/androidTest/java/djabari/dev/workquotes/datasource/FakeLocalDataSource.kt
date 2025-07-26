package djabari.dev.workquotes.datasource

import androidx.paging.PagingSource
import djabari.dev.workquotes.data.datasource.local.WorkQuotesLocalDataSource
import djabari.dev.workquotes.data.db.room.entity.QuoteEntity

class FakeLocalDataSource : WorkQuotesLocalDataSource {
    override fun getQuotesHistory(search: String): PagingSource<Int, QuoteEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun saveQuoteHistory(quote: QuoteEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteQuote(id: String) {
        TODO("Not yet implemented")
    }
}