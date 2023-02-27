package com.bofu.beerbox.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bofu.beerbox.R
import com.bofu.beerbox.adapters.FilterAdapter
import com.bofu.beerbox.adapters.MultipleTypeAdapter
import com.bofu.beerbox.databinding.ActivityBeerBinding
import com.bofu.beerbox.models.Beer
import com.bofu.beerbox.models.Filter
import com.bofu.beerbox.models.UiState
import com.bofu.beerbox.services.BeerService
import com.bofu.beerbox.utils.CustomLayoutManager
import com.bofu.beerbox.utils.RefreshScrollListener
import com.bofu.beerbox.viewModels.BeerViewModel
import com.bofu.beerbox.viewModels.ViewModelFactory
import kotlinx.coroutines.launch


class BeerActivity : BaseActivity() {

    private val TAG = javaClass.simpleName
    private val binding: ActivityBeerBinding by lazy { ActivityBeerBinding.inflate(layoutInflater) }
    private val beerAdapter by lazy { MultipleTypeAdapter(mutableListOf(), this::selectBeer) }
    private val filterAdapter by lazy { FilterAdapter(mutableListOf(), this::selectFilter) }
    private val beerViewModel: BeerViewModel by viewModels {
        ViewModelFactory(application, BeerService())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        navigationBarSetup()
        beerRecyclerViewSetup()
        filterRecyclerViewSetup()
        searchViewSetup()
        retryBtnSetup()
        beerViewModelSetup()
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
            layoutManager = CustomLayoutManager(context)
            setHasFixedSize(true)
            adapter = beerAdapter
        }
    }

    private fun selectBeer(beer: Beer){
        showDialog(beer.id, beer.name, beer.tagline, beer.description, beer.image_url)
    }

    private fun filterRecyclerViewSetup(){
        binding.beerFilterRecyclerview.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
            adapter = filterAdapter
        }
    }

    private fun selectFilter(filter: Filter){
        clearSearchView()
        beerViewModel.setFilter(filter)
    }

    private fun searchViewSetup() {
        binding.beerSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                beerViewModel.search(p0)
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                beerViewModel.search(p0)
                return false
            }
        })
    }

    private fun clearSearchView(){
        binding.beerSearchView.setQuery("", false)
        binding.beerSearchView.clearFocus()
        binding.beerSearchView.isIconified = true
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

            // Refresh beer list only if there is neither filter nor search keyword
            val bool = beerViewModel.hasNoFilter() && binding.beerSearchView.query.toString() == ""
            enableRefresh(bool)

            // Update new list of beer
            beerAdapter.update(it)

            // go to top of the list
            goToTop()
        }

        beerViewModel.filterLiveData.observe(this) {
            filterAdapter.update(it)
        }
    }

    private fun enableRefresh(bool: Boolean){
        //set adapter
        beerAdapter.enableRefresh(bool)
        //set listener
        beerRecyclerViewListenerUpdate(bool)
        //set top
        setTop(bool)
    }

    private fun beerRecyclerViewListenerUpdate(bool: Boolean){

        val scrollListener = when(bool) {
            true -> {
                object : RefreshScrollListener(resources) {
                    override fun pullDownToRefresh() {
                        beerViewModel.fetchData(BeerViewModel.PREVIOUS)
                    }

                    override fun pullUpToRefresh() {
                        beerViewModel.fetchData(BeerViewModel.NEXT)
                    }
                }
            }
            false -> {
                object : RecyclerView.OnScrollListener() {}
            }
        }

        binding.beerRecyclerview.clearOnScrollListeners()
        binding.beerRecyclerview.addOnScrollListener(scrollListener)
    }

    private fun setTop(bool: Boolean) {

        val topOffset = when(bool){
            true -> {
                resources.getDimensionPixelOffset(R.dimen.blank_item_height) * -1
            }
            false -> {
                0
            }
        }

        val customLayoutManager = binding.beerRecyclerview.layoutManager as CustomLayoutManager
        customLayoutManager.setTopOffset(topOffset)
    }

    private fun goToTop() {
        val customLayoutManager = binding.beerRecyclerview.layoutManager as CustomLayoutManager
        customLayoutManager.goToTop()
    }

    private fun render(uiState: UiState){
        showNoConnectionView(uiState.hasConnection)
        showProgressBar(uiState.isLoading)
        showNoResultView(uiState.emptyResult)
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

    private fun showNoResultView(bool: Boolean){
        when(bool){
            true -> {
                binding.beerNoResultView.noResultViewLayout.visibility = View.VISIBLE
                binding.beerRecyclerview.visibility = View.GONE
            }
            false -> {
                binding.beerNoResultView.noResultViewLayout.visibility = View.GONE
                binding.beerRecyclerview.visibility = View.VISIBLE
            }
        }
    }

    private fun showError(message: String?){
        message?.let {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }
}