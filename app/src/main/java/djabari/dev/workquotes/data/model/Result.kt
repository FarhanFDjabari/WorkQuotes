package djabari.dev.workquotes.data.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

sealed interface Result<out T> {
    @JvmInline
    value class Success<out T>(val data: T) : Result<T>

    @JvmInline
    value class Error(val throwable: Throwable?) : Result<Nothing>

    data object Loading : Result<Nothing>
}

inline fun <T, R> Result<T>.map(block: (T) -> R): Result<R> {
    return when (this) {
        is Result.Success -> {
            Result.Success(block(this.data))
        }

        is Result.Loading -> this
        is Result.Error -> this
    }
}

inline fun <T, R> Result<T>.mapTrySuccess(block: (T) -> Result<R>): Result<R> {
    return when (this) {
        is Result.Success -> {
            block(this.data)
        }

        is Result.Loading -> this
        is Result.Error -> this
    }
}

/**
 * Scope which is used by [resultFlow] operator which simplifies the invocation of
 * ```
 * flow<Result<T>> {
 *      emit(
 *          Result.Success(T)
 *      )
 * }
 * ```
 * into
 * ```
 * resultFlow<T> {
 *      emitSuccess(T)
 * }
 * ```
 */
open class ResultFlowCollector<in T>(private val flowCollector: FlowCollector<Result<T>>) :
    FlowCollector<Result<T>> by flowCollector {
    suspend fun emitSuccess(data: T) = emit(Result.Success(data))
    suspend fun emitLoading() = emit(Result.Loading)
    suspend fun emitError(e: Throwable? = null) = emit(Result.Error(e))
}

fun interface ResultProgressEmitter {
    suspend fun emitProgress(progress: Int, total: Int)
}

fun <T> resultFlow(
    block: suspend ResultFlowCollector<T>.() -> Unit
): Flow<Result<T>> = resultFlowTo(transform = { it }, block)

/**
 * Emits [Result.Loading] before [block] and catches any [Throwable] occurs in [block]
 * then emits it as [Result.Error].
 */
fun <T, R> resultFlowTo(
    transform: (Result<T>) -> R,
    block: suspend ResultFlowCollector<T>.() -> Unit
): Flow<R> {
    return flow<Result<T>> {
        resultCollector().apply {
            emitLoading()
            block()
        }
    }.catch { e ->
        resultCollector().apply {
            emitError(e)
        }
    }.map(transform)
}

fun <T> resultFlowOf(
    block: suspend () -> T
): Flow<Result<T>> = resultFlowOf(transform = { it }, block = block)

fun <T, R> resultFlowOf(
    transform: (Result<T>) -> R, block: suspend () -> T
): Flow<R> = flow {
    emit(transform(Result.Loading))
    emit(transform(Result.Success(block())))
}.catch { t ->
    emit(transform(Result.Error(t)))
}

fun <T> FlowCollector<Result<T>>.resultCollector(): ResultFlowCollector<T> =
    ResultFlowCollector(this)

/**
 * Gets the current [Result.Success.data] or null if this [Result] is not [Result.Success].
 */
val <T> Result<T>.dataOrNull: T?
    get() = if (this is Result.Success) data else null

val Result<Boolean>.isSuccess: Boolean get() = this is Result.Success && data

fun <T: Any> Result<T?>.filterNotNull(): Result<T> = mapTrySuccess { data ->
    data?.let { Result.Success(it) }
        ?: Result.Error(
            NoSuchElementException()
        )
}

fun <T: Any> Flow<Result<T?>>.filterResultNotNull(): Flow<Result<T>> = map { res ->
    res.filterNotNull()
}