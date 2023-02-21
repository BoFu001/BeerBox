package com.bofu.beerbox.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bofu.beerbox.R
import com.bofu.beerbox.databinding.ActivityMainBinding
import com.bofu.beerbox.models.NetworkResult
import com.bofu.beerbox.services.BeerService
import com.bofu.beerbox.viewModels.BeerViewModel
import com.bofu.beerbox.viewModels.ViewModelFactory
import kotlinx.coroutines.launch

class BeerActivity : AppCompatActivity() {



    private val TAG = javaClass.simpleName
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val beerViewModel: BeerViewModel by viewModels {
        ViewModelFactory(application, BeerService())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        uiSetup()
        beerViewModelSetup()
    }


    private fun uiSetup(){

        supportActionBar?.displayOptions = androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setCustomView(R.layout.navigation_bar)
    }

    private fun beerViewModelSetup() {

        // LifecycleScope is defined for each Lifecycle object.
        // Any coroutine launched in this scope is canceled when the Lifecycle is destroyed.
        lifecycleScope.launch{

            // This approach processes the flow emissions only when the UI is visible on the screen,
            // saving resources and potentially avoiding app crashes.
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                beerViewModel.uiStateFlow.collect {
                    println("aaaaa uiStateFlow " + it.isLoading)
                    //render(it)
                }
            }
        }


        beerViewModel.beerLiveData.observe(this) {
            when(it){
                is NetworkResult.ResponseSuccess<*> -> {
                    println("aaaaa beerLiveData" + it.data)
                }
                is NetworkResult.Exception -> {
                    println("aaaaa beerLiveData" + it.exception.message)
                }
            }
        }
    }


}