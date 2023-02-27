package com.bofu.beerbox.utils

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager

class CustomLayoutManager(context: Context) : LinearLayoutManager(context) {

    private var topOffset = 0
    fun setTopOffset(topOffset: Int){
        this.topOffset = topOffset
    }

    fun goToTop(){
        this.scrollToPositionWithOffset(0, topOffset)
    }
}