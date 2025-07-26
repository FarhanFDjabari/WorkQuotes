package djabari.dev.workquotes.data.network.response

import android.util.Log
import djabari.dev.workquotes.di.workQuotesComponent
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.statement.HttpResponse
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonPrimitive
import kotlin.coroutines.cancellation.CancellationException

@Throws(CancellationException::class)
internal suspend inline fun <reified T> HttpResponse.typedRequestResponseOrThrow(): T {
    Log.d("WorkQuotes-DEBUG-HTTP", "HttpResponse.typedRequestResponseOrThrow() status.value=${status.value} status.description=${status.description}")
    return when (status.value) {
        in 200..399 -> {
            try {
                val responseBody = body<String>()
                Log.d("HTTPRESPONSE", "Raw response body: $responseBody")

                val resultData = workQuotesComponent().provideDefaultJsonConvert.decodeFromString<T>(responseBody)
                Log.d("HTTPRESPONSE", "HttpResponse.typedRequestResponseOrThrow() resultData=${resultData}")

                resultData
            } catch (e: Throwable) {
                throw CancellationException(e)
            } catch (e: IllegalStateException) {
                throw IllegalStateException(e)
            }
        }

        in 400..499 -> {
            var error: Exception? = null
            val response = try {
                val responseBody = body<String>()
                Log.d("HTTPRESPONSE", "Raw response body: $responseBody")

                responseBody
            } catch (e: NoTransformationFoundException) {
                error = e
                null
            }

            val resultData = response?.let {
                workQuotesComponent().provideDefaultJsonConvert.decodeFromString<T>(response)
            }
            Log.d("HTTPRESPONSE", "HttpResponse.typedRequestResponseOrThrow() resultData=${resultData}")

            throw ClientRequestException(
                cachedResponseText = "Response not found",
                response = this,
            )
        }
        in 500..599 -> {
            var error: Exception? = null
            val response = try {
                val responseBody = body<String>()
                Log.d("HTTPRESPONSE", "Raw response body: $responseBody")

                responseBody
            } catch (e: NoTransformationFoundException) {
                error = e
                null
            }

            val resultData = response?.let {
                workQuotesComponent().provideDefaultJsonConvert.decodeFromString<T>(response)
            }
            Log.d("HTTPRESPONSE", "HttpResponse.typedRequestResponseOrThrow() resultData=${resultData}")

            throw ClientRequestException(
                cachedResponseText = "Internal server error",
                response = this,
            )
        }
        else -> {
            throw ClientRequestException(
                cachedResponseText = "Unknown Error, please try again later. (Error code: ${status.value})",
                response = this
            )
        }
    }
}