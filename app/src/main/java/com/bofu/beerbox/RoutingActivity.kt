package com.bofu.beerbox

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class RoutingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        start()
    }


    private fun start() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}