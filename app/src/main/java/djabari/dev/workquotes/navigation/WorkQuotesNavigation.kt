package djabari.dev.workquotes.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface WorkQuotesNavigation {
    @Serializable
    data object Home: WorkQuotesNavigation
    @Serializable
    data object History: WorkQuotesNavigation
}