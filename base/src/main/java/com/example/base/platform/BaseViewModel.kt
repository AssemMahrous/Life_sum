package com.example.base.platform

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.base.utils.ApplicationException
import com.example.base.utils.ApplicationMessage
import com.example.base.utils.ErrorType
import com.example.base.utils.Loading
import com.example.base.utils.LoadingStatus
import com.example.base.utils.LocalException
import com.example.base.utils.NetworkException
import com.example.base.utils.Result
import com.example.base.utils.SingleLiveEvent
import com.example.base.utils.exhaustive
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class BaseViewModel : ViewModel() {
    val error = SingleLiveEvent<Result.Error>()
    val loading =
        SingleLiveEvent<Loading>().apply {
            value = Loading(LoadingStatus.Dismiss.All)
        }
    val nextScreen = SingleLiveEvent<BaseNavigationDestination<*, *>>()
    var latestRetry: RetryOperation? = null
    val message = SingleLiveEvent<ApplicationMessage.Message>()

    inline fun wrapBlockingOperation(
        loading: Loading = Loading(LoadingStatus.Show.FullScreenNotCancelable),
        noinline function: suspend () -> Any,
    ): Job {
        this.loading.value = loading
        return viewModelScope.launch {
            try {
                function()
            } catch (throwable: Throwable) {
                latestRetry = RetryOperation(loading, function)
                handelError(throwable)
                Timber.e(throwable)
            } finally {
                hideLoading(loading)
            }
        }
    }

    inline fun <reified T : Any> wrapBlockingOperationLiveData(
        loading: Loading = Loading(LoadingStatus.Show.FullScreenNotCancelable),
        defaultOnError: T? = null,
        noinline function: suspend () -> T,
    ): LiveData<T> {
        this.loading.value = loading
        return liveData {
            try {
                val value = function()
                emit(value)
            } catch (throwable: Throwable) {
                latestRetry = RetryOperation(loading, function)
                handelError(throwable)
                defaultOnError?.let { emit(it) }
                Timber.e(throwable)
            } finally {
                hideLoading(loading)
            }
        }
    }

    fun <T> handleResult(
        result: Result<T>,
        skipAutoErrorHandlingIf: ((Result.Error) -> Boolean) = { false },
        onError: ((Result.Error) -> Unit)? = null,
        onSuccess: (Result.Success<T>) -> Unit,
    ) {
        when (result) {
            is Result.Success<T> -> onSuccess(result)
            is Result.Error -> {
                if (onError != null) onError(result)
                if (!skipAutoErrorHandlingIf(result)) {
                    throw result.exception
                } else {
                    Timber.e(result.exception)
                }
            }
        }.exhaustive
    }

    fun <T> handleResultLiveData(
        result: Result<T>,
        onError: ((Exception) -> Unit)? = null,
    ): Result.Success<T> {
        when (result) {
            is Result.Success<T> -> {
                return result
            }
            is Result.Error -> {
                onError?.invoke(result.exception)
                throw result.exception
            }
        }.exhaustive
    }

    suspend fun <T> handleResultSuspend(
        result: Result<T>,
        onSuccess: suspend (Result.Success<T>) -> Unit,
    ) {
        when (result) {
            is Result.Success<T> -> {
                onSuccess(result)
            }
            is Result.Error -> {
                throw result.exception
            }
        }.exhaustive
    }

    fun handelError(throwable: Throwable) = when (throwable) {
        is NetworkException -> handleNetworkError(throwable)
        is ApplicationException -> handleApplicationException(throwable)
        is LocalException -> handleLocalError(throwable)
        else -> TODO()
    }.exhaustive

    private fun handleLocalError(throwable: LocalException) = when (throwable.type) {
        ErrorType.Local.ContentProvider.ReadContactError -> error.postValue(
            Result.Error(throwable)
        )
        else -> error.postValue(Result.Error(throwable))
    }.exhaustive

    open fun handleNetworkError(networkException: NetworkException) =
        when (networkException.type) {
            ErrorType.Network.BadRequest -> handleBadRequest(networkException)
            // ErrorType.Network.Unexpected -> TODO()
            ErrorType.Network.NoInternetConnection -> error.postValue(
                Result.Error(networkException)
            )
            // ErrorType.Network.Unauthorized -> TODO()
            // ErrorType.Network.Forbidden -> TODO()
            // ErrorType.Network.NotFound -> TODO()
            // ErrorType.Network.MethodNotAllowed -> TODO()
            // ErrorType.Network.NotAcceptable -> TODO()
            // ErrorType.Network.PreconditionFailed -> TODO()
            // ErrorType.Network.UnsupportedMediaType -> TODO()
            // ErrorType.Network.InternalServerError -> TODO()
            else -> error.postValue(Result.Error(networkException))
        }.exhaustive

    open fun handleBadRequest(networkException: NetworkException) =
        error.postValue(Result.Error(networkException))

    open fun handleApplicationException(throwable: ApplicationException) {
        error.postValue(Result.Error(throwable))
    }

    fun clearRetry() {
        latestRetry = null
    }

    fun retry() =
        latestRetry?.apply {
            wrapBlockingOperation(showLoading, function)
        }


    fun hideLoading(loading: Loading) {
        val dismissStatus = when (loading.status) {
            LoadingStatus.Show.FullScreenNotCancelable,
            LoadingStatus.Show.FullScreenCancelable,
            -> LoadingStatus.Dismiss.FullScreen
            LoadingStatus.Show.Pagination -> LoadingStatus.Dismiss.Pagination
            LoadingStatus.Dismiss.Pagination,
            LoadingStatus.Dismiss.FullScreen,
            LoadingStatus.Dismiss.All,
            -> LoadingStatus.Dismiss.All
        }.exhaustive
        this.loading.value = Loading(dismissStatus)
    }

    override fun onCleared() {
        clearRetry()
        super.onCleared()
    }
}
