package com.tolkiana.spotifyplayer

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        connectButton.setOnClickListener {
            SpotifyService.connect(this) {
                if(it) {
                    showPlayer()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SpotifyService.disconnect()
    }

    private fun showPlayer() {
        val intent = Intent(this, PlayerActivity::class.java)
        startActivity(intent)
    }
}
