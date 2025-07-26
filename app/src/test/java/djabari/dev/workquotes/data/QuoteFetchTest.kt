package djabari.dev.workquotes.data

import djabari.dev.workquotes.data.datasource.FakeRemoteDataSource
import djabari.dev.workquotes.data.di.TestQuoteFetchComponent
import djabari.dev.workquotes.data.di.create
import djabari.dev.workquotes.data.model.Result
import djabari.dev.workquotes.data.model.dataOrNull
import io.ktor.client.network.sockets.ConnectTimeoutException
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class QuoteFetchTest {
    private lateinit var testQuoteFetchComponent: TestQuoteFetchComponent

    @Before
    fun setup() {
        testQuoteFetchComponent = TestQuoteFetchComponent::class.create()
    }

    @Test
    fun `test successful data fetch`() = runBlocking {
        // This test would typically involve invoking the remote data source to fetch a quote
        // and asserting that the result is successful. Since we are using a fake remote data source,
        // we can directly check if it returns an empty list as expected.
        val result = testQuoteFetchComponent.fakes.remoteDataSource.getRandomQuote(3, emptyList())
        // Assert that the result is successful and contains quotes according to limit
        assert(result is Result.Success) { "Expected a successful result" }
        assert(result.dataOrNull?.size == 3) { "Expected an 1 item of quotes" }
    }

    @Test
    fun `test error for invalid limit`() = runBlocking {
        // This test would typically involve invoking the remote data source with an invalid limit
        // and asserting that the result is an error.
        val result = testQuoteFetchComponent.fakes.remoteDataSource.getRandomQuote(0, emptyList())
        // Assert that the result is an error
        assert(result is Result.Error) { "Expected an error result" }
        assert((result as Result.Error).throwable?.message == "Invalid limit: must be greater than 0") {
            "Expected an error message for invalid limit"
        }
    }

    @Test
    fun `test connection error handling`() = runBlocking {
        // This test would typically involve simulating a connection error
        // and asserting that the result is an error.
        (testQuoteFetchComponent.fakes.remoteDataSource as FakeRemoteDataSource).isConnectionError = true
        val result = testQuoteFetchComponent.fakes.remoteDataSource.getRandomQuote(3, emptyList())
        // Assert that the result is an error
        assert(result is Result.Error) { "Expected an error result" }
        assert((result as Result.Error).throwable is ConnectTimeoutException) {
            "Expected a ConnectTimeoutException"
        }
    }
}