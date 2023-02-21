package com.bofu.beerbox.viewModels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bofu.beerbox.services.BeerService
import java.lang.IllegalArgumentException

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val application: Application,
    private val beerService: BeerService
) : ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when(modelClass){
        BeerViewModel::class.java -> BeerViewModel(application, beerService)
        //OtherViewModel::class.java -> OtherViewModel(OtherService())
        else -> throw IllegalArgumentException("Unknown ViewModel class ${modelClass.name}")
    } as T
}