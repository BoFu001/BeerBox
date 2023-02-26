package com.bofu.beerbox.utils

import android.content.res.Resources
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bofu.beerbox.R

abstract class RefreshScrollListener(

    private val resources: Resources,
    private val numberExtraTypes: Int

): RecyclerView.OnScrollListener()  {

    private val TAG = javaClass.simpleName

    // Stop scrolling when the first and last beer items are completely visible.
    // Further scrolling should be performed only by dragging
    var blockTop = false
    var blockBottom = false


    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)


        val linearLayoutManager =  recyclerView.layoutManager as LinearLayoutManager

        val total = linearLayoutManager.itemCount
        val firstVisible = linearLayoutManager.findFirstVisibleItemPosition()
        val lastVisible = linearLayoutManager.findLastVisibleItemPosition()


        // If the first visible item is the top blank item, scroll to first beer and set stop, without refresh
        if(firstVisible == 0){
            if(blockTop){
                //linearLayoutManager.scrollToPositionWithOffset(0, -valueInPixels)
                recyclerView.stopScroll()
            }
        }

        // If the last visible item is the bottom blank item, scroll to last beer and set stop, without refresh
        if(lastVisible == total - 1){

            if(blockBottom) {
                recyclerView.stopScroll()
            }
        }
    }


    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int){
        super.onScrollStateChanged(recyclerView, newState)

        val valueInPixels = resources.getDimensionPixelOffset(R.dimen.blank_item_height)
        val linearLayoutManager =  recyclerView.layoutManager as LinearLayoutManager

        val total = linearLayoutManager.itemCount
        val firstVisible = linearLayoutManager.findFirstVisibleItemPosition()
        val firstComplete = linearLayoutManager.findFirstCompletelyVisibleItemPosition()
        val lastVisible = linearLayoutManager.findLastVisibleItemPosition()
        val lastComplete = linearLayoutManager.findLastCompletelyVisibleItemPosition()


        // If the first completely visible item is top blank item, that means the top has been reached, fetch data
        if(firstComplete == 0){
            Log.d(TAG, "top reached")
            pullDownToRefresh()
        }


        // Touched the bottom without blank items
        //if(last == (total!! - 1)) {
        // Touched the bottom with blank items fetch data
        if(lastComplete == (total - 1) && total > numberExtraTypes) {
            Log.d(TAG, "bottom reached")
            pullUpToRefresh()
        }

        when(newState) {

            RecyclerView.SCROLL_STATE_SETTLING -> {
                Log.d(TAG, "onScrollStateChanged: SCROLLING")
            }

            RecyclerView.SCROLL_STATE_DRAGGING -> {
                Log.d(TAG, "onScrollStateChanged: DRAGGING")
                // Set a stop block if scrolling starts from the middle of list
                if(firstVisible > 1){
                    blockTop = true
                }
                if(lastVisible < total - 2){
                    blockBottom = true
                }
            }

            RecyclerView.SCROLL_STATE_IDLE -> {
                Log.d(TAG, "onScrollStateChanged: STOPPED")
                // If the first visible item is the first blank item, scroll to first beer
                if(firstVisible == 0){
                    linearLayoutManager.scrollToPositionWithOffset(0, -valueInPixels)
                }

                // Don't set a stop block if scrolling starts from the first beer item
                if(firstVisible <= 1){
                    blockTop = false
                }

                // If the last visible item is the last blank item, scroll to last beer
                if(lastVisible == total - 1){
                    val lastBeerItem = linearLayoutManager.findViewByPosition(lastComplete)
                    lastBeerItem?.measuredHeight?.let {

                        val offset = recyclerView.measuredHeight - it
                        linearLayoutManager.scrollToPositionWithOffset( lastComplete, offset)
                    }
                }

                // Don't set a stop block if scrolling starts from the last beer item
                if(lastVisible >= total - 2){
                    blockBottom = false
                }
            }
        }
    }

    abstract fun pullDownToRefresh()

    abstract fun pullUpToRefresh()
}