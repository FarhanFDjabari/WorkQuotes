package djabari.dev.workquotes.domain.repository

import android.util.Log
import androidx.paging.PagingData
import androidx.paging.map
import djabari.dev.workquotes.data.datasource.local.WorkQuotesLocalDataSource
import djabari.dev.workquotes.data.datasource.remote.WorkQuotesRemoteDataSource
import djabari.dev.workquotes.data.db.room.entity.toQuote
import djabari.dev.workquotes.data.model.Quote
import djabari.dev.workquotes.data.model.Result
import djabari.dev.workquotes.data.model.dataOrNull
import djabari.dev.workquotes.data.model.map
import djabari.dev.workquotes.data.model.resultFlow
import djabari.dev.workquotes.data.model.toEntity
import djabari.dev.workquotes.data.network.response.toQuote
import djabari.dev.workquotes.data.paging.createPager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject

interface WorkQuotesRepository {
    fun getQuotesHistory(
        search: String
    ): Flow<PagingData<Quote>>
    fun saveQuote(quote: Quote): Flow<Result<Boolean>>
    fun deleteQuote(id: String): Flow<Result<Boolean>>
    fun getRandomQuote(
        limit: Int = 1,
        tags: List<String>
    ): Flow<Result<List<Quote>>>
    fun getAvailableTags(): Flow<Result<List<String>>>
}

@Inject
class WorkQuotesRepositoryImpl(
    private val remoteDataSource: WorkQuotesRemoteDataSource,
    private val localDataSource: WorkQuotesLocalDataSource
) : WorkQuotesRepository {
    override fun getQuotesHistory(
        search: String
    ): Flow<PagingData<Quote>> {
         return createPager(
            pageSize = 25,
            enablePlaceholders = false,
            pagingSourceFactory = {
                localDataSource.getQuotesHistory(search)
            }
        ).flow.map { result ->
            result.map {
                it.toQuote()
            }
        }.catch {
             Log.d("WorkQuotesRepository", it.cause?.stackTraceToString() ?: "")
         }
    }

    override fun saveQuote(quote: Quote): Flow<Result<Boolean>> = resultFlow {
        localDataSource.saveQuoteHistory(quote.toEntity())
        Log.d("WorkQuotesRepository", "Quote inserted: ${quote.id}")
        emit(Result.Success(true))
    }

    override fun deleteQuote(id: String): Flow<Result<Boolean>> = resultFlow {
        localDataSource.deleteQuote(id)
        Log.d("WorkQuotesRepository", "Quote deleted: $id")
        emit(Result.Success(true))
    }

    override fun getRandomQuote(
        limit: Int,
        tags: List<String>
    ): Flow<Result<List<Quote>>> = resultFlow {
        val response = remoteDataSource.getRandomQuote(limit, tags)
            .map { response ->
                response.map { it.toQuote() }
            }
        emit(response)
    }

    override fun getAvailableTags(): Flow<Result<List<String>>> = resultFlow {
        val localTags = localDataSource.getQuoteTags()
        if (localTags.isNotEmpty()) {
            emit(Result.Success(localTags))
        } else {
            val response = remoteDataSource.getAvailableTags()
            response.dataOrNull?.let {
                localDataSource.saveQuoteTags(it)
            }
            emit(response)
        }
    }.catch {
        Log.d("WorkQuotesRepository", it.cause?.stackTraceToString() ?: "")
    }
}