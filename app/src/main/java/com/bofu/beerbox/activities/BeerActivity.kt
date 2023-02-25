package com.bofu.beerbox.activities

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
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
import com.bofu.beerbox.adapters.MultipleTypeAdapter
import com.bofu.beerbox.databinding.ActivityBeerBinding
import com.bofu.beerbox.databinding.DialogBottomBinding
import com.bofu.beerbox.extensions.loadImage
import com.bofu.beerbox.models.Beer
import com.bofu.beerbox.models.UiState
import com.bofu.beerbox.services.BeerService
import com.bofu.beerbox.viewModels.BeerViewModel
import com.bofu.beerbox.viewModels.ViewModelFactory
import kotlinx.coroutines.launch


class BeerActivity : AppCompatActivity() {

    private val TAG = javaClass.simpleName
    private val binding: ActivityBeerBinding by lazy { ActivityBeerBinding.inflate(layoutInflater) }
    //private val beerAdapter by lazy { BeerAdapter(mutableListOf(), this::selectBeer) }
    private val beerAdapter by lazy { MultipleTypeAdapter(mutableListOf(), number_extra_types, this::selectBeer) }
    private val beerViewModel: BeerViewModel by viewModels {
        ViewModelFactory(application, BeerService())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        navigationBarSetup()
        beerViewModelSetup()
        beerRecyclerViewSetup()
        retryBtnSetup()
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

            // Stop scrolling when the first and last beer items are completely visible.
            // Further scrolling should be performed only by dragging
            var block = false

            // Height of blank item
            val valueInPixels = resources.getDimensionPixelOffset(R.dimen.blank_item_height)


            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)


                val linearLayoutManager =  recyclerView.layoutManager as LinearLayoutManager

                val total = linearLayoutManager.itemCount
                val firstVisible = linearLayoutManager.findFirstVisibleItemPosition()
                val lastComplete = linearLayoutManager.findLastCompletelyVisibleItemPosition()





                // If the first visible item is the top blank item, scroll to first beer and set stop, without refresh
                if(firstVisible == 0){
                    if(block){
                        linearLayoutManager.scrollToPositionWithOffset(0, -valueInPixels)
                        recyclerView.stopScroll()
                    }
                }





                // If the last completely visible item is the last beer item, scroll to last beer and set stop, without refresh
                if(lastComplete == total - number_extra_types){

                    if(block) {

                        val lastBeerItem = linearLayoutManager.findViewByPosition(lastComplete)
                        lastBeerItem?.measuredHeight?.let {

                            val offset = recyclerView.measuredHeight - it
                            linearLayoutManager.scrollToPositionWithOffset( lastComplete, offset)
                            recyclerView.stopScroll()

                        }
                    }
                }
            }


            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int){
                super.onScrollStateChanged(recyclerView, newState)



                val valueInPixels = resources.getDimensionPixelOffset(R.dimen.blank_item_height)
                val linearLayoutManager =  recyclerView.layoutManager as LinearLayoutManager?

                val total = linearLayoutManager?.itemCount
                val firstVisible = linearLayoutManager?.findFirstVisibleItemPosition()
                val firstComplete = linearLayoutManager?.findFirstCompletelyVisibleItemPosition()
                val last = linearLayoutManager?.findLastCompletelyVisibleItemPosition()

                if (listOf(total, firstComplete, last).none { it == null }) {


                    // If the first completely visible item is top blank item, that means the top has been reached, fetch data
                    if(firstComplete == 0){
                        Log.d(TAG, "top reached")
                        beerViewModel.fetchData(BeerViewModel.PREVIOUS)
                    }


                    // Touched the bottom without blank items
                    //if(last == (total!! - 1)) {
                    // Touched the bottom with blank items fetch data
                    if(last == (total!! - 1) && total > number_extra_types) {
                        Log.d(TAG, "bottom reached")
                        beerViewModel.fetchData(BeerViewModel.NEXT)
                    }
                }



                when(newState) {

                    RecyclerView.SCROLL_STATE_SETTLING -> {
                        Log.d(TAG, "onScrollStateChanged: SCROLLING")
                    }

                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        // Set a stop block if scrolling starts from the middle of list
                        Log.d(TAG, "onScrollStateChanged: DRAGGING")
                        firstVisible?.let { _firstVisible ->
                            if(_firstVisible in 2..24){
                                block = true
                            }
                        }
                    }

                    RecyclerView.SCROLL_STATE_IDLE -> {
                        // If the first visible item is the first blank item, scroll to first beer
                        Log.d(TAG, "onScrollStateChanged: STOPPED")
                        if(firstVisible == 0){
                            block = false
                            linearLayoutManager.scrollToPositionWithOffset(0, -valueInPixels)
                        }

                        // Don't set a stop block if scrolling starts from the first beer item
                        if(firstVisible == 1){
                            block = false
                        }
                    }
                }
            }


        })
    }

    private fun retryBtnSetup(){
        binding.beerNoConnectionView.noConnectionBtn.setOnClickListener {
            beerViewModel.fetchData()
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
                    render(it)
                }
            }
        }

        beerViewModel.beerLiveData.observe(this) {
            // Scroll to top (after the first black item which has index 0)
            //binding.beerRecyclerview.scrollToPosition(1)


            val linearLayoutManager = binding.beerRecyclerview.layoutManager as LinearLayoutManager?
            val valueInPixels = resources.getDimensionPixelOffset(R.dimen.blank_item_height)
            linearLayoutManager?.scrollToPositionWithOffset(0, -valueInPixels)



            // Update new list of beer
            beerAdapter.update(it)
        }
    }

    private fun selectBeer(beer: Beer){
        showDialog(beer.name, beer.tagline, beer.description, beer.image_url)
    }

    private fun render(uiState: UiState){
        showNoConnectionView(uiState.hasConnection)
        showProgressBar(uiState.isLoading)
        //showNoResultView(uiState.errorMessage)
        showError(uiState.errorMessage)
    }

    private fun showNoConnectionView(bool: Boolean){
        when(bool){
            true -> binding.beerNoConnectionView.noConnectionViewLayout.visibility = View.GONE
            false -> binding.beerNoConnectionView.noConnectionViewLayout.visibility = View.VISIBLE
        }
    }

    private fun showProgressBar(bool: Boolean){
        when(bool){
            true -> binding.beerProgressbar.visibility = View.VISIBLE
            false -> binding.beerProgressbar.visibility = View.GONE
        }
    }

    /*
    private fun showNoResultView(bool: Boolean){
        when(bool){
            true -> if (mainViewModel.viewState.value!!.hasConnection) binding.mainNoResultView.noResultViewLayout.visibility = View.VISIBLE
            false -> binding.mainNoResultView.noResultViewLayout.visibility = View.GONE
        }
    }*/

    private fun showError(message: String?){
        message?.let {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
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

    companion object{
        private const val number_extra_types = 2
    }
}