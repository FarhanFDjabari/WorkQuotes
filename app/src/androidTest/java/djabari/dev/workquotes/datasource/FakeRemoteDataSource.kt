package djabari.dev.workquotes.datasource

import djabari.dev.workquotes.data.datasource.remote.WorkQuotesRemoteDataSource
import djabari.dev.workquotes.data.model.Result
import djabari.dev.workquotes.data.network.response.QuoteResponse

class FakeRemoteDataSource : WorkQuotesRemoteDataSource {
    override suspend fun getRandomQuote(
        limit: Int,
        tags: List<String>
    ): Result<List<QuoteResponse>> {
        return Result.Success(emptyList())
    }

    override suspend fun getAvailableTags(): Result<List<String>> {
        return Result.Success(emptyList())
    }
}