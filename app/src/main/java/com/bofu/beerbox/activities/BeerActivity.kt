package com.bofu.beerbox.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bofu.beerbox.R
import com.bofu.beerbox.adapters.BeerAdapter
import com.bofu.beerbox.databinding.ActivityBeerBinding
import com.bofu.beerbox.models.Beer
import com.bofu.beerbox.models.NetworkResult
import com.bofu.beerbox.services.BeerService
import com.bofu.beerbox.viewModels.BeerViewModel
import com.bofu.beerbox.viewModels.ViewModelFactory
import kotlinx.coroutines.launch

class BeerActivity : AppCompatActivity() {

    private val TAG = javaClass.simpleName
    private val binding: ActivityBeerBinding by lazy { ActivityBeerBinding.inflate(layoutInflater) }
    private val beerAdapter by lazy { BeerAdapter(mutableListOf(), this::selectBeer) }
    // The beerViewModel is scoped to `this` Activity
    private val beerViewModel: BeerViewModel by viewModels {
        ViewModelFactory(application, BeerService())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        navigationBarSetup()
        beerViewModelSetup()
        beerRecyclerViewSetup()
    }


    private fun navigationBarSetup(){

        supportActionBar?.displayOptions = androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setCustomView(R.layout.navigation_bar)
    }

    private fun beerRecyclerViewSetup(){
        binding.beerRecyclerview.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = beerAdapter
        }
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
                    beerAdapter.update(it.data as List<Beer>)
                }
                is NetworkResult.Exception -> {
                    Toast.makeText(this, it.exception.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun selectBeer(beer: Beer, position:Int){
        Log.d(TAG, "beer: ${beer.name}, position: $position")

        // Test the code performance
        //beerViewModel.measureNanoTime()
    }
}