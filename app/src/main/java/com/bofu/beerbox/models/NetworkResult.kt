package com.bofu.beerbox.models

sealed class NetworkResult {
    data class ResponseSuccess<T: Any>(val data: T?) : NetworkResult()
    data class Exception(val exception: Throwable): NetworkResult()
}