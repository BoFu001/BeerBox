package com.bofu.beerbox.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bofu.beerbox.R
import com.bofu.beerbox.extensions.hasConnection
import com.bofu.beerbox.models.Beer
import com.bofu.beerbox.models.Filter
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
    private var page = 1

    // For internal usage
    private var _beers = mutableListOf<Beer>()
    private val _beerLiveData = MutableLiveData(_beers)
    // Only publicly exposed, never mutable, as read-only LiveData
    val beerLiveData: LiveData<MutableList<Beer>> = _beerLiveData


    // StateFlow can be exposed from ViewModel so that the View can listen for UI state updates
    // and inherently make the screen state survive configuration changes.
    private val _uiStateFlow = MutableStateFlow(UiState())
    val uiStateFlow: StateFlow<UiState> = _uiStateFlow

    private var _filter = mutableListOf<Filter>()
    private val _filterLiveData = MutableLiveData(_filter)
    val filterLiveData: LiveData<MutableList<Filter>> = _filterLiveData

    init {
        fetchData()
        getFilter()
    }

    fun fetchData(addend: Int = UNCHANGED) {

        // Check connect before
        if(!checkConnection()) return
        // Fetch data only when it's not busy
        if(_uiStateFlow.value.isLoading) return

        // The viewModelScope is automatically canceled if the ViewModel is cleared.
        // This dispatcher is optimized to perform disk or network I/O outside of the main thread.
        viewModelScope.launch(Dispatchers.IO) {

            Log.d(TAG, "fetchData, running on thread:" + Thread.currentThread().name)

            _uiStateFlow.update {it.copy(errorMessage = null)}
            _uiStateFlow.update {it.copy(isLoading = true)}

            val sum = pageCheck(page + addend)

            when(val networkResult: NetworkResult = beerService.getBeers(sum)){
                is NetworkResult.ResponseSuccess -> {
                    _beers = networkResult.data
                    _beerLiveData.postValue(_beers)
                    page = sum
                }
                is NetworkResult.Exception -> {
                    _uiStateFlow.update {it.copy(errorMessage = networkResult.exception.message)}
                }
            }

            _uiStateFlow.update {it.copy(isLoading = false)}
        }
    }


    private fun checkConnection(): Boolean {
        val isConnected = getApplication<Application>().applicationContext.hasConnection()
        _uiStateFlow.update { it.copy(hasConnection = isConnected) }
        return isConnected
    }

    private fun pageCheck(sum: Int): Int{
        return if(sum == 0) {
            MINIMUM_PAGE
        } else {
            sum
        }
    }

    private fun getFilter(){
        viewModelScope.launch(Dispatchers.IO) {
            val tagNames = getApplication<Application>().applicationContext.resources.getStringArray(R.array.beer_tags)

            tagNames.forEach {
                _filter.add(Filter(it, false))
            }

            _filterLiveData.postValue(_filter)
        }
    }
    
    fun setFilter(selectedFilter: Filter){
        viewModelScope.launch(Dispatchers.IO) {

            _filter.map {
                if (it.name.lowercase() == selectedFilter.name.lowercase()) {
                    it.isChecked = !selectedFilter.isChecked
                } else {
                    it.isChecked = false
                }
            }
            _filterLiveData.postValue(_filter)
            performFilter(_beers)
        }
    }

    private fun performFilter(beers: MutableList<Beer>?){

        viewModelScope.launch(Dispatchers.IO) {

            if(hasNoFilter()){
                _beerLiveData.postValue(beers)
            } else {
                val keyword = _filter.single { it.isChecked }.name
                val filteredBeers = beers?.filter{
                    it.name.lowercase().contains(keyword.lowercase()) ||
                    it.tagline.lowercase().contains(keyword.lowercase()) ||
                    it.description.contains(keyword.lowercase())
                } as MutableList<Beer>

                _beerLiveData.postValue(filteredBeers)
            }
        }
    }

    fun hasNoFilter(): Boolean{
        return _filter.none { it.isChecked }
    }

    fun search(p0: String?) {
        viewModelScope.launch(Dispatchers.IO) {

            if(p0 == null){
                performFilter(_beers)
            } else {
                val filteredBeers = _beers.filter{
                    it.name.lowercase().contains(p0.lowercase())
                } as MutableList<Beer>

                performFilter(filteredBeers)
            }
        }
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

    companion object {
        const val NEXT = 1
        const val UNCHANGED = 0
        const val PREVIOUS = -1
        const val MINIMUM_PAGE = 1
    }
}