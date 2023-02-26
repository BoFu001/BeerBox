package com.bofu.beerbox.extensions

import android.view.View
import com.bofu.beerbox.utils.SafeClickListener

fun View.setOnSafeClickListener(
    onSafeClick: (View) -> Unit
) {
    setOnClickListener(SafeClickListener { v ->
        onSafeClick(v)
    })
}