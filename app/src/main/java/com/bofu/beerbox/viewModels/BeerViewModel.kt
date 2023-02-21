package com.bofu.beerbox.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bofu.beerbox.extensions.hasConnection
import com.bofu.beerbox.models.Beer
import com.bofu.beerbox.models.NetworkResult
import com.bofu.beerbox.models.UiState
import com.bofu.beerbox.services.BeerService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BeerViewModel(

    application: Application,
    private val beerService: BeerService,

) : AndroidViewModel(application) {

    private val TAG = javaClass.simpleName


    // For internal usage
    private val _beerLiveData = MutableLiveData<NetworkResult>(NetworkResult.ResponseSuccess(listOf<Beer>()))
    // Only publicly exposed, never mutable, as read-only LiveData
    val beerLiveData: LiveData<NetworkResult> = _beerLiveData


    // StateFlow can be exposed from ViewModel so that the View can listen for UI state updates
    // and inherently make the screen state survive configuration changes.
    private val _uiStateFlow = MutableStateFlow(UiState())
    val uiStateFlow: StateFlow<UiState> = _uiStateFlow

    init {
        fetchData()
    }

    private fun fetchData() {

        if(!checkConnection()) return

        // viewModelScope is automatically canceled if the ViewModel is cleared.
        viewModelScope.launch {

            _uiStateFlow.update {it.copy(isLoading = true)}
            _beerLiveData.value = beerService.getBeers()
            _uiStateFlow.update {it.copy(isLoading = false)}
        }
    }


    private fun checkConnection(): Boolean {
        val isConnected = getApplication<Application>().applicationContext.hasConnection()
        _uiStateFlow.update { it.copy(hasConnection = isConnected) }
        return isConnected
    }

}