package com.example.base.common

import com.google.gson.annotations.SerializedName

data class BaseResponse<T>(
    @SerializedName("response") val data: T?,
    @SerializedName("meta") val meta: Meta?,
) {
    fun getResponseErrorCode() = meta?.code.toString()

    fun getResponseErrorMessage() =
        meta?.errorDetail ?: ""
}

data class Meta(
    @SerializedName("code") val code: Int,
    @SerializedName("errorType") val errorType: String? = null,
    @SerializedName("errorDetail") val errorDetail: String? = null,
)
