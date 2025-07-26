package djabari.dev.workquotes.domain.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import djabari.dev.workquotes.domain.notification.NotificationHelper
import djabari.dev.workquotes.domain.repository.WorkQuotesRepository
import me.tatarka.inject.annotations.Inject

class QuoteWorkerFactory @Inject constructor(
    private val quoteRepository: WorkQuotesRepository,
    private val notificationHelper: NotificationHelper
) : ChildWorkerFactory {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            QuoteFetchWorker::class.java.name -> {
                QuoteFetchWorker(appContext, workerParameters, quoteRepository, notificationHelper)
            }
            else -> null
        }
    }
}

interface ChildWorkerFactory {
    fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker?
}

class QuoteAppWorkerFactory @Inject constructor(
    private val workerFactories: Map<Class<out ChildWorkerFactory>, ChildWorkerFactory>
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        val foundEntry = workerFactories.entries.find {
            Class.forName(workerClassName).isAssignableFrom(it.key)
        }
        val factoryProvider = foundEntry?.value
            ?: workerFactories[QuoteWorkerFactory::class.java]
            ?: throw IllegalArgumentException("Unknown worker class name: $workerClassName")

        return factoryProvider.createWorker(appContext, workerClassName, workerParameters)
    }
}