package djabari.dev.workquotes.data.paging

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class DefaultPagingSource<T : Any>(
    private val coroutineContext: CoroutineContext = EmptyCoroutineContext,
    private val dataFetcher: suspend (LoadParams<Int>) -> List<T>
) : PagingSource<Int, T>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        return withContext(coroutineContext) {
            try {
                val position = params.defaultKey()
                val pageData = dataFetcher(params)
                val nextKey = if (pageData.isEmpty()) {
                    null
                } else {
                    position.plus(1)
                }
                LoadResult.Page(
                    data = pageData,
                    prevKey = if (position == 1) null else position.minus(1),
                    nextKey = nextKey
                )
            } catch (t: Throwable) {
                t.printStackTrace()
                LoadResult.Error(t)
            }
        }
    }

    // The refresh key is used for the initial load of the next PagingSource, after invalidation
    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        // We need to get the previous key (or next key if previous is null) of the page
        // that was closest to the most recently accessed index.
        // Anchor position is the most recently accessed index
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}

fun <V : Any> createDefaultPager(
    pageSize: Int = 25,
    enablePlaceholders: Boolean = false,
    dataFetcher: suspend (PagingSource.LoadParams<Int>) -> List<V>
): Pager<Int, V> = createPager(pageSize = pageSize, enablePlaceholders = enablePlaceholders) {
    DefaultPagingSource(dataFetcher = dataFetcher)
}

fun <T : Any, V : Any> createPager(
    pageSize: Int = 25,
    enablePlaceholders: Boolean = false,
    pagingSourceFactory: () -> PagingSource<T, V>
): Pager<T, V> =
    Pager(
        PagingConfig(
            pageSize = pageSize,
            initialLoadSize = pageSize,
            enablePlaceholders = enablePlaceholders
        ),
        pagingSourceFactory = pagingSourceFactory
    )

fun PagingSource.LoadParams<Int>.defaultKey(): Int = key ?: 1