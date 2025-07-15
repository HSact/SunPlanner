package com.hsact.sunplanner.domain.error

import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun Throwable.toApiError(): ApiError = when (this) {
    is UnknownHostException, is ConnectException, is SocketTimeoutException -> ApiError.NoInternet
    is HttpException -> when (code()) {
        429 -> ApiError.TooManyRequests
        in 500..599 -> ApiError.ServerError
        in 400..499 -> ApiError.BadRequest(message())
        else -> ApiError.Unknown(this)
    }
    is JsonDataException, is JsonEncodingException -> ApiError.InvalidResponse
    else -> ApiError.Unknown(this)
}