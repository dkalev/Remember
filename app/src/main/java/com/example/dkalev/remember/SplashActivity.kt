package com.example.dkalev.remember

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent



class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Start home activity
        startActivity(Intent(this@SplashActivity, MainActivity::class.java))

        // close splash activity
        finish()
    }
}
