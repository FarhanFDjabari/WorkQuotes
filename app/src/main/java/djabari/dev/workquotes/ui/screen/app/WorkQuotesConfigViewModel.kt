package djabari.dev.workquotes.ui.screen.app

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import djabari.dev.workquotes.data.db.datastore.ConfigPreferences
import djabari.dev.workquotes.data.enum.WorkQuotesFrequency
import djabari.dev.workquotes.data.model.Result
import djabari.dev.workquotes.data.model.WorkQuotesConfig
import djabari.dev.workquotes.domain.repository.WorkQuotesRepository
import djabari.dev.workquotes.domain.worker.QuoteFetchWorker
import djabari.dev.workquotes.domain.worker.QuoteWorkScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import java.time.Duration
import java.time.ZoneId
import java.util.concurrent.TimeUnit

class WorkQuotesConfigViewModel(
    private val repository: WorkQuotesRepository,
    private val workScheduler: QuoteWorkScheduler,
    private val configPreferences: ConfigPreferences,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val availableTags = savedStateHandle.getStateFlow<List<String>>(
        KEY_AVAILABLE_TAGS,
        emptyList()
    )
    val quoteFrequencyList = WorkQuotesFrequency.entries.toList()
    private val _savingConfigState = MutableStateFlow<ViewState>(ViewState.Idle)
    val savingConfigState = _savingConfigState.asStateFlow()
    val selectedQuoteFrequency = savedStateHandle.getStateFlow<WorkQuotesFrequency>(
        KEY_QUOTE_FREQUENCY,
        WorkQuotesFrequency.DAILY
    )
    private val _selectedQuoteTime = MutableStateFlow(
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time
    )
    val selectedQuoteTime = _selectedQuoteTime.asStateFlow()
    val currentQuoteSize = savedStateHandle.getStateFlow(KEY_QUOTE_SIZE, 0)
    val selectedQuoteTags = savedStateHandle.getStateFlow<List<String>>(KEY_QUOTE_TAGS, emptyList())
    val configEnabled = savedStateHandle.getStateFlow(KEY_CONFIG_ENABLED, true)

    init {
        getConfig()
    }

    private fun getConfig() {
        viewModelScope.launch {
            launch {
                configPreferences.workQuotesConfig.collectLatest { config ->
                    savedStateHandle[KEY_CONFIG_ENABLED] = config.enable
                    savedStateHandle[KEY_QUOTE_FREQUENCY] = config.quoteFrequency
                    _selectedQuoteTime.value = config.quoteTime
                    savedStateHandle[KEY_QUOTE_SIZE] = config.maxNumberOfQuotes
                    savedStateHandle[KEY_QUOTE_TAGS] = config.selectedQuoteTags
                }
            }
            launch(Dispatchers.IO) {
                repository.getAvailableTags().collectLatest { result ->
                    if (result is Result.Success) {
                        val tags = result.data
                        withContext(Dispatchers.Main) {
                            savedStateHandle[KEY_AVAILABLE_TAGS] = tags
                        }
                    } else if (result is Result.Error) {
                        Log.d(
                            "WorkQuotesConfigViewModel",
                            result.throwable?.stackTraceToString() ?: ""
                        )
                    }
                }
            }
        }
    }

    fun setQuoteFrequency(frequency: WorkQuotesFrequency) {
        val frequency = frequency
        savedStateHandle[KEY_QUOTE_FREQUENCY] = frequency
    }

    fun setQuoteTime(time: LocalTime) {
        _selectedQuoteTime.value = time
    }

    fun setQuoteSize(size: Int) {
        savedStateHandle[KEY_QUOTE_SIZE] = size
    }

    fun setQuoteTags(tags: List<String>) {
        savedStateHandle[KEY_QUOTE_TAGS] = tags.map { it.replace(" ", "-") }
    }

    fun setConfigEnabled(enabled: Boolean) {
        savedStateHandle[KEY_CONFIG_ENABLED] = enabled
    }

    fun reset() {
        savedStateHandle[KEY_QUOTE_FREQUENCY] = WorkQuotesFrequency.DAILY
        _selectedQuoteTime.value =
            Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time
        savedStateHandle[KEY_QUOTE_SIZE] = 0
        savedStateHandle[KEY_QUOTE_TAGS] = emptyList<String>()
    }

    fun saveConfig() {
        viewModelScope.launch {
            _savingConfigState.value = ViewState.SavingConfig
            try {
                val newConfig = WorkQuotesConfig(
                    enable = configEnabled.value,
                    quoteFrequency = selectedQuoteFrequency.value,
                    quoteTime = selectedQuoteTime.value,
                    maxNumberOfQuotes = currentQuoteSize.value,
                    selectedQuoteTags = selectedQuoteTags.value
                )
                configPreferences.updateConfig(newConfig)
                if (newConfig.enable) {
                    workScheduler.scheduleQuoteFetch(newConfig)
                } else {
                    workScheduler.cancelQuoteFetch()
                }
                _savingConfigState.value = ViewState.SaveConfigSuccess
            } catch (e: Exception) {
                e.printStackTrace()
                _savingConfigState.value = ViewState.SaveConfigError(e.message ?: "Unknown error")
            }
        }
    }

    fun resetSavingState() {
        _savingConfigState.value = ViewState.Idle
    }

    sealed interface ViewState {
        object Idle : ViewState
        object SavingConfig : ViewState
        data class SaveConfigError(val message: String) : ViewState
        object SaveConfigSuccess : ViewState
    }

    companion object {
        const val KEY_QUOTE_FREQUENCY = "quote_frequency"
        const val KEY_CONFIG_ENABLED = "config_enabled"
        const val KEY_QUOTE_SIZE = "quote_size"
        const val KEY_QUOTE_TAGS = "quote_tags"
        const val KEY_AVAILABLE_TAGS = "available_tags"
    }
}