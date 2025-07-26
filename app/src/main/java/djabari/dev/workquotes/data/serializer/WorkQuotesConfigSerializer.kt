package djabari.dev.workquotes.data.serializer

import androidx.datastore.core.Serializer
import djabari.dev.workquotes.data.enum.WorkQuotesFrequency
import djabari.dev.workquotes.data.model.WorkQuotesConfig
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

class WorkQuotesConfigSerializer : Serializer<WorkQuotesConfig> {
    override val defaultValue: WorkQuotesConfig = WorkQuotesConfig(
        enable = false,
        quoteFrequency = WorkQuotesFrequency.DAILY,
        quoteTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time,
        maxNumberOfQuotes = 1,
        selectedQuoteTags = emptyList()
    )

    override suspend fun readFrom(input: InputStream): WorkQuotesConfig =
        try {
            Json.decodeFromString(
                WorkQuotesConfig.serializer(),
                input.readBytes().decodeToString()
            )
        } catch (e: SerializationException) {
            throw SerializationException("Error reading WorkQuotesConfig", e)
        }

    override suspend fun writeTo(
        t: WorkQuotesConfig,
        output: OutputStream
    ) {
        output.write(
            Json.encodeToString(
                WorkQuotesConfig.serializer(),
                t
            ).encodeToByteArray()
        )
    }

}