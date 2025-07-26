package djabari.dev.workquotes.data.db.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import djabari.dev.workquotes.data.enum.WorkQuotesFrequency
import djabari.dev.workquotes.data.model.WorkQuotesConfig
import djabari.dev.workquotes.data.serializer.WorkQuotesConfigSerializer
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import me.tatarka.inject.annotations.Inject
import java.io.File
import java.util.concurrent.atomic.AtomicReference

@Inject
class ConfigPreferences(private val context: Context) {
    val Context.datastore : DataStore<WorkQuotesConfig> by lazy {
        DataStoreFactory.create(
            serializer = WorkQuotesConfigSerializer(),
            produceFile = {
                File("${context.cacheDir.path}/$dataStoreFileName")
            },
            corruptionHandler = ReplaceFileCorruptionHandler {
                WorkQuotesConfig(
                    enable = false,
                    quoteFrequency = WorkQuotesFrequency.DAILY,
                    quoteTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time,
                    maxNumberOfQuotes = 1,
                    selectedQuoteTags = emptyList()
                )
            }
        )
    }

    val workQuotesConfig: Flow<WorkQuotesConfig> = context.datastore.data

    suspend fun updateConfig(config: WorkQuotesConfig) {
        context.datastore.updateData {
            it.copy(
                enable = config.enable,
                quoteFrequency = config.quoteFrequency,
                quoteTime = config.quoteTime,
                maxNumberOfQuotes = config.maxNumberOfQuotes,
                selectedQuoteTags = config.selectedQuoteTags
            )
        }
    }

    companion object {
        private val instance: AtomicReference<ConfigPreferences?> = AtomicReference(null)

        fun getInstance(context: Context): ConfigPreferences {
            var currentInstance = instance.get()
            if (currentInstance == null) {
                currentInstance = ConfigPreferences(context)
                instance.compareAndSet(null, currentInstance)
            }
            return currentInstance
        }
    }
}

internal const val dataStoreFileName = "work_quotes.preferences_pb"