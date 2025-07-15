package com.hsact.sunplanner.domain.error

sealed class ApiError {
    object TooManyRequests : ApiError()
    data class BadRequest(val reason: String?) : ApiError()
    object ServerError : ApiError()
    object InvalidResponse : ApiError()
    object NoInternet : ApiError()
    object EmptyResponse : ApiError()
    data class Unknown(val throwable: Throwable?) : ApiError()
}