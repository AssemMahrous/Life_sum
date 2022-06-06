package com.example.base.platform

import com.example.base.utils.Result
import kotlinx.coroutines.Deferred
import retrofit2.Response

interface IBaseRepository {
    suspend fun <T : Any> safeApiCall(call: suspend () -> Response<T>): Result<T>
    suspend fun <T : Any> safeDeferredApiCall(call: () -> Deferred<Response<T>>): Result<T>
}