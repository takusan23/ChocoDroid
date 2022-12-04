package io.github.takusan23.chocodroid.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import androidx.media.session.MediaButtonReceiver
import coil.imageLoader
import coil.request.ImageRequest
import io.github.takusan23.chocodroid.ChocoDroidApplication
import io.github.takusan23.chocodroid.MainActivity
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.setting.SettingKeyObject
import io.github.takusan23.chocodroid.setting.dataStore
import io.github.takusan23.internet.data.CommonVideoData
import io.github.takusan23.internet.data.watchpage.WatchPageResponseJSONData
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/** スムーズに バックグラウンド / フォアグラウンド 再生を切り替えるためのサービス */
class SmoothBackgroundPlayService : LifecycleService() {

    /** 通知出すやつ */
    private val notificationManager by lazy { NotificationManagerCompat.from(this) }

    /** MediaSession。外部に音楽情報を提供するやつ */
    private val mediaSession by lazy {
        MediaSessionCompat(this, MEDIA_SESSION_TAG).apply {
            setCallback(mediaSessionCallback)
        }
    }

    /** [MediaButtonReceiver]がリピートモードに対応してないので自前で書いた */
    private val notificationButtonReceiver by lazy {
        NotificationMediaButtonReceiver(this, mediaSession.controller.transportControls)
    }

    /** MediaSessionで受け付けている操作のコールバック */
    private val mediaSessionCallback: MediaSessionCompat.Callback = object : MediaSessionCompat.Callback() {
        /** 一時停止 */
        override fun onPause() {
            super.onPause()
            chocoDroidPlayer.playWhenReady = false
        }

        /** 再生 */
        override fun onPlay() {
            super.onPlay()
            chocoDroidPlayer.playWhenReady = true
            mediaSession.isActive = true
        }

        /** シーク */
        override fun onSeekTo(pos: Long) {
            super.onSeekTo(pos)
            chocoDroidPlayer.currentPositionMs = pos
        }

        /** 終了時 */
        override fun onStop() {
            super.onStop()
            mediaSession.isActive = false
            // もう使わないのでプレイヤーも終了する
            ChocoDroidApplication.instance.playerDestroy()
            stopSelf()
        }

        /** Android 13 以降は CustomAction を利用して通知領域にアイコンを追加する（リピートなど） */
        override fun onCustomAction(action: String?, extras: Bundle?) {
            super.onCustomAction(action, extras)
            when (action) {
                MEDIA_SESSION_CUSTOM_REPEAT -> {
                    lifecycleScope.launch {
                        dataStore.edit { it[SettingKeyObject.PLAYER_REPEAT_MODE] = it[SettingKeyObject.PLAYER_REPEAT_MODE] == false }
                        updateState(contentLoader.watchPageResponseDataFlow.first()!!.watchPageResponseJSONData)
                    }
                }
                MEDIA_SESSION_CUSTOM_CLOSE -> {
                    ChocoDroidApplication.instance.playerDestroy()
                    stopSelf()
                }
            }
        }

        /** リピートモード変更時 */
        override fun onSetRepeatMode(repeatMode: Int) {
            super.onSetRepeatMode(repeatMode)
            // ExoPlayerに反映
            lifecycleScope.launch {
                dataStore.edit { it[SettingKeyObject.PLAYER_REPEAT_MODE] = repeatMode == PlaybackStateCompat.REPEAT_MODE_ONE }
                updateState(contentLoader.watchPageResponseDataFlow.first()!!.watchPageResponseJSONData)
                updateNotification()
            }
        }
    }

    /** コンテンツ読み込み */
    private val contentLoader by lazy { ChocoDroidApplication.instance.chocoDroidContentLoader }

    /** プレイヤー */
    private val chocoDroidPlayer by lazy { ChocoDroidApplication.instance.chocoDroidPlayer }

    override fun onCreate() {
        super.onCreate()
        // とりあえず通知を出す
        updateNotification()

        // 再生中の動画情報
        contentLoader.watchPageResponseDataFlow.filterNotNull().onEach { watchPageData ->
            updateState(watchPageData.watchPageResponseJSONData)
            updateNotification()
        }.launchIn(lifecycleScope)

        // プレイヤー状態変化に対応
        chocoDroidPlayer.playbackStateFlow.onEach {
            updateState(contentLoader.watchPageResponseDataFlow.first()!!.watchPageResponseJSONData)
            updateNotification()
        }.launchIn(lifecycleScope)

        // 再生位置を関し
        chocoDroidPlayer.currentPositionDataFlow.onEach {
            updateState(contentLoader.watchPageResponseDataFlow.first()!!.watchPageResponseJSONData)
        }.launchIn(lifecycleScope)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_NOT_STICKY
    }

    /** 最近のアプリ画面から消したときに呼ばれる */
    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        // プレイヤー一式終了させる
        ChocoDroidApplication.instance.playerDestroy()
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        notificationButtonReceiver.release()
    }

    /** ExoPlayerの再生状態をMediaSessionへ渡す */
    private suspend fun updateState(watchPageResponseJSONData: WatchPageResponseJSONData) {
        // リピートモードの設定
        val mediaSessionRepeatMode = if (chocoDroidPlayer.repeatMode) PlaybackStateCompat.REPEAT_MODE_ONE else PlaybackStateCompat.REPEAT_MODE_ALL
        mediaSession.setRepeatMode(mediaSessionRepeatMode)
        // 再生状態の提供
        val playbackStateCompat = PlaybackStateCompat.Builder().apply {
            // 受け付ける操作
            setActions(
                PlaybackStateCompat.ACTION_PLAY
                        or PlaybackStateCompat.ACTION_PAUSE
                        or PlaybackStateCompat.ACTION_SEEK_TO
                        or PlaybackStateCompat.ACTION_STOP
                        or PlaybackStateCompat.ACTION_SET_REPEAT_MODE
            )
            val currentState = if (chocoDroidPlayer.playWhenReady) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED
            setState(currentState, chocoDroidPlayer.currentPositionMs, 1f)
            // Android 13 以降 CustomAction に定義しないと通知領域に表示されるコントローラーには出ない
            // リピートモード切り替えをカスタムアクションとして定義する
            if (chocoDroidPlayer.repeatMode) {
                addCustomAction(MEDIA_SESSION_CUSTOM_REPEAT, "Repeat", R.drawable.ic_outline_repeat_one_24)
            } else {
                addCustomAction(MEDIA_SESSION_CUSTOM_REPEAT, "Repeat", R.drawable.ic_outline_repeat_24)
            }
            // プレイヤー終了ボタン
            addCustomAction(MEDIA_SESSION_CUSTOM_CLOSE, "Close", R.drawable.ic_outline_close_24)
        }.build()
        // 再生中の音楽情報
        val videoData = CommonVideoData(watchPageResponseJSONData)
        // これあるとAndroid 10でシーク使えます
        val duration = chocoDroidPlayer.videoDataFlow.first().durationMs
        // CoilでBitmap読み込み
        val bitmap = loadBitmap(videoData.thumbnailUrl)
        val mediaMetadataCompat = MediaMetadataCompat.Builder().apply {
            putString(MediaMetadataCompat.METADATA_KEY_TITLE, videoData.videoTitle)
            putString(MediaMetadataCompat.METADATA_KEY_ARTIST, videoData.ownerName)
            putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration)
            putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap)
        }.build()
        mediaSession.setMetadata(mediaMetadataCompat)
        mediaSession.setPlaybackState(playbackStateCompat)
    }

    /** MediaSessionスタイルな通知を出す */
    private fun updateNotification() {
        // TODO ダウンロード連続再生と同じ通知チャンネル
        val channelId = "download_content_background_player_service"
        val channel = NotificationChannelCompat.Builder(channelId, NotificationManagerCompat.IMPORTANCE_LOW).apply {
            setName(getString(R.string.download_content_backrgound_player))
        }.build()
        // チャンネルID登録
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager.getNotificationChannel(channelId) == null) {
            notificationManager.createNotificationChannel(channel)
        }
        val notificationManagerCompat = NotificationCompat.Builder(this@SmoothBackgroundPlayService, channelId).apply {
            setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken).setShowActionsInCompactView(0, 1, 2))
            setSmallIcon(R.drawable.chocodroid_background_player)
            setContentIntent(PendingIntent.getActivity(
                this@SmoothBackgroundPlayService,
                0,
                Intent(this@SmoothBackgroundPlayService, MainActivity::class.java),
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
            ))

            /**
             * addActionしやすいように関数に分ける。
             * Android 13 以降、ここの AddAction は無視される。（MediaSessionのPlaybackStateで書いた操作が優先される）
             */
            fun simpleAddAction(icon: Int, title: String, action: String) {
                addAction(icon, title, notificationButtonReceiver.buildPendingIntent(action))
            }

            // リピートボタン
            val isRepeatMode = chocoDroidPlayer.repeatMode
            simpleAddAction(
                if (!isRepeatMode) R.drawable.ic_outline_repeat_24 else R.drawable.ic_outline_repeat_one_24,
                "repeat",
                if (!isRepeatMode) NotificationMediaButtonReceiver.PlayerControlType.ACTION_REPEAT_ONE.action else NotificationMediaButtonReceiver.PlayerControlType.ACTION_REPEAT_ALL.action
            )
            // 一時停止ボタン
            val isPlaying = chocoDroidPlayer.playWhenReady
            simpleAddAction(
                if (isPlaying) R.drawable.ic_outline_pause_24 else R.drawable.ic_outline_play_arrow_24,
                if (isPlaying) "pause" else "play",
                if (isPlaying) NotificationMediaButtonReceiver.PlayerControlType.ACTION_PAUSE.action else NotificationMediaButtonReceiver.PlayerControlType.ACTION_PLAY.action
            )
            // 終了
            simpleAddAction(
                R.drawable.ic_outline_close_24,
                "stop",
                NotificationMediaButtonReceiver.PlayerControlType.ACTION_STOP.action
            )
        }.build()
        startForeground(NOTIFICATION_ID, notificationManagerCompat)
    }

    /**
     * CoilでBitmap読み込みを行う。コルーチン内で使って下さい
     *
     * @param path 画像パス
     * @return Bitmap
     */
    private suspend fun loadBitmap(path: String): Bitmap? {
        // CoilでBitmap読み込み
        return imageLoader.execute(ImageRequest.Builder(this).data(path).build()).drawable?.toBitmap()
    }

    companion object {

        /** 通知ID */
        private const val NOTIFICATION_ID = 20221204

        /** MediaSessionのやつ */
        private const val MEDIA_SESSION_TAG = "io.github.takusan23.chocodroid.service.SMOOTH_BACKGROUND_PLAY_SERVICE"

        /** MediaSessionリピート切り替えカスタムアクション */
        private const val MEDIA_SESSION_CUSTOM_REPEAT = "io.github.takusan23.chocodroid.service.MEDIA_SESSION_CUSTOM_REPEAT"

        /** MediaSessionプレイヤー終了カスタムアクション */
        private const val MEDIA_SESSION_CUSTOM_CLOSE = "io.github.takusan23.chocodroid.service.MEDIA_SESSION_CUSTOM_CLOSE"

        /**
         * サービスを起動する
         *
         * @param context [Context]
         */
        fun startService(context: Context) {
            ContextCompat.startForegroundService(context, Intent(context, SmoothBackgroundPlayService::class.java))
        }

        /**
         * サービスを終了させる
         *
         * @param context [Context]
         */
        fun stopService(context: Context) {
            context.stopService(Intent(context, SmoothBackgroundPlayService::class.java))
        }
    }
}