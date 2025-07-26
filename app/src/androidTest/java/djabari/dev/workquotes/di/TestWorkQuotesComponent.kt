package djabari.dev.workquotes.di

import androidx.work.WorkerFactory
import djabari.dev.workquotes.data.datasource.local.WorkQuotesLocalDataSource
import djabari.dev.workquotes.data.datasource.remote.WorkQuotesRemoteDataSource
import djabari.dev.workquotes.datasource.FakeLocalDataSource
import djabari.dev.workquotes.datasource.FakeRemoteDataSource
import djabari.dev.workquotes.domain.notification.FakeNotificationHelper
import djabari.dev.workquotes.domain.notification.NotificationHelper
import djabari.dev.workquotes.domain.repository.WorkQuotesRepository
import djabari.dev.workquotes.domain.repository.WorkQuotesRepositoryImpl
import djabari.dev.workquotes.domain.worker.ChildWorkerFactory
import djabari.dev.workquotes.domain.worker.QuoteAppWorkerFactory
import djabari.dev.workquotes.domain.worker.QuoteWorkerFactory
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

class TestFakes(
    @get:Provides val remoteDataSource: WorkQuotesRemoteDataSource = FakeRemoteDataSource(),
    @get:Provides val localDataSource: WorkQuotesLocalDataSource = FakeLocalDataSource(),
    @get:Provides val notificationHelper: NotificationHelper = FakeNotificationHelper(),
    @get:Provides val workerRepository: WorkQuotesRepository = WorkQuotesRepositoryImpl(
        remoteDataSource = remoteDataSource,
        localDataSource = localDataSource
    ),
    @get:Provides val quoteWorkerFactory: QuoteWorkerFactory = QuoteWorkerFactory(
        quoteRepository = workerRepository,
        notificationHelper = notificationHelper
    ),
    @get:Provides val workerFactoriesMap: Map<Class<out ChildWorkerFactory>, ChildWorkerFactory> = mapOf(
        QuoteWorkerFactory::class.java to quoteWorkerFactory
    ),
    @get:Provides val workerFactory: WorkerFactory = QuoteAppWorkerFactory(
        workerFactories = workerFactoriesMap
    )
)

@Component
abstract class TestWorkQuotesComponent(@Component val fakes: TestFakes = TestFakes())