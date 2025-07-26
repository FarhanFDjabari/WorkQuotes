package djabari.dev.workquotes.data.datasource.remote

import djabari.dev.workquotes.data.model.Result
import djabari.dev.workquotes.data.network.response.QuoteResponse
import djabari.dev.workquotes.data.network.response.safeApiCall
import djabari.dev.workquotes.data.network.response.typedRequestResponseOrThrow
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.URLBuilder
import io.ktor.http.parameters
import me.tatarka.inject.annotations.Inject

interface WorkQuotesRemoteDataSource {
    suspend fun getRandomQuote(
        limit: Int,
        tags: List<String>
    ): Result<List<QuoteResponse>>
    suspend fun getAvailableTags(): Result<List<String>>
}

@Inject
class WorkQuotesRemoteDataSourceImpl(
    private val httpClient: HttpClient
) : WorkQuotesRemoteDataSource {
    override suspend fun getRandomQuote(
        limit: Int,
        tags: List<String>
    ): Result<List<QuoteResponse>> = safeApiCall {
        val response = httpClient.get("quotes/random") {
            parameter("count", limit)
            if (tags.isNotEmpty()) parameter("tags", tags.joinToString(","))
        }

        if (limit > 1) {
            response.typedRequestResponseOrThrow<List<QuoteResponse>>()
        } else {
            val actualResponse = response.typedRequestResponseOrThrow<QuoteResponse>()
            listOf(actualResponse)
        }
    }

    override suspend fun getAvailableTags(): Result<List<String>> = safeApiCall {
        val response = httpClient.get("tags").typedRequestResponseOrThrow<List<String>>()
        response
    }
}