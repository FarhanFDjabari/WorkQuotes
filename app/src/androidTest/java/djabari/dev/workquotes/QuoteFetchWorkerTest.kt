package djabari.dev.workquotes

import android.content.Context
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.Configuration
import androidx.work.Data
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.TestDriver
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.testing.TestWorkerBuilder
import androidx.work.testing.WorkManagerTestInitHelper
import djabari.dev.workquotes.data.enum.WorkQuotesFrequency
import djabari.dev.workquotes.data.model.WorkQuotesConfig
import djabari.dev.workquotes.di.TestWorkQuotesComponent
import djabari.dev.workquotes.di.create
import djabari.dev.workquotes.domain.worker.QuoteFetchWorker
import djabari.dev.workquotes.domain.worker.QuoteFetchWorkerFactory
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import org.junit.Before
import org.junit.Test
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class QuoteFetchWorkerTest {

    private lateinit var context: Context
    private lateinit var testDriver: TestDriver
    private lateinit var testWorkQuotesComponent: TestWorkQuotesComponent

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        testWorkQuotesComponent = TestWorkQuotesComponent::class.create()
        val workQuoteFetcherWorkerFactory = QuoteFetchWorkerFactory(
            testWorkQuotesComponent.fakes.remoteDataSource,
            testWorkQuotesComponent.fakes.localDataSource
        )
        val config = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setWorkerFactory(workQuoteFetcherWorkerFactory)
            .build()
        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)

        WorkManagerTestInitHelper.getTestDriver(context)?.let {
            testDriver = it
        }
    }

    @Test
    fun testDailyQuoteWorker() = runBlocking {
        val request = PeriodicWorkRequestBuilder<QuoteFetchWorker>(15, TimeUnit.MINUTES)
            .setInputData(
                Data.Builder().put(
                    "work_quotes_config",
                    Json.encodeToString(
                        WorkQuotesConfig(
                            quoteFrequency = WorkQuotesFrequency.DAILY,
                            quoteTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time,
                            maxNumberOfQuotes = 1,
                            selectedQuoteTags = emptyList()
                        )
                    )
                )
                .build()
            )
            .build()

        val workManager = WorkManager.getInstance(context)
        workManager.enqueue(request).result.get()

        testDriver.setPeriodDelayMet(request.id)

        val workInfo = workManager.getWorkInfoById(request.id).get()
        assertTrue(workInfo?.state?.isFinished == true)
    }
}