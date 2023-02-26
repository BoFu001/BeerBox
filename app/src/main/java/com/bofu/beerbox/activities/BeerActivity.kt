package com.bofu.beerbox.activities

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
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
import com.bofu.beerbox.R
import com.bofu.beerbox.adapters.MultipleTypeAdapter
import com.bofu.beerbox.databinding.ActivityBeerBinding
import com.bofu.beerbox.databinding.DialogBottomBinding
import com.bofu.beerbox.extensions.loadImage
import com.bofu.beerbox.models.Beer
import com.bofu.beerbox.models.UiState
import com.bofu.beerbox.services.BeerService
import com.bofu.beerbox.utils.RefreshScrollListener
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

        binding.beerRecyclerview.addOnScrollListener(object : RefreshScrollListener(resources, number_extra_types) {
            override fun pullDownToRefresh(){
                beerViewModel.fetchData(BeerViewModel.PREVIOUS)
            }
            override fun pullUpToRefresh(){
                beerViewModel.fetchData(BeerViewModel.NEXT)
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