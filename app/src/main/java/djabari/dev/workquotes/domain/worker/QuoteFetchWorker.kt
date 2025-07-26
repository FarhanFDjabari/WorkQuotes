package djabari.dev.workquotes.domain.worker

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import djabari.dev.workquotes.data.model.Quote
import djabari.dev.workquotes.data.model.WorkQuotesConfig
import djabari.dev.workquotes.domain.notification.NotificationHelper
import djabari.dev.workquotes.domain.repository.WorkQuotesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json


class QuoteFetchWorker(
    context: Context,
    params: WorkerParameters,
    private val workQuotesRepository: WorkQuotesRepository,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, params) {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {
        return try {
            val config = getConfig() ?: return Result.failure()
            Log.d("WorkQuotes-DEBUG", "Config fetched: $config")
            if (!config.enable) return Result.success()
            Log.d("WorkQuotes-DEBUG", "Fetching quotes with config: $config")
            val result = fetchQuote(config)
            if (result is djabari.dev.workquotes.data.model.Result.Success) {
                result.data.forEach { quote ->
                    saveToLocal(quote)
                    notificationHelper.showQuoteNotification(quote)
                }
                Log.d("WorkQuotes-DEBUG", "Fetched and saved quotes successfully.")
                Result.success()
            } else {
                // Log the error or handle it accordingly
                Log.d("WorkQuotes-DEBUG", "Current Result State: $result")
                val error = result as? djabari.dev.workquotes.data.model.Result.Error
                Log.d("WorkQuotes-DEBUG", "Failed to fetch quotes: ${error?.throwable}")
                Result.retry()
            }
        } catch (e: Exception) {
            Log.d("WorkQuotes-DEBUG", "Error in QuoteFetchWorker: ${e.message}")
            Result.failure()
        }
    }

    private fun getConfig(): WorkQuotesConfig? {
        val configData = inputData.getString(CONFIG_KEY)?.let {
            Json.decodeFromString<WorkQuotesConfig>(it)
        }
        return configData
    }

    private suspend fun fetchQuote(config: WorkQuotesConfig): djabari.dev.workquotes.data.model.Result<List<Quote>> {
        return workQuotesRepository.getRandomQuote(
            limit = config.maxNumberOfQuotes,
            tags = config.selectedQuoteTags
        ).firstOrNull {
            it !is djabari.dev.workquotes.data.model.Result.Loading
        } ?: djabari.dev.workquotes.data.model.Result.Error(
            Exception("Failed to fetch quotes")
        )
    }

    private suspend fun saveToLocal(quote: Quote) {
        withContext(Dispatchers.IO) {
            Log.d("WorkQuotes-DEBUG", "Saving Quote to local: ${quote.content}")
            workQuotesRepository.saveQuote(quote).firstOrNull {
                it !is djabari.dev.workquotes.data.model.Result.Loading
            }
        }
    }

    companion object {
        const val CONFIG_KEY = "work_quotes_config"
    }
}