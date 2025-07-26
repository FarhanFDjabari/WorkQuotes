package djabari.dev.workquotes.ui.screen.app

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import djabari.dev.workquotes.domain.repository.WorkQuotesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest

class WorkQuotesHistoryViewModel(
    private val workQuotesRepository: WorkQuotesRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val searchQueryFlow = savedStateHandle.getStateFlow(SEARCH_QUERY_KEY, "")

    fun search(query: String) {
        savedStateHandle[SEARCH_QUERY_KEY] = query
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val workQuotesHistoryFlow = searchQueryFlow
        .debounce(300L)
        .flatMapLatest { query ->
            workQuotesRepository.getQuotesHistory(query)
        }
        .cachedIn(viewModelScope)

    companion object {
        const val SEARCH_QUERY_KEY = "search_query_key"
    }
}