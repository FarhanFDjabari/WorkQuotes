package djabari.dev.workquotes

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import djabari.dev.workquotes.di.WorkQuotesComponent
import djabari.dev.workquotes.di.workQuotesComponent

class WorkQuotesApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        WorkQuotesComponent.init(this)
        val config = Configuration.Builder()
            .setWorkerFactory(workQuotesComponent().workQuoteWorkerFactory)
            .build()

        WorkManager.initialize(this, config)
    }
}
