package com.example.dkalev.remember

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.dkalev.remember.deck.DecksActivity
import com.example.dkalev.remember.flashcard.CardFlipActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startActivity(Intent(this@MainActivity, DecksActivity::class.java))
        finish()
    }
}
