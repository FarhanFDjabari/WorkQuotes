package djabari.dev.workquotes.di

import djabari.dev.workquotes.datasource.FakeLocalDataSource
import djabari.dev.workquotes.datasource.FakeRemoteDataSource
import djabari.dev.workquotes.data.datasource.local.WorkQuotesLocalDataSource
import djabari.dev.workquotes.data.datasource.remote.WorkQuotesRemoteDataSource
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

class TestFakes(
    @get:Provides val remoteDataSource: WorkQuotesRemoteDataSource = FakeRemoteDataSource(),
    @get:Provides val localDataSource: WorkQuotesLocalDataSource = FakeLocalDataSource()
)

@Component
abstract class TestWorkQuotesComponent(@Component val fakes: TestFakes = TestFakes())