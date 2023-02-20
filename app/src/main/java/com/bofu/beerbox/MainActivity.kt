package com.bofu.beerbox

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bofu.beerbox.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {



    private val TAG = javaClass.simpleName
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        uiSetup()
    }


    private fun uiSetup(){

        supportActionBar?.displayOptions = androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setCustomView(R.layout.navigation_bar)
    }
}