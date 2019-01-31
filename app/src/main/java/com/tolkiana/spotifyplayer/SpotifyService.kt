package com.tolkiana.spotifyplayer

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.ImageUri
import com.spotify.protocol.types.Track


enum class PlayingState {
    PAUSED, PLAYING, STOPPED
}

object SpotifyService {
    private const val CLIENT_ID = "YOUR_CLIENT_ID"
    private const val  REDIRECT_URI = "com.tolkiana.spotifyplayer://callback"

    private var mSpotifyAppRemote: SpotifyAppRemote? = null
    private var connectionParams: ConnectionParams = ConnectionParams.Builder(CLIENT_ID)
        .setRedirectUri(REDIRECT_URI)
        .showAuthView(true)
        .build()

    fun connect(context: Context, handler: (connected: Boolean) -> Unit) {

        if (mSpotifyAppRemote?.isConnected == true) {
            handler(true)
            return
        }

        val connectionListener = object : Connector.ConnectionListener {
            override fun onConnected(spotifyAppRemote: SpotifyAppRemote) {
                mSpotifyAppRemote = spotifyAppRemote
                handler(true)
            }

            override fun onFailure(throwable: Throwable) {
                Log.e("SpotifyService", throwable.message, throwable)
                handler(false)
            }
        }
        SpotifyAppRemote.connect(context, connectionParams, connectionListener)
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

    fun getCurrentTrack(handler: (track: Track) -> Unit) {
        mSpotifyAppRemote?.playerApi?.playerState?.setResultCallback { result ->
            handler(result.track)
        }
    }

    fun getImage(imageUri: ImageUri, handler: (Bitmap) -> Unit)  {
        mSpotifyAppRemote?.imagesApi?.getImage(imageUri)?.setResultCallback {
            handler(it)
        }
    }

    fun suscribeToChanges(handler: (Track) -> Unit) {
        mSpotifyAppRemote?.playerApi?.subscribeToPlayerState()?.setEventCallback {
            handler(it.track)
        }
    }

    fun getCurrentTrackImage(handler: (Bitmap) -> Unit)  {
        getCurrentTrack {
            getImage(it.imageUri) {
                handler(it)
            }
        }
    }

    fun playingState(handler: (PlayingState) -> Unit) {
        mSpotifyAppRemote?.playerApi?.playerState?.setResultCallback { result ->
            if (result.track.uri == null) {
                handler(PlayingState.STOPPED)
            } else if (result.isPaused) {
                handler(PlayingState.PAUSED)
            } else {
                handler(PlayingState.PLAYING)
            }
        }
    }

}