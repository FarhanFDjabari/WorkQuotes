package djabari.dev.workquotes.data.model

import djabari.dev.workquotes.data.enum.WorkQuotesFrequency
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
data class WorkQuotesConfig(
    val enable: Boolean = true,
    val quoteFrequency: WorkQuotesFrequency,
    val quoteTime: LocalTime,
    val maxNumberOfQuotes: Int,
    val selectedQuoteTags: List<String>
)