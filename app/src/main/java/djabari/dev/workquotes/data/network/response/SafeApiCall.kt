package djabari.dev.workquotes.data.network.response

import android.util.Log
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import kotlinx.coroutines.ensureActive
import kotlinx.serialization.SerializationException
import djabari.dev.workquotes.data.model.Result
import kotlin.coroutines.coroutineContext

internal suspend inline fun <reified T> safeApiCall(execute: () -> T) : Result<T> {
    try {
        return Result.Success(execute())
    } catch(e: SocketTimeoutException) {
        Log.e("SafeApiCall", "SocketTimeoutException:  ${e.message ?: e.cause?.message ?: "Unknown error"}", e)
        return Result.Error(SocketTimeoutException(message = "Socket Timeout", cause = e.cause))
    } catch(e: ConnectTimeoutException) {
        Log.e("SafeApiCall", "ConnectTimeoutException:  ${e.message ?: e.cause?.message ?: "Unknown error"}", e)
        return Result.Error(ConnectTimeoutException(message = "Connection Timeout", cause = e.cause))
    } catch (e: SerializationException) {
        Log.e("SafeApiCall", "SerializationException:  ${e.message ?: e.cause?.message ?: "Unknown error"}", e)
        return Result.Error(SerializationException(message = "Serialization Error", cause = e.cause))
    } catch (e: Exception) {
        coroutineContext.ensureActive()
        Log.e("SafeApiCall", "Exception:  ${e.message ?: e.cause?.message ?: "Unknown error"}", e)
        return Result.Error(SocketTimeoutException(message = e.message ?: "Unknown error", cause = e.cause))
    }
}