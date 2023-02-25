package com.bofu.beerbox.models

sealed class NetworkResult {
    data class ResponseSuccess(val data: List<Beer>) : NetworkResult()
    data class Exception(val exception: Throwable): NetworkResult()
}