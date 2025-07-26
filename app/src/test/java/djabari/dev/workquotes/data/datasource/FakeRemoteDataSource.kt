package djabari.dev.workquotes.data.datasource

import djabari.dev.workquotes.data.datasource.remote.WorkQuotesRemoteDataSource
import djabari.dev.workquotes.data.model.Result
import djabari.dev.workquotes.data.network.response.QuoteResponse
import djabari.dev.workquotes.data.network.response.safeApiCall
import io.ktor.client.network.sockets.ConnectTimeoutException

class FakeRemoteDataSource : WorkQuotesRemoteDataSource {
    var isConnectionError: Boolean = false

    override suspend fun getRandomQuote(
        limit: Int,
        tags: List<String>
    ): Result<List<QuoteResponse>> = safeApiCall {
        if (isConnectionError) {
            throw ConnectTimeoutException(
                message = "Connection Timeout",
                cause = Exception("Simulated connection error")
            )
        }
        return if (limit > 0) {
            Result.Success(
                List(limit) {
                    QuoteResponse(
                        id = it,
                        quote = "Content $it",
                        author = "Quote Author $it",
                        tags = emptyList(),
                    )
                }
            )
        } else {
            Result.Error(Exception("Invalid limit: must be greater than 0"))
        }
    }

    override suspend fun getAvailableTags(): Result<List<String>> {
        return safeApiCall {
            if (isConnectionError) {
                throw ConnectTimeoutException(
                    message = "Connection Timeout",
                    cause = Exception("Simulated connection error")
                )
            }

            listOf("inspirational", "motivational", "life", "humor")
        }
    }
}