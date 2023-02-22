package com.bofu.beerbox.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class RoutingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        // Keep SplashScreen visible until start the next activity
        installSplashScreen().apply {
            setKeepOnScreenCondition{ true }
        }
        super.onCreate(savedInstanceState)


        startBeerActivity()
    }


    private fun startBeerActivity(){
        val intent = Intent(this, BeerActivity::class.java)
        startActivity(intent)
        finish()
    }
}