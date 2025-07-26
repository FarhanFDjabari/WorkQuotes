package djabari.dev.workquotes.domain.worker

import android.content.Context
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import djabari.dev.workquotes.data.enum.WorkQuotesFrequency
import djabari.dev.workquotes.data.model.WorkQuotesConfig
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Inject
import java.util.Calendar
import java.util.concurrent.TimeUnit

@Inject
class QuoteWorkScheduler(
    private val context: Context,
    private val workManager: WorkManager = WorkManager.getInstance(context)
) {
    companion object {
        private const val QUOTE_WORK_TAG = "quote_fetch_work"
        private const val QUOTE_WORK_NAME = "quote_fetch_work"
    }

    fun scheduleQuoteFetch(config: WorkQuotesConfig) {
        // Cancel existing work
        workManager.cancelUniqueWork(QUOTE_WORK_NAME)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val inputData = Data.Builder()
            .putString("work_quotes_config", Json.encodeToString(config))
            .build()

        val currentTime = Calendar.getInstance()
        val scheduledTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, config.quoteTime.hour)
            set(Calendar.MINUTE, config.quoteTime.minute)
            set(Calendar.SECOND, config.quoteTime.second)

            // If the time has already passed today, schedule for tomorrow
            if (timeInMillis <= currentTime.timeInMillis) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        val initialDelay = scheduledTime.timeInMillis - currentTime.timeInMillis

        val repeatInterval = when (config.quoteFrequency) {
            WorkQuotesFrequency.DAILY -> 1L
            WorkQuotesFrequency.WEEKLY -> 7L
            WorkQuotesFrequency.MONTHLY -> 30L
        }

        val workRequest = PeriodicWorkRequestBuilder<QuoteFetchWorker>(
            repeatInterval = repeatInterval,
            repeatIntervalTimeUnit = TimeUnit.DAYS
        )
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .addTag(QUOTE_WORK_TAG)
            .setBackoffCriteria(
                backoffPolicy = BackoffPolicy.LINEAR,
                backoffDelay = 15,
                timeUnit = TimeUnit.MINUTES
            )
            .setInputData(inputData)
            .build()

        workManager.enqueueUniquePeriodicWork(
            QUOTE_WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )

        // Log the scheduling for debugging purposes
        Log.d("WorkQuotes-DEBUG", "Scheduled quote fetch with config: $config")
        Log.d("WorkQuotes-DEBUG", "Scheduled time: ${scheduledTime.time}")
        Log.d("WorkQuotes-DEBUG", "Initial delay: $initialDelay ms")
        Log.d("WorkQuotes-DEBUG", "Repeat interval: $repeatInterval days")
        Log.d("WorkQuotes-DEBUG", "Work request ID: ${workRequest.id}")
        Log.d("WorkQuotes-DEBUG", "Work request tags: ${workRequest.tags}")

    }

    fun cancelQuoteFetch() {
        Log.d("WorkQuotes-DEBUG", "Cancelling scheduled quote fetch work $QUOTE_WORK_NAME via cancelQuoteFetch")
        workManager.cancelUniqueWork(QUOTE_WORK_NAME)
    }
}
