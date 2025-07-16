package com.hsact.sunplanner.domain.error

/**
 * Represents different types of API-related errors that can occur during data retrieval or processing.
 */
sealed class ApiError {

    /**
     * Indicates that the client has sent too many requests in a short amount of time.
     *
     * This error usually maps to HTTP 429 and is a rate limit imposed by the external API provider.
     * The client should wait before retrying.
     */
    object TooManyRequests : ApiError()

    /**
     * Indicates that the request was malformed or contained invalid parameters.
     *
     * @property reason Optional explanation provided by the API for the bad request.
     */
    data class BadRequest(val reason: String?) : ApiError()

    /**
     * Indicates a server-side failure (e.g. HTTP 5xx errors).
     */
    object ServerError : ApiError()

    /**
     * Indicates that the response body could not be parsed or was in an unexpected format.
     */
    object InvalidResponse : ApiError()

    /**
     * Indicates that no internet connection is available when attempting the request.
     */
    object NoInternet : ApiError()

    /**
     * Indicates that the API returned an empty or incomplete response when data was expected.
     */
    object EmptyResponse : ApiError()

    /**
     * Indicates an unknown error that does not match any other known category.
     *
     * @property throwable The underlying exception that caused the error, if available.
     */
    data class Unknown(val throwable: Throwable?) : ApiError()
}