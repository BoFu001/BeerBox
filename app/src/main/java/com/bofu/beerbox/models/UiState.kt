package com.bofu.beerbox.models

data class UiState (
    val page: Int = 1,
    val isLoading: Boolean = false,
    val hasConnection: Boolean = true,
    val errorMessage: String? = null
)