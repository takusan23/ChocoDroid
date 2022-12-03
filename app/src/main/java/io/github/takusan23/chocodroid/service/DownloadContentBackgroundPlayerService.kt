package io.github.takusan23.chocodroid.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.datastore.preferences.core.edit
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver
import coil.imageLoader
import coil.request.ImageRequest
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import io.github.takusan23.chocodroid.MainActivity
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.service.DownloadContentBackgroundPlayerService.Companion.REQUEST_START_VIDEO_ID
import io.github.takusan23.chocodroid.setting.SettingKeyObject
import io.github.takusan23.chocodroid.setting.dataStore
import io.github.takusan23.chocodroid.tool.DownloadContentManager
import io.github.takusan23.internet.data.CommonVideoData
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlin.math.max

/**
 * ダウンロード済みの動画はバックグラウンドで連続再生できるようにする
 *
 * 通知のボタンを押す
 * ↓
 * [NotificationMediaButtonReceiver]が受け取る
 * ↓
 * [MediaSessionCompat.Callback]が受け取る
 * ↓
 * [ExoPlayer]に来る
 *
 * putExtra いれられるもの
 *
 * [REQUEST_START_VIDEO_ID] | String | 指定した動画から再生してほしい場合は動画IDを入れて下さい
 *
 * */
class DownloadContentBackgroundPlayerService : MediaBrowserServiceCompat() {

    /** システムが最後の曲を要求している場合はこっち */
    private val ROOT_REQUIRE_RECENT = ""

    /** それ以外 */
    private val ROOT = ""

    /** データ読み込み等で使うコルーチンスコープ */
    private val scope = CoroutineScope(Dispatchers.Main)

    /** ダウンロードコンテンツマネージャー */
    private val downloadContentManager by lazy { DownloadContentManager(this) }

    /** 通知出すやつ */
    private val notificationManager by lazy { NotificationManagerCompat.from(this) }

    /** ExoPlayer */
    private val exoPlayer by lazy {
        ExoPlayer.Builder(this).build().apply {
            addListener(exoPlayerListener)
            playWhenReady = true
        }
    }

    /** MediaSession。外部に音楽情報を提供するやつ */
    private val mediaSession by lazy {
        MediaSessionCompat(this, MEDIASESSION_TAG).apply {
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
            exoPlayer.pause()
        }

        /** 再生 */
        override fun onPlay() {
            super.onPlay()
            exoPlayer.play()
            mediaSession.isActive = true
        }

        /** 次の曲 */
        override fun onSkipToNext() {
            super.onSkipToNext()
            exoPlayer.seekToNextMediaItem()
        }

        /** 前の曲 */
        override fun onSkipToPrevious() {
            super.onSkipToPrevious()
            exoPlayer.seekToPreviousMediaItem()
        }

        /** シーク */
        override fun onSeekTo(pos: Long) {
            super.onSeekTo(pos)
            exoPlayer.seekTo(pos)
        }

        /** 終了時 */
        override fun onStop() {
            super.onStop()
            mediaSession.isActive = false
            stopSelf()
        }

        override fun onCustomAction(action: String?, extras: Bundle?) {
            super.onCustomAction(action, extras)
        }

        /** リピートモード変更時 */
        override fun onSetRepeatMode(repeatMode: Int) {
            super.onSetRepeatMode(repeatMode)
            // ExoPlayerに反映
            val exoPlayerRepeatMode = if (repeatMode == PlaybackStateCompat.REPEAT_MODE_ALL) Player.REPEAT_MODE_ALL else Player.REPEAT_MODE_ONE
            exoPlayer.repeatMode = exoPlayerRepeatMode
        }

    }

    /** ExoPlayerのコールバック */
    private val exoPlayerListener = object : Player.Listener {

        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
            super.onPlayWhenReadyChanged(playWhenReady, reason)
            scope.launch {
                updateState()
                updateNotification()
            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            scope.launch {
                updateState()
                updateNotification()
            }
        }

        override fun onPositionDiscontinuity(oldPosition: Player.PositionInfo, newPosition: Player.PositionInfo, reason: Int) {
            super.onPositionDiscontinuity(oldPosition, newPosition, reason)
            scope.launch {
                updateState()
                updateNotification()
            }
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            super.onRepeatModeChanged(repeatMode)
            scope.launch {
                updateState()
                updateNotification()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        // Token登録
        sessionToken = mediaSession.sessionToken
        mediaSession.isActive = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        scope.launch {
            // 再生する動画指定されていればそれに従う
            val requestIndexPos = intent?.getStringExtra(REQUEST_START_VIDEO_ID)

            // 最後の動画から設定を読み出す
            val setting = dataStore.data.first()
            val lastPlayingId = setting[SettingKeyObject.DOWNLOAD_CONTENT_BACKGROUND_PLAYER_LATEST_PLAYING_ID]
            val isRepeatOne = setting[SettingKeyObject.DOWNLOAD_CONTENT_BACKGROUND_PLAYER_REPEAT_MODE] == true

            // 動画をロード
            loadContent(requestIndexPos ?: lastPlayingId)
            exoPlayer.repeatMode = if (isRepeatOne) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_ALL
            exoPlayer.playWhenReady = true
        }
        return START_NOT_STICKY
    }

    /** 外部からの接続が来たとき */
    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot {
        // 最後の曲をリクエストしている場合はtrue
        val isRequestRecentMusic = rootHints?.getBoolean(BrowserRoot.EXTRA_RECENT) == true
        // BrowserRootに入れる値を変える
        val rootPath = if (isRequestRecentMusic) ROOT_REQUIRE_RECENT else ROOT
        return BrowserRoot(rootPath, null)
    }

    /** クライアント一覧へ返す曲をここで設定する */
    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        // とりあえず
        result.detach()
        if (parentId == ROOT_REQUIRE_RECENT) {
            scope.launch(Dispatchers.IO) {
                // 最後に再生した動画のIDとそれから情報を取得
                val setting = dataStore.data.first()
                val latestPlayingId = setting[SettingKeyObject.DOWNLOAD_CONTENT_BACKGROUND_PLAYER_LATEST_PLAYING_ID]
                if (latestPlayingId != null) {
                    val commonVideoData = downloadContentManager.getCommonVideoData(latestPlayingId)
                    result.sendResult(mutableListOf(createMediaItem(commonVideoData)))
                }
            }
        }
    }

    /** 後始末 */
    override fun onDestroy() {
        super.onDestroy()
        scope.launch {
            // 次再生時用に値を保存する
            dataStore.edit {
                val currentPlayingId = exoPlayer.currentMediaItem?.mediaId ?: return@edit
                it[SettingKeyObject.DOWNLOAD_CONTENT_BACKGROUND_PLAYER_LATEST_PLAYING_ID] = currentPlayingId
                it[SettingKeyObject.DOWNLOAD_CONTENT_BACKGROUND_PLAYER_REPEAT_MODE] = exoPlayer.repeatMode == Player.REPEAT_MODE_ONE
            }
            scope.cancel()
        }
        notificationButtonReceiver.release()
        mediaSession.release()
        exoPlayer.release()
    }

    /**
     * ダウンロードコンテンツを読み込んで、ExoPlayerの連続再生の形式にして返す
     *
     * @param startVideoId もしこの動画から再生してほしい！みたいなのがあればその動画ID
     * */
    private suspend fun loadContent(startVideoId: String? = null) {
        val videoList = withContext(Dispatchers.IO) { downloadContentManager.collectDownloadContent().first() }
        if (videoList.isNotEmpty()) {
            // ExoPlayerのプレイリスト登録
            exoPlayer.setMediaItems(videoList.map { downloadContent ->
                MediaItem.Builder().apply {
                    setUri(downloadContent.contentPath)
                    setMediaId(downloadContent.videoId)
                }.build()
            })
            exoPlayer.prepare()
            // MediaSessionのキュー
            mediaSession.setQueue(videoList.map {
                val commonVideoData = it.convertCommonVideoData()
                MediaSessionCompat.QueueItem(createMediaDescription(commonVideoData), it.id.toLong())
            })
            // 位置を出す。-1かもしれないのでmaxで大きい方を取る
            val indexPos = videoList.indexOfFirst { it.videoId == startVideoId }
            exoPlayer.seekTo(max(0, indexPos), 0)
        } else {
            // 無いので終了
            stopSelf()
        }
    }

    /** ExoPlayerの再生状態をMediaSessionへ渡す */
    private suspend fun updateState() {
        // リピートモードの設定
        val mediaSessionRepeatMode = if (exoPlayer.repeatMode == Player.REPEAT_MODE_ALL) PlaybackStateCompat.REPEAT_MODE_ALL else PlaybackStateCompat.REPEAT_MODE_ONE
        mediaSession.setRepeatMode(mediaSessionRepeatMode)
        // 再生状態の提供
        val playbackStateCompat = PlaybackStateCompat.Builder().apply {
            // 受け付ける操作
            setActions(
                PlaybackStateCompat.ACTION_PLAY
                        or PlaybackStateCompat.ACTION_PAUSE
                        or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                        or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                        or PlaybackStateCompat.ACTION_SEEK_TO
                        or PlaybackStateCompat.ACTION_STOP
                        or PlaybackStateCompat.ACTION_SET_REPEAT_MODE
            )
            val currentState = if (exoPlayer.playWhenReady) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED
            setState(currentState, exoPlayer.currentPosition, 1f)
        }.build()
        mediaSession
        // 再生中の音楽情報。DBから持ってくる
        val playingContentId = exoPlayer.currentMediaItem?.mediaId
        if (playingContentId != null) {
            // これ再生時間が秒で入っていないので直したい
            val videoData = downloadContentManager.getCommonVideoData(playingContentId)
            val duration = exoPlayer.duration
            // CoilでBitmap読み込み
            val bitmap = loadBitmap(videoData.thumbnailUrl)
            val mediaMetadataCompat = MediaMetadataCompat.Builder().apply {
                putString(MediaMetadataCompat.METADATA_KEY_TITLE, videoData.videoTitle)
                putString(MediaMetadataCompat.METADATA_KEY_ARTIST, videoData.ownerName)
                putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration) // これあるとAndroid 10でシーク使えます
                putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap)
            }.build()
            mediaSession.setMetadata(mediaMetadataCompat)
        }
        mediaSession.setPlaybackState(playbackStateCompat)
    }

    /** MediaSessionスタイルな通知を出す */
    private fun updateNotification() {
        val channelId = "download_content_background_player_service"
        val channel = NotificationChannelCompat.Builder(channelId, NotificationManagerCompat.IMPORTANCE_LOW).apply {
            setName(getString(R.string.download_content_backrgound_player))
        }.build()
        // チャンネルID登録
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager.getNotificationChannel(channelId) == null) {
            notificationManager.createNotificationChannel(channel)
        }
        val notificationManagerCompat = NotificationCompat.Builder(this@DownloadContentBackgroundPlayerService, channelId).apply {
            setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken).setShowActionsInCompactView(1, 2, 3))
            setSmallIcon(R.drawable.chocodroid_background_player)
            setContentIntent(PendingIntent.getActivity(
                this@DownloadContentBackgroundPlayerService,
                0,
                Intent(this@DownloadContentBackgroundPlayerService, MainActivity::class.java),
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
            ))

            /** addActionしやすいように関数に分ける */
            fun simpleAddAction(icon: Int, title: String, action: String) {
                addAction(
                    icon,
                    title,
                    notificationButtonReceiver.buildPendingIntent(action)
                )
            }

            // リピートボタン
            val isRepeatOne = exoPlayer.repeatMode == Player.REPEAT_MODE_ONE
            simpleAddAction(
                if (isRepeatOne) R.drawable.ic_outline_repeat_one_24 else R.drawable.ic_outline_repeat_24,
                "repeat",
                if (isRepeatOne) NotificationMediaButtonReceiver.PlayerControlType.ACTION_REPEAT_ALL.action else NotificationMediaButtonReceiver.PlayerControlType.ACTION_REPEAT_ONE.action
            )
            // 前の曲ボタン
            simpleAddAction(
                R.drawable.ic_outline_skip_previous_24,
                "prev",
                NotificationMediaButtonReceiver.PlayerControlType.ACTION_SKIP_TO_PREVIOUS.action
            )
            // 一時停止ボタン
            val isPlaying = exoPlayer.playWhenReady
            simpleAddAction(
                if (isPlaying) R.drawable.ic_outline_pause_24 else R.drawable.ic_outline_play_arrow_24,
                if (isPlaying) "pause" else "play",
                if (isPlaying) NotificationMediaButtonReceiver.PlayerControlType.ACTION_PAUSE.action else NotificationMediaButtonReceiver.PlayerControlType.ACTION_PLAY.action
            )
            // 次の曲
            simpleAddAction(
                R.drawable.ic_outline_skip_next_24,
                "next",
                NotificationMediaButtonReceiver.PlayerControlType.ACTION_SKIP_TO_NEXT.action
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
     * */
    private suspend fun loadBitmap(path: String): Bitmap? {
        // CoilでBitmap読み込み
        return imageLoader.execute(ImageRequest.Builder(this).data(path).build()).drawable?.toBitmap()
    }

    /**
     * [CommonVideoData]を[MediaDescriptionCompat]へ変換する
     *
     * @param commonVideoData 動画情報
     * @return [MediaDescriptionCompat]
     * */
    private suspend fun createMediaDescription(commonVideoData: CommonVideoData): MediaDescriptionCompat {
        return MediaDescriptionCompat.Builder().apply {
            setTitle(commonVideoData.videoTitle)
            setSubtitle(commonVideoData.ownerName)
            setMediaId(commonVideoData.videoId)
            setIconBitmap(loadBitmap(commonVideoData.thumbnailUrl))
        }.build()
    }

    /** [onLoadChildren]で返すデータを作成する */
    private suspend fun createMediaItem(commonVideoData: CommonVideoData): MediaBrowserCompat.MediaItem {
        return MediaBrowserCompat.MediaItem(createMediaDescription(commonVideoData), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE)
    }

    companion object {

        private const val NOTIFICATION_ID = 1919
        private const val MEDIASESSION_TAG = "io.github.takusan23.chocodroid.service.DOWNLOAD_CONTENT_BACKGROUND_PLAYER_SERVICE"

        /** putExtra のキーにこれを入れて、値には動画IDをしていすることでその動画から再生を始めます */
        const val REQUEST_START_VIDEO_ID = "request_start_video_id"

        /**
         * バックグラウンド再生を始める
         *
         * @param context [Context]
         * @param startVideoId 開始動画ID
         * */
        fun startService(context: Context, startVideoId: String? = null) {
            ContextCompat.startForegroundService(
                context,
                Intent(context, DownloadContentBackgroundPlayerService::class.java).apply {
                    putExtra(REQUEST_START_VIDEO_ID, startVideoId)
                }
            )
        }

    }

}