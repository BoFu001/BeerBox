package com.bofu.beerbox.models

data class UiState (
    val isLoading: Boolean = false,
    val hasConnection: Boolean = true,
    val errorMessage: String? = null,
    val emptyResult: Boolean = false
)