package djabari.dev.workquotes.di

import android.content.Context
import androidx.work.WorkerFactory
import djabari.dev.workquotes.data.datasource.local.WorkQuotesLocalDataSource
import djabari.dev.workquotes.data.datasource.local.WorkQuotesLocalDataSourceImpl
import djabari.dev.workquotes.data.datasource.remote.WorkQuotesRemoteDataSource
import djabari.dev.workquotes.data.datasource.remote.WorkQuotesRemoteDataSourceImpl
import djabari.dev.workquotes.data.db.datastore.ConfigPreferences
import djabari.dev.workquotes.data.db.room.WorkQuotesDatabase
import djabari.dev.workquotes.data.db.room.WorkQuotesDatabaseClient
import djabari.dev.workquotes.data.db.room.dao.WorkQuotesDao
import djabari.dev.workquotes.data.network.HttpClientFactory
import djabari.dev.workquotes.domain.notification.NotificationHelperImpl
import djabari.dev.workquotes.domain.repository.WorkQuotesRepository
import djabari.dev.workquotes.domain.repository.WorkQuotesRepositoryImpl
import djabari.dev.workquotes.domain.worker.ChildWorkerFactory
import djabari.dev.workquotes.domain.worker.QuoteAppWorkerFactory
import djabari.dev.workquotes.domain.worker.QuoteWorkScheduler
import djabari.dev.workquotes.domain.worker.QuoteWorkerFactory
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import me.tatarka.inject.annotations.Scope
import java.util.concurrent.atomic.AtomicReference

@Scope
@Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.TYPEALIAS
)
internal annotation class Singleton

@Singleton
@Component
internal abstract class WorkQuotesComponent(@get:Provides val context: Context) {
    abstract val httpClient: HttpClient
    abstract val workQuotesDatabase: WorkQuotesDatabase
    abstract val workQuotesRemoteDataSource: WorkQuotesRemoteDataSource
    abstract val workQuotesLocalDataSource: WorkQuotesLocalDataSource
    abstract val workQuotesRepository: WorkQuotesRepository
    abstract val notificationHelper: NotificationHelperImpl
    abstract val workQuoteWorkScheduler: QuoteWorkScheduler
    abstract val workQuoteWorkerFactory: WorkerFactory

    val provideDefaultJsonConvert: Json
        @Provides get() = Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        }

    @Singleton
    @Provides
    fun providesConfigPreference(): ConfigPreferences = ConfigPreferences.getInstance(context)

    val HttpClientFactory.bind: HttpClient
        @Singleton @Provides get() = this.httpClient

    val WorkQuotesDatabaseClient.bind: WorkQuotesDatabase
        @Singleton @Provides get() = this.database

    val WorkQuotesRemoteDataSourceImpl.bind: WorkQuotesRemoteDataSource
        @Singleton @Provides get() = this

    val WorkQuotesLocalDataSourceImpl.bind: WorkQuotesLocalDataSource
        @Singleton @Provides get() = this

    val WorkQuotesRepositoryImpl.bind: WorkQuotesRepository
        @Provides get() = this

    @Singleton
    @Provides
    fun providesWorkQuotesDao(): WorkQuotesDao = workQuotesDatabase.getWorkQuotesDao()

    @Provides
    @Singleton
    fun provideNotificationHelper(context: Context): NotificationHelperImpl {
        return NotificationHelperImpl(context)
    }

    @Provides
    @Singleton
    fun provideQuoteWorkScheduler(context: Context): QuoteWorkScheduler {
        return QuoteWorkScheduler(context)
    }

    @Provides
    @Singleton
    fun provideQuoteWorkerFactory(
        repository: WorkQuotesRepository,
        notificationHelper: NotificationHelperImpl
    ): QuoteWorkerFactory {
        return QuoteWorkerFactory(repository, notificationHelper)
    }

    @Provides
    @Singleton
    fun provideWorkerFactoryMap(
        quoteWorkerFactory: QuoteWorkerFactory
    ): Map<Class<out ChildWorkerFactory>, ChildWorkerFactory> {
        return mapOf(
            QuoteWorkerFactory::class.java to quoteWorkerFactory
        )
    }

    @Provides
    @Singleton
    fun provideQuoteAppWorkerFactory(
        workerFactories: Map<Class<out ChildWorkerFactory>, ChildWorkerFactory>
    ): WorkerFactory {
        return QuoteAppWorkerFactory(workerFactories)
    }

    companion object {
        private val instance: AtomicReference<WorkQuotesComponent?> = AtomicReference(null)

        val currentInstance: WorkQuotesComponent? get() = instance.get()

        internal fun init(context: Context): WorkQuotesComponent {
            var tempInstance = instance.get()
            if (tempInstance == null) {
                tempInstance = WorkQuotesComponent::class.create(context)
                instance.compareAndSet(null, tempInstance)
            }
            return tempInstance
        }
    }
}

internal fun workQuotesComponent(): WorkQuotesComponent = checkNotNull(WorkQuotesComponent.currentInstance) {
    "${WorkQuotesComponent::class.simpleName} is not initialized"
}