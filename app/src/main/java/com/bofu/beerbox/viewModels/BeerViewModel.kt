package com.bofu.beerbox.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bofu.beerbox.extensions.hasConnection
import com.bofu.beerbox.models.Beer
import com.bofu.beerbox.models.NetworkResult
import com.bofu.beerbox.models.UiState
import com.bofu.beerbox.services.BeerService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.system.measureNanoTime

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
        if(checkConnection()) fetchData()
    }

    private fun fetchData() {



        // The viewModelScope is automatically canceled if the ViewModel is cleared.
        // This dispatcher is optimized to perform disk or network I/O outside of the main thread.
        viewModelScope.launch(Dispatchers.IO) {
            Log.d(TAG, "fetchData, running on thread:" + Thread.currentThread().name)
            _uiStateFlow.update {it.copy(isLoading = true)}
            _beerLiveData.postValue(beerService.getBeers())
            _uiStateFlow.update {it.copy(isLoading = false)}
        }
    }


    private fun checkConnection(): Boolean {
        val isConnected = getApplication<Application>().applicationContext.hasConnection()
        _uiStateFlow.update { it.copy(hasConnection = isConnected) }
        return isConnected
    }


    // The function used to measure execution time of Android code
    fun measureNanoTime(){
        val elapsedTime = measureNanoTime {
            fetchData()
        }
        Log.d(TAG, "measureNanoTime: $elapsedTime")

        /**
         * Calling fetchData() with Dispatchers.IO can radically improve the performant of execution of code.
         * The result has shown as follow:
         * Dispatchers.IO    measureNanoTime: 1880209, 1356771, 1435937, 1345312, 1093750, 1837500, 1757292
         * Dispatchers.Main  measureNanoTime: 5459896, 4825000, 3920313, 4184375, 5897917, 8667708, 10731250
         */
    }
}