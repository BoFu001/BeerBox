package com.bofu.beerbox.models

sealed class NetworkResult {
    data class ResponseSuccess(val data: MutableList<Beer>) : NetworkResult()
    data class Exception(val exception: Throwable): NetworkResult()
}