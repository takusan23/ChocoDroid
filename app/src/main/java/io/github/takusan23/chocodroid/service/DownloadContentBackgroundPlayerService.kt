package io.github.takusan23.chocodroid.service

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
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.setting.SettingKeyObject
import io.github.takusan23.chocodroid.setting.dataStore
import io.github.takusan23.chocodroid.tool.DownloadContentManager
import io.github.takusan23.internet.data.CommonVideoData
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first

/**
 * ダウンロード済みのコンテンツはバックグラウンドで連続再生できるようにする
 * */
class DownloadContentBackgroundPlayerService : MediaBrowserServiceCompat() {

    private val NOTIFICATION_ID = 1919
    private val MEDIASESSION_TAG = "io.github.takusan23.chocodroid.service.DOWNLOAD_CONTENT_BACKGROUND_PLAYER_SERVICE"

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

    /** MediaSessionで受け付けている操作のコールバック */
    private val mediaSessionCallback: MediaSessionCompat.Callback = object : MediaSessionCompat.Callback() {
        /** 一時停止 */
        override fun onPause() {
            super.onPause()
            exoPlayer.pause()
            mediaSession.isActive = false
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
            stopSelf()
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
        // 動画をロード
        scope.launch { loadContent() }
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
                // 最後に再生したコンテンツのIDとそれから情報を取得
                val latestPlayingId = getLatestPlayVideoId()
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
        // 今再生中のコンテンツIDを控える
        scope.launch {
            dataStore.edit {
                val currentPlayingId = exoPlayer.currentMediaItem?.mediaId ?: return@edit
                it[SettingKeyObject.DOWNLOAD_CONTENT_BACKGROUND_PLAYER_LATEST_PLAYING_ID] = currentPlayingId
            }
            scope.cancel()
        }
        mediaSession.release()
        exoPlayer.release()
    }

    /** ダウンロードコンテンツを読み込んで、ExoPlayerの連続再生の形式にして返す */
    private suspend fun loadContent() {
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
                val commonVideoData = it.toCommonVideoData(this)
                MediaSessionCompat.QueueItem(createMediaDescription(commonVideoData), it.id.toLong())
            })
        } else {
            // 無いので終了
            stopSelf()
        }
    }

    /** ExoPlayerの再生状態をMediaSessionへ渡す */
    private suspend fun updateState() {
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
            )
            val currentState = if (exoPlayer.playWhenReady) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED
            setState(currentState, exoPlayer.currentPosition, 1f)
        }.build()
        // 再生中の音楽情報。DBから持ってくる
        val playingContentId = exoPlayer.currentMediaItem?.mediaId
        if (playingContentId != null) {
            // これ再生時間が秒で入っていないので直したい
            val videoData = downloadContentManager.getCommonVideoData(playingContentId)
            val duration = exoPlayer.duration
            // CoilでBitmap読み込み
            val bitmap = imageLoader.execute(ImageRequest.Builder(this).data(videoData.thumbnailUrl).build())
                .drawable
                ?.toBitmap()
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
            setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken).setShowActionsInCompactView(0, 1, 2))
            setSmallIcon(R.drawable.chocodroid_background_player)
            // ボタン
            addAction(R.drawable.ic_outline_skip_previous_24, "prev", MediaButtonReceiver.buildMediaButtonPendingIntent(this@DownloadContentBackgroundPlayerService, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS))
            val isPlaying = exoPlayer.playWhenReady
            addAction(
                if (isPlaying) R.drawable.ic_outline_pause_24 else R.drawable.ic_outline_play_arrow_24,
                if (isPlaying) "pause" else "play",
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    this@DownloadContentBackgroundPlayerService,
                    if (isPlaying) PlaybackStateCompat.ACTION_PAUSE else PlaybackStateCompat.ACTION_PLAY
                )
            )
            addAction(R.drawable.ic_outline_skip_next_24, "next", MediaButtonReceiver.buildMediaButtonPendingIntent(this@DownloadContentBackgroundPlayerService, PlaybackStateCompat.ACTION_SKIP_TO_NEXT))
            addAction(R.drawable.ic_outline_close_24, "stop", MediaButtonReceiver.buildMediaButtonPendingIntent(this@DownloadContentBackgroundPlayerService, PlaybackStateCompat.ACTION_STOP))
        }.build()
        startForeground(NOTIFICATION_ID, notificationManagerCompat)
    }

    /**
     * 最後に再生した動画IDを返す
     *
     * @return 再生したことない場合はnull
     * */
    private suspend fun getLatestPlayVideoId(): String? {
        val setting = dataStore.data.first()
        return setting[SettingKeyObject.DOWNLOAD_CONTENT_BACKGROUND_PLAYER_LATEST_PLAYING_ID]
    }

    /**
     * CoilでBitmap読み込みを行う。コルーチン内で使って下さい
     *
     * @param path 画像パス
     * @return Bitmap
     * */
    private suspend fun loadBitmap(path: String): Bitmap? {
        // CoilでBitmap読み込み
        return imageLoader.execute(ImageRequest.Builder(this).data(path).build())
            .drawable
            ?.toBitmap()
    }

    /**
     * [CommonVideoData]を[MediaDescriptionCompat]へ変換する
     *
     * @param commonVideoData コンテンツ情報
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

        /**
         * バックグラウンド再生を始める
         * */
        fun startService(context: Context) {
            ContextCompat.startForegroundService(context, Intent(context, DownloadContentBackgroundPlayerService::class.java))
        }

    }

}