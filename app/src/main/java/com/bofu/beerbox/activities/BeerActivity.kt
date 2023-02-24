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
import androidx.recyclerview.widget.RecyclerView
import com.bofu.beerbox.R
import com.bofu.beerbox.adapters.BeerAdapter
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
    private val beerAdapter by lazy { BeerAdapter(mutableListOf(), this::selectBeer) }
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
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager =  recyclerView.layoutManager as LinearLayoutManager?

                val total = linearLayoutManager?.itemCount
                val first = linearLayoutManager?.findFirstCompletelyVisibleItemPosition()
                val last = linearLayoutManager?.findLastCompletelyVisibleItemPosition()

                if (listOf(total, first, last).none { it == null }) {

                    // Touched the top
                    if(first == 0){
                        println("arrive on top!")
                    }
                    // Touched the bottom
                    if(last == (total!! - 1)) {
                        beerViewModel.fetchData()
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
            // Scroll to top
            binding.beerRecyclerview.scrollToPosition(0)
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
        showPage(uiState.page)
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

    private fun showPage(page: Int){
        // TODO("Some mistake occurred")
        Toast.makeText(this, "Page $page", Toast.LENGTH_LONG).show()
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