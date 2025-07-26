package djabari.dev.workquotes.data.network

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttpConfig
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.plugin
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.accept
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.content.OutgoingContent
import io.ktor.http.contentType
import io.ktor.http.headers
import io.ktor.http.parseUrlEncodedParameters
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Inject
import java.util.concurrent.TimeUnit

@Inject
internal class HttpClientFactory(
    private val json: Json
) {
    val httpClient by lazy {
        HttpClient {
            engine {
                this as OkHttpConfig

                config {
                    retryOnConnectionFailure(true)
                    connectTimeout(0, TimeUnit.SECONDS)
                    hostnameVerifier { _, _ -> true }
                }
            }

            install(ContentNegotiation) {
                json(json)
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 60000
                connectTimeoutMillis = 60000
                socketTimeoutMillis = 60000
            }

            install(Logging)

            defaultRequest {
                url {
                    url("https://quote-slate-eight.vercel.app/api/")
                }
            }

            ContentEncoding {
                gzip()
                deflate()
            }

        }.apply {
            plugin(HttpSend).intercept { request ->
                request.logBody()
                request.contentType(ContentType.Application.Json)
                execute(request)
            }
        }
    }

    private fun HttpRequestBuilder.logBody() {
        val logHeader = mutableMapOf<String, String>()
        val logBody = mutableMapOf<String, String>()

        this.headers.entries().forEach { header ->
            logHeader[header.key] = header.value.first()
        }

        when (this.body) {
            is FormDataContent -> {
                (this.body as FormDataContent).let {
                    it.formData.forEach { key, value ->
                        logBody[key] = value.first()
                    }
                }
            }
            is MultiPartFormDataContent -> {
                (this.body as MultiPartFormDataContent).let {
                    logBody["multipart"] = "true"
                    it.headers.forEach { s, strings ->
                        logHeader[s] = strings.first()
                    }
                    it.contentType.let { contentType ->
                        logHeader["Content-Type"] = contentType.toString()
                    }
                    it.contentLength.let { contentLength ->
                        logHeader["Content-Length"] = contentLength.toString()
                    }
                }
            }
            is OutgoingContent.ByteArrayContent -> {
                (this.body as OutgoingContent.ByteArrayContent).let {
                    val params = it.bytes().decodeToString().parseUrlEncodedParameters()
                    params.forEach { key, value ->
                        logBody[key] = value.first()
                    }
                }
            }
        }

        val jsonBody = json.encodeToString(logBody)
        val jsonHeader = json.encodeToString(logHeader)

        Log.d("HTTPCLIENT", "######## PARAM START ########")
        Log.d("HTTPCLIENT", "Url -> ${this.url}")
        Log.d("HTTPCLIENT", "Method -> ${this.method.value}")
        Log.d("HTTPCLIENT", "Headers -> $jsonHeader")
        Log.d("HTTPCLIENT", "Params -> $jsonBody")
        Log.d("HTTPCLIENT", "######## PARAM END ########")
    }
}