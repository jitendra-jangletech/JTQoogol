package com.jangletech.qoogol

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util
import com.jangletech.qoogol.databinding.ActivityVideoBinding
import com.jangletech.qoogol.ui.GeneralViewModel
import com.jangletech.qoogol.util.QoogolApp

class VideoActivity : AppCompatActivity(), View.OnClickListener, Player.EventListener {

    private var activityVideoBinding: ActivityVideoBinding? = null
    private var cacheDataSourceFactory: CacheDataSourceFactory? = null
    private var simpleCache: SimpleCache? = null
    private var player: SimpleExoPlayer? = null
    private var path: String? = null
    private var isFromUrl: Boolean = false
    private var mGeneralViewModel: GeneralViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mGeneralViewModel = ViewModelProvider(this@VideoActivity)[GeneralViewModel::class.java]
        activityVideoBinding = DataBindingUtil.setContentView(this, R.layout.activity_video)
        initView()
        setListeners()
    }

    private fun setListeners() {
        activityVideoBinding!!.btnIvClose.setOnClickListener(this)
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        val intent = intent
        if (intent != null) {
            path = intent.getStringExtra("uri")
            isFromUrl = intent.getBooleanExtra("fromUrl", true)
            // initializePlayer();
        }
        activityVideoBinding!!.root.findViewById<View>(R.id.exo_spd).setOnClickListener { v: View? ->
            if (mGeneralViewModel!!.lastPlaySpeed < 3.0f) {
                mGeneralViewModel!!.lastPlaySpeed += 0.5f
                (activityVideoBinding!!.root.findViewById<View>(R.id.exo_spd) as TextView).text = "x " + mGeneralViewModel!!.lastPlaySpeed
            } else {
                mGeneralViewModel!!.lastPlaySpeed = 0.5f
                (activityVideoBinding!!.root.findViewById<View>(R.id.exo_spd) as TextView).text = "x 0.5"
            }
            player!!.setPlaybackParameters(PlaybackParameters(mGeneralViewModel!!.lastPlaySpeed))
        }
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            initializePlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT < 24 || player == null) {
            initializePlayer()
        }
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        player!!.playWhenReady = playWhenReady
    }

    private fun initializePlayer() {
        player = SimpleExoPlayer.Builder(this).build()
        simpleCache = QoogolApp.simpleCache

        if (isFromUrl) {
            cacheDataSourceFactory = CacheDataSourceFactory(
                    simpleCache,
                    DefaultHttpDataSourceFactory(this.let {
                        Util.getUserAgent(
                                it, getString(
                                R.string.app_name
                        )
                        )
                    }),
                    CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR
            )
        }

        activityVideoBinding!!.ivPreviewVideo.player = player
        val mediaSource = buildMediaSource(Uri.parse(path))
        player!!.playWhenReady = mGeneralViewModel!!.playWhenReady
        player!!.addListener(this)
        activityVideoBinding!!.ivPreviewVideo.useController = true
        player!!.seekTo(mGeneralViewModel!!.currentWindow, mGeneralViewModel!!.playbackPosition)
        player!!.prepare(mediaSource, false, false)
    }

    private fun buildMediaSource(uri: Uri): MediaSource {
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(this, "exoplayer-chat")
        return if (!isFromUrl) {
            ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
        } else {
            ProgressiveMediaSource.Factory(cacheDataSourceFactory).createMediaSource(uri)
        }
    }

    override fun onPause() {
        super.onPause()
        if (player != null) {
            releasePlayer()
        }
    }

    override fun onClick(v: View) {
        if (v.id == R.id.btnIvClose) {
            finish()
        }
    }

    private fun releasePlayer() {
        try {
            if (player != null) {
                mGeneralViewModel!!.playWhenReady = player!!.playWhenReady
                mGeneralViewModel!!.playbackPosition = player!!.currentPosition
                mGeneralViewModel!!.currentWindow = player!!.currentWindowIndex
                player!!.release()
                player = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
