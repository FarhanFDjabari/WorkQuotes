package djabari.dev.workquotes.data.enum

enum class WorkQuotesFrequency {
    DAILY,
    WEEKLY,
    MONTHLY;

    companion object {
        fun fromString(value: String): WorkQuotesFrequency {
            return when (value) {
                "Daily" -> DAILY
                "Weekly" -> WEEKLY
                "Monthly" -> MONTHLY
                else -> throw IllegalArgumentException("Unknown frequency: $value")
            }
        }
        fun toString(value: WorkQuotesFrequency): String {
            return when (value) {
                DAILY -> "Daily"
                WEEKLY -> "Weekly"
                MONTHLY -> "Monthly"
            }
        }
    }
}