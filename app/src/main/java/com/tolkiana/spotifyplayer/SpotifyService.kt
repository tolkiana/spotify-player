package com.tolkiana.spotifyplayer

import android.content.Context
import android.util.Log
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote


object SpotifyService {
    private const val CLIENT_ID = "YOUR_CLIENT_ID"
    private const val  REDIRECT_URI = "com.tolkiana.spotifyplayer://callback"

    private var mSpotifyAppRemote: SpotifyAppRemote? = null
    private var connectionParams: ConnectionParams = ConnectionParams.Builder(CLIENT_ID)
        .setRedirectUri(REDIRECT_URI)
        .showAuthView(true)
        .build()

    fun connect(context: Context, handler: () -> Unit) {

        if (mSpotifyAppRemote?.isConnected == true) {
            handler()
            return
        }

        SpotifyAppRemote.connect(context, connectionParams,
            object : Connector.ConnectionListener {

                override fun onConnected(spotifyAppRemote: SpotifyAppRemote) {
                    mSpotifyAppRemote = spotifyAppRemote
                    handler()
                }

                override fun onFailure(throwable: Throwable) {
                    Log.e("SpotifyService", throwable.message, throwable)
                }
            })
    }

    fun disconnect() {
        SpotifyAppRemote.disconnect(mSpotifyAppRemote)
    }

    fun play(uri: String) {
        mSpotifyAppRemote?.playerApi?.play(uri)
    }

    fun resume() {
        mSpotifyAppRemote?.playerApi?.resume()
    }

    fun pause() {
        mSpotifyAppRemote?.playerApi?.pause()
    }

    fun isPlaying(uri: String, handler: (Boolean) -> Unit) {
        mSpotifyAppRemote?.playerApi?.playerState?.setResultCallback { result ->
            handler(result.track.uri == uri)
        }
    }

    fun isPaused(uri: String, handler: (Boolean) -> Unit) {
        mSpotifyAppRemote?.playerApi?.playerState?.setResultCallback { result ->
            handler(result.track.uri == uri && result.isPaused)
        }
    }

}