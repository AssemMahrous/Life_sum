package com.example.base.platform

import com.example.base.common.BaseResponse
import com.example.base.common.IApplicationContext
import com.example.base.common.RemoteDataSource
import com.example.base.utils.HttpUtils
import com.example.base.utils.Result
import com.example.base.utils.getKoinInstance
import com.google.gson.Gson
import kotlinx.coroutines.Deferred
import retrofit2.Response

abstract class BaseRepository<RemoteData : RemoteDataSource>
    (val remoteDataSource: RemoteData) : IBaseRepository {
    val applicationContext by getKoinInstance<IApplicationContext>()
    val gson by getKoinInstance<Gson>()

    override suspend fun <T : Any> safeApiCall(call: suspend () -> Response<T>): Result<T> {
        return HttpUtils.safeApiCall(call)
    }

    override suspend fun <T : Any> safeDeferredApiCall(call: () -> Deferred<Response<T>>): Result<T> {
        return HttpUtils.safeDeferredApiCall(call)
    }

    suspend inline fun <reified InputType : Any, reified ResponseOfInputType : BaseResponse<InputType>, reified ReturnType : Any>
            safeApiCall(
        crossinline networkCall: suspend () -> Response<ResponseOfInputType>,
        crossinline successHandler: (InputType) -> ReturnType,
    )
            : Result<ReturnType> {
        return HttpUtils.safeApiCall(networkCall, successHandler)
    }

    suspend inline fun <reified InputType : Any, reified ReturnType : Any> safeApiCall2(
        crossinline networkCall: suspend () -> Response<InputType>,
        crossinline successHandler: (InputType) -> ReturnType,
    ): Result<ReturnType> {
        return HttpUtils.safeApiCall2(networkCall, successHandler)
    }

    suspend inline fun <reified InputType : Any, reified ReturnType : Any> safeApiCall3(
        crossinline networkCall: suspend () -> Response<InputType>,
        crossinline successHandler: suspend (InputType) -> ReturnType,
    ): Result<ReturnType> {
        return HttpUtils.safeApiCall3(networkCall, successHandler)
    }
}