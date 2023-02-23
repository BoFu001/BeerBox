package com.bofu.beerbox.activities

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bofu.beerbox.R
import com.bofu.beerbox.adapters.BeerAdapter
import com.bofu.beerbox.databinding.ActivityBeerBinding
import com.bofu.beerbox.databinding.DialogBottomBinding
import com.bofu.beerbox.extensions.loadImage
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
        supportActionBar?.apply {
            displayOptions = androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM
            setDisplayShowCustomEnabled(true)
            setCustomView(R.layout.navigation_bar)
        }
    }

    private fun beerRecyclerViewSetup(){
        binding.beerRecyclerview.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = beerAdapter
        }

        binding.beerRecyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager =  recyclerView.layoutManager as LinearLayoutManager?

                val total = linearLayoutManager?.itemCount
                val first = linearLayoutManager?.findFirstCompletelyVisibleItemPosition()
                val last = linearLayoutManager?.findLastCompletelyVisibleItemPosition()

                if (listOf(total, first, last).none { it == null }) {

                    // Scroll to top
                    if(first == 0){
                        println("arrive on top!")
                    }
                    // Scroll to bottom
                    if(last == (total!! - 1)) {
                        println("arrive on bottom!")
                    }
                }
            }
        })
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
        showDialog(beer.name, beer.tagline, beer.description, beer.image_url)
        // Test the code performance
        //beerViewModel.measureNanoTime()
    }

    private fun showDialog(name: String, tag: String, description: String, url: String){


        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        val dialogBottomBinding = DialogBottomBinding.inflate(layoutInflater)
        builder.setView(dialogBottomBinding.root)

        dialogBottomBinding.dialogBottomName.text = name
        dialogBottomBinding.dialogBottomTagline.text = tag
        dialogBottomBinding.dialogBottomDescription.text = description
        dialogBottomBinding.dialogBottomImg.loadImage(url)

        val dialog = builder.create()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_bottom)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)

        dialog.show()
    }
}