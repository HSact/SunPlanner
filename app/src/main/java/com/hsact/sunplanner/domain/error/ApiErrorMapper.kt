package com.hsact.sunplanner.domain.error

import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Maps a [Throwable] instance to a corresponding [ApiError] type.
 *
 * This function is used to interpret exceptions that occur during network or data operations
 * and convert them into domain-specific error types that can be handled consistently in the app.
 *
 * ## Mapping rules:
 * - [UnknownHostException], [ConnectException], or [SocketTimeoutException] → [ApiError.NoInternet]
 * - [HttpException] with:
 *   - HTTP 429 → [ApiError.TooManyRequests]
 *   - HTTP 5xx → [ApiError.ServerError]
 *   - HTTP 4xx → [ApiError.BadRequest] (includes message from the exception)
 *   - Other HTTP codes → [ApiError.Unknown]
 * - [JsonDataException], [JsonEncodingException] → [ApiError.InvalidResponse]
 * - All other cases → [ApiError.Unknown]
 *
 * @return The corresponding [ApiError] for this [Throwable].
 */
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