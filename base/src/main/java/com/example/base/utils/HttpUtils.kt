package com.example.base.utils

import com.example.base.R
import com.example.base.common.BaseResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import java.io.InterruptedIOException
import java.net.UnknownHostException

object HttpUtils {
    private val connectivityUtils by getKoinInstance<IConnectivityUtils>()
    private val gson by getKoinInstance<Gson>()
    private val noInternetError = Result.Error(
        NetworkException(
            type = ErrorType.Network.NoInternetConnection,
            errorMessageRes = R.string.message_error_no_internet
        )
    )
    val unexpectedError =
        Result.Error(ApplicationException(type = ErrorType.Network.Unexpected))

    suspend fun <T : Any> safeApiCall(call: suspend () -> Response<T>): Result<T> {
        return withContext(Dispatchers.IO) {
            return@withContext try {
                // check internet connection
                if (connectivityUtils.isNetworkConnected.not()) return@withContext noInternetError

                // make api call
                val response = call()

                // check response and convert to result
                return@withContext handleResult(response)

            } catch (error: Throwable) {
                Timber.e(error)
                if (error is IOException || error is InterruptedIOException || error is UnknownHostException) noInternetError
                else unexpectedError(error)
            }
        }
    }

    suspend inline fun <reified InputType : Any, reified ResponseOfInputType : BaseResponse<InputType>, reified ReturnType : Any>
            safeApiCall(
        crossinline networkCall: suspend () -> Response<ResponseOfInputType>,
        crossinline successHandler: (InputType) -> ReturnType,
    ): Result<ReturnType> {
        return when (val result = safeApiCall {
            networkCall()
        }) {
            is Result.Success -> Result.Success(successHandler(result.data.data!!))
            is Result.Error -> result
        }.exhaustive
    }

    suspend inline fun <reified InputType : Any, reified ReturnType : Any>
            safeApiCall2(
        crossinline networkCall: suspend () -> Response<InputType>,
        crossinline successHandler: (InputType) -> ReturnType,
    ): Result<ReturnType> {
        return when (val result = safeApiCall {
            networkCall()
        }) {
            is Result.Success -> Result.Success(successHandler(result.data))
            is Result.Error -> result
        }.exhaustive
    }

    suspend inline fun <reified InputType : Any, reified ReturnType : Any>
            safeApiCall3(
        crossinline networkCall: suspend () -> Response<InputType>,
        crossinline successHandler: suspend (InputType) -> ReturnType,
    ): Result<ReturnType> {
        return when (val result = safeApiCall {
            networkCall()
        }) {
            is Result.Success -> Result.Success(successHandler(result.data))
            is Result.Error -> result
        }.exhaustive
    }

    suspend fun <T : Any> safeDeferredApiCall(call: () -> Deferred<Response<T>>): Result<T> {
        return withContext(Dispatchers.IO) {
            return@withContext try {
                // check internet connection
                if (connectivityUtils.isNetworkConnected.not()) return@withContext noInternetError

                // make api call
                val response = call().await()

                // check response and convert to result
                return@withContext handleResult(response)
            } catch (error: Throwable) {
                Timber.e(error)
                unexpectedError(error)
            }
        }
    }

    private fun unexpectedError(error: Throwable): Result.Error {
        return Result.Error(
            ApplicationException(
                throwable = error,
                type = ErrorType.Network.Unexpected
            )
        )
    }

    private fun <T : Any> handleResult(response: Response<T>): Result<T> {
        return when {
            response.isSuccessful -> Result.Success(response.body()!!)
            else -> {
                val (errorCode, errorMessage) = getErrorMessage(response)
                val exception = ErrorType.Network
                    .getNetworkApplicationException(response.code(), errorMessage, errorCode)
                Result.Error(exception)
            }
        }
    }

    private fun <T> getErrorMessage(response: Response<T>): Pair<String?, String?> {
        val errorBody = response.errorBody()?.string()
        val baseResponse =
            gson.fromJson<BaseResponse<*>>(
                errorBody,
                object : TypeToken<BaseResponse<*>>() {}.type
            )
        return baseResponse.getResponseErrorCode() to baseResponse.getResponseErrorMessage()
    }
}