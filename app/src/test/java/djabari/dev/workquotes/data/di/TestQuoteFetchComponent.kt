package djabari.dev.workquotes.data.di

import djabari.dev.workquotes.data.datasource.FakeRemoteDataSource
import djabari.dev.workquotes.data.datasource.remote.WorkQuotesRemoteDataSource
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

class TestFakes(
    @get:Provides val remoteDataSource: WorkQuotesRemoteDataSource = FakeRemoteDataSource()
)

@Component
abstract class TestQuoteFetchComponent(@Component val fakes: TestFakes = TestFakes())