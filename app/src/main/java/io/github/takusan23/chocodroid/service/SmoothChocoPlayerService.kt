package io.github.takusan23.chocodroid.service

import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.media.session.MediaButtonReceiver
import coil.imageLoader
import coil.request.ImageRequest
import io.github.takusan23.chocodroid.MainActivity
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.player.*
import io.github.takusan23.chocodroid.setting.SettingKeyObject
import io.github.takusan23.chocodroid.setting.dataStore
import io.github.takusan23.chocodroid.tool.StacktraceToString
import io.github.takusan23.internet.data.CommonVideoData
import io.github.takusan23.internet.data.watchpage.MediaUrlData
import io.github.takusan23.internet.data.watchpage.WatchPageData
import io.github.takusan23.internet.data.watchpage.WatchPageResponseJSONData
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import java.lang.ref.WeakReference

class SmoothChocoPlayerService : Service() {

    /** コルーチン起動時の引数に指定してね。例外を捕まえ、Flowに流します */
    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        _errorMessageFlow.value = StacktraceToString.stackTraceToString(throwable)
        _isLoadingFlow.value = false
    }
    private val scope = CoroutineScope(Job() + Dispatchers.Main + errorHandler)
    private val notificationManager by lazy { NotificationManagerCompat.from(this) }
    private val binder = LocalBinder(this)

    /** フォアグラウンドサービスで実行中かどうか。バインド中はフォアグラウンドサービスではない */
    private var isRunningForegroundService = false

    /** コンテンツ取得する */
    private val contentLoader by lazy { ChocoDroidContentLoader(this) }

    /** [MediaButtonReceiver]がリピートモードに対応してないので自前で書いた */
    private val notificationButtonReceiver by lazy { NotificationMediaButtonReceiver(this, mediaSession.controller.transportControls) }

    /** MediaSession。外部に音楽情報を提供するやつ */
    private val mediaSession by lazy {
        MediaSessionCompat(this, MEDIA_SESSION_TAG).apply {
            setCallback(mediaSessionCallback)
        }
    }

    /** MediaSessionで受け付けている操作のコールバック */
    private val mediaSessionCallback: MediaSessionCompat.Callback = object : MediaSessionCompat.Callback() {
        /** 一時停止 */
        override fun onPause() {
            super.onPause()
            chocoDroidPlayer?.playWhenReady = false
        }

        /** 再生 */
        override fun onPlay() {
            super.onPlay()
            chocoDroidPlayer?.playWhenReady = true
            mediaSession.isActive = true
        }

        /** シーク */
        override fun onSeekTo(pos: Long) {
            super.onSeekTo(pos)
            chocoDroidPlayer?.currentPositionMs = pos
        }

        /** 終了時 */
        override fun onStop() {
            super.onStop()
            mediaSession.isActive = false
            // もう使わないのでプレイヤーも終了する
            stopSelf()
        }

        /** Android 13 以降は CustomAction を利用して通知領域にアイコンを追加する（リピートなど） */
        override fun onCustomAction(action: String?, extras: Bundle?) {
            super.onCustomAction(action, extras)
            when (action) {
                MEDIA_SESSION_CUSTOM_REPEAT -> {
                    scope.launch {
                        dataStore.edit { it[SettingKeyObject.PLAYER_REPEAT_MODE] = it[SettingKeyObject.PLAYER_REPEAT_MODE] == false }
                        updateState(watchPageResponseDataFlow.first()!!.watchPageResponseJSONData)
                    }
                }
                MEDIA_SESSION_CUSTOM_CLOSE -> stopSelf()
            }
        }

        /** リピートモード変更時 */
        override fun onSetRepeatMode(repeatMode: Int) {
            super.onSetRepeatMode(repeatMode)
            // ExoPlayerに反映
            scope.launch {
                dataStore.edit { it[SettingKeyObject.PLAYER_REPEAT_MODE] = repeatMode == PlaybackStateCompat.REPEAT_MODE_ONE }
                updateState(watchPageResponseDataFlow.first()!!.watchPageResponseJSONData)
                updateNotification()
            }
        }
    }

    // 視聴ページデータ
    private val _isLoadingFlow = MutableStateFlow(false)
    private val _errorMessageFlow = MutableStateFlow<String?>(null)
    private val _currentQualityData = MutableStateFlow<MediaUrlData?>(null)
    private val _watchPageDataFlow = MutableStateFlow<WatchPageData?>(null)

    // プレイヤーの状態
    private val _playbackStateFlow = MutableStateFlow<PlayerState>(PlayerState.Destroy)
    private val _currentPositionDataFlow = MutableStateFlow(CurrentPositionData(0, 0))
    private val _videoMetaDataFlow = MutableStateFlow(VideoMetaData(0, 1.7f)) // copy するので適当な初期値を入れる

    /** 動画情報データクラスを保持するFlow。外部公開用は受け取りのみ */
    val watchPageResponseDataFlow = _watchPageDataFlow.asStateFlow() // 初期値nullだけどnull流したくないので

    /** 再生中の画質 */
    val currentQualityData = _currentQualityData.asStateFlow()

    /** 読み込み中？ */
    val isLoadingFlow = _isLoadingFlow.asStateFlow()

    /** エラーメッセージ送信用Flow */
    val errorMessageFlow = _errorMessageFlow.asStateFlow()

    /** 動画プレイヤー */
    var chocoDroidPlayer: ChocoDroidPlayer? = null
        private set

    /** 現在の再生状態 */
    val playbackStateFlow = _playbackStateFlow.asStateFlow()

    /** 現在の再生位置とバッファリング位置 */
    val currentPositionDataFlow = _currentPositionDataFlow.asStateFlow()

    /** 動画情報 */
    val videoMetaDataFlow = _videoMetaDataFlow.asStateFlow()

    /** 視聴行動中か。破棄時とエラー時以外 */
    val isContentPlaying: Boolean
        get() = playbackStateFlow.value == PlayerState.Play
                || playbackStateFlow.value == PlayerState.Pause
                || playbackStateFlow.value == PlayerState.Buffering

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()

        // プレイヤー状態変化に対応
        merge(playbackStateFlow, currentPositionDataFlow).onEach {
            watchPageResponseDataFlow.firstOrNull()?.watchPageResponseJSONData?.also { watchPageResponseJSONData ->
                updateState(watchPageResponseJSONData)
                if (isRunningForegroundService) {
                    updateNotification()
                } else {
                    ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
                }
            }
        }.launchIn(scope)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        isRunningForegroundService = intent?.getBooleanExtra(KEY_SERVICE_FOREGROUND_TYPE, false) == true
        if (isRunningForegroundService) {
            // フォアグラウンドサービスに昇格させる
            scope.launch {
                watchPageResponseDataFlow.firstOrNull()?.watchPageResponseJSONData?.also { watchPageResponseJSONData ->
                    updateState(watchPageResponseJSONData)
                }
                updateNotification()
            }
        } else {
            // UI とバインドしたらフォアグラウンドサービスを解除する
            ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        }
        return START_NOT_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        // 多分タスクキル時に onStop で呼んでる startForeground のせいで落ちてしまうので？通知を出してから終了させる
        updateNotification()
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        notificationButtonReceiver.release()
        destroy()
    }

    /**
     * ようつべ視聴ページを読み込み、再生する
     *
     * @param videoIdOrHttpUrl 動画IDもしくは動画URL
     */
    fun loadWatchPage(videoIdOrHttpUrl: String) {
        scope.launch {
            chocoDroidPlayer?.destroy()
            chocoDroidPlayer = createChocoPlayer()
            // 視聴ページ取得
            val watchPageData = loadingTask { contentLoader.loadWatchPage(videoIdOrHttpUrl) }
            _watchPageDataFlow.value = watchPageData
            selectQuality()
            updateState(watchPageData.watchPageResponseJSONData)
        }
    }

    /**
     * ダウンロード済み動画を読み込み、再生する
     *
     * @param videoId 動画ID
     */
    fun loadWatchPageFromLocal(videoId: String) {
        scope.launch {
            chocoDroidPlayer?.destroy()
            chocoDroidPlayer = createChocoPlayer()
            val watchPageData = loadingTask { contentLoader.loadWatchPageFromLocal(videoId) }
            _watchPageDataFlow.value = watchPageData
            selectQuality()
            updateState(watchPageData.watchPageResponseJSONData)
        }
    }

    /**
     * 画質の変更を行い再生する
     *
     * @param quality 画質。省略時は前回見ていた画質、それがない場合は360p、それもない場合は最高画質
     */
    fun selectQuality(quality: String? = null) {
        scope.launch {
            // 前回の画質。ない場合は 360p
            val prevQuality = quality ?: dataStore.data.map { it[SettingKeyObject.PLAYER_QUALITY_VIDEO] }.first() ?: "360p"
            // 画質を選ぶ
            val mediaUrlData = _watchPageDataFlow.value?.getMediaUrlDataFromQuality(prevQuality) ?: return@launch
            // 保存する
            dataStore.edit { it[SettingKeyObject.PLAYER_QUALITY_VIDEO] = prevQuality }
            _currentQualityData.value = mediaUrlData
            // Hls/DashのManifestがあればそれを読み込む（生放送、一部の動画）。
            // ない場合は映像、音声トラックをそれぞれ渡す
            if (mediaUrlData.mixTrackUrl != null) {
                val isDash = mediaUrlData.urlType == MediaUrlData.MediaUrlType.TYPE_DASH
                chocoDroidPlayer?.setMediaSourceUri(mediaUrlData.mixTrackUrl!!, isDash)
            } else {
                chocoDroidPlayer?.setMediaSourceVideoAudioUriSupportVer(mediaUrlData.videoTrackUrl!!, mediaUrlData.audioTrackUrl!!)
            }
            // ダブルタップシークを実装した際に、初回ロード中にダブルタップすることで即時再生されることを発見したので、
            // わからないレベルで進めておく。これで初回のめっちゃ長い読み込みが解決する？
            if (_watchPageDataFlow.value?.isLiveContent == false) {
                chocoDroidPlayer?.currentPositionMs = 10L
            }
        }
    }

    /** プレイヤーを終了させる */
    fun destroy() {
        chocoDroidPlayer?.destroy()
        scope.coroutineContext.cancelChildren()
        _errorMessageFlow.value = null
        _currentQualityData.value = null
        _watchPageDataFlow.value = null
        _playbackStateFlow.value = PlayerState.Destroy
    }

    /** プレイヤーを作成する */
    private fun createChocoPlayer(): ChocoDroidPlayer = ChocoDroidPlayer(
        context = this,
        onPlayerState = { _playbackStateFlow.value = it },
        onCurrentPositionData = { _currentPositionDataFlow.value = it },
        onVideoAspectRate = { _videoMetaDataFlow.value = videoMetaDataFlow.value.copy(aspectRate = it) },
        onVideoDuration = { _videoMetaDataFlow.value = videoMetaDataFlow.value.copy(durationMs = it) }
    )

    /** ブロック内のタスクが終わるまで [isLoadingFlow] を true にする */
    private suspend fun <T> loadingTask(block: suspend () -> T): T {
        _isLoadingFlow.value = true
        val result = block()
        _isLoadingFlow.value = false
        return result
    }

    /** ExoPlayerの再生状態をMediaSessionへ渡す */
    private suspend fun updateState(watchPageResponseJSONData: WatchPageResponseJSONData) {
        // リピートモードの設定
        val mediaSessionRepeatMode = if (chocoDroidPlayer?.repeatMode == true) PlaybackStateCompat.REPEAT_MODE_ONE else PlaybackStateCompat.REPEAT_MODE_ALL
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
            val currentState = if (chocoDroidPlayer?.playWhenReady == true) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED
            setState(currentState, chocoDroidPlayer?.currentPositionMs ?: -1L, 1f)
            // Android 13 以降 CustomAction に定義しないと通知領域に表示されるコントローラーには出ない
            // リピートモード切り替えをカスタムアクションとして定義する
            if (chocoDroidPlayer?.repeatMode == true) {
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
        val durationMs = watchPageResponseJSONData.videoDetails.lengthSeconds * 1000L
        // CoilでBitmap読み込み
        val bitmap = loadBitmap(videoData.thumbnailUrl)
        val mediaMetadataCompat = MediaMetadataCompat.Builder().apply {
            putString(MediaMetadataCompat.METADATA_KEY_TITLE, videoData.videoTitle)
            putString(MediaMetadataCompat.METADATA_KEY_ARTIST, videoData.ownerName)
            putLong(MediaMetadataCompat.METADATA_KEY_DURATION, durationMs)
            putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap)
        }.build()
        mediaSession.setMetadata(mediaMetadataCompat)
        mediaSession.setPlaybackState(playbackStateCompat)
    }

    /** MediaSessionスタイルな通知を表示させ、サービスをフォアグラウンドで実行する */
    private fun updateNotification() {
        // TODO ダウンロード連続再生と同じ通知チャンネル
        val channelId = "download_content_background_player_service"
        val channel = NotificationChannelCompat.Builder(channelId, NotificationManagerCompat.IMPORTANCE_LOW).apply {
            setName(getString(R.string.download_content_backrgound_player))
        }.build()
        // チャンネルID登録
        if (notificationManager.getNotificationChannel(channelId) == null) {
            notificationManager.createNotificationChannel(channel)
        }
        val notificationManagerCompat = NotificationCompat.Builder(this, channelId).apply {
            setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken).setShowActionsInCompactView(0, 1, 2))
            setSmallIcon(R.drawable.chocodroid_background_player)
            setContentIntent(
                PendingIntent.getActivity(
                    this@SmoothChocoPlayerService,
                    0,
                    Intent(this@SmoothChocoPlayerService, MainActivity::class.java),
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
                )
            )

            /**
             * addActionしやすいように関数に分ける。
             * Android 13 以降、ここの AddAction は無視される。（MediaSessionのPlaybackStateで書いた操作が優先される）
             */
            fun simpleAddAction(icon: Int, title: String, action: String) {
                addAction(icon, title, notificationButtonReceiver.buildPendingIntent(action))
            }

            // リピートボタン
            val isRepeatMode = chocoDroidPlayer?.repeatMode == true
            simpleAddAction(
                if (!isRepeatMode) R.drawable.ic_outline_repeat_24 else R.drawable.ic_outline_repeat_one_24,
                "repeat",
                if (!isRepeatMode) NotificationMediaButtonReceiver.PlayerControlType.ACTION_REPEAT_ONE.action else NotificationMediaButtonReceiver.PlayerControlType.ACTION_REPEAT_ALL.action
            )
            // 一時停止ボタン
            val isPlaying = chocoDroidPlayer?.playWhenReady == true
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
     * @return [Bitmap]
     */
    private suspend fun loadBitmap(path: String): Bitmap? {
        // CoilでBitmap読み込み
        return imageLoader.execute(ImageRequest.Builder(this).data(path).build()).drawable?.toBitmap()
    }

    /** Binder を利用して UI と Service をつなげる */
    private class LocalBinder(service: SmoothChocoPlayerService) : Binder() {
        val serviceRef = WeakReference(service)

        /** サービスのインスタンスを返す */
        val service: SmoothChocoPlayerService
            get() = serviceRef.get()!!
    }

    companion object {

        /** 通知ID */
        private const val NOTIFICATION_ID = 20230114

        /** MediaSessionのやつ */
        private const val MEDIA_SESSION_TAG = "io.github.takusan23.chocodroid.service.SMOOTH_BACKGROUND_PLAY_SERVICE"

        /** MediaSessionリピート切り替えカスタムアクション */
        private const val MEDIA_SESSION_CUSTOM_REPEAT = "io.github.takusan23.chocodroid.service.MEDIA_SESSION_CUSTOM_REPEAT"

        /** MediaSessionプレイヤー終了カスタムアクション */
        private const val MEDIA_SESSION_CUSTOM_CLOSE = "io.github.takusan23.chocodroid.service.MEDIA_SESSION_CUSTOM_CLOSE"

        /** サービスをフォアグラウンド化するかどうか */
        private const val KEY_SERVICE_FOREGROUND_TYPE = "io.github.takusan23.chocodroid.service.KEY_SERVICE_FOREGROUND_TYPE"

        /**
         * サービスとバインドして[SmoothChocoPlayerService]のインスタンスを取得する。
         * フォアグラウンドサービスの場合はフォアグラウンド化が解除される。
         *
         * またライフサイクルを追跡する機能があります。
         *
         * @param context [Context]
         * @param lifecycleOwner [LifecycleOwner]
         */
        fun bindSmoothChocoPlayer(
            context: Context,
            lifecycleOwner: LifecycleOwner
        ) = callbackFlow {
            val serviceConnection = object : ServiceConnection {
                override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                    trySend((service as LocalBinder).service)
                }

                override fun onServiceDisconnected(name: ComponentName?) {
                    trySend(null)
                }
            }
            lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onStart(owner: LifecycleOwner) {
                    super.onStart(owner)
                    // サービス起動。ここではフォアグラウンドはしない
                    Intent(context, SmoothChocoPlayerService::class.java).also { intent ->
                        intent.putExtra(KEY_SERVICE_FOREGROUND_TYPE, false)
                        context.startService(intent)
                    }
                    // バインドする
                    Intent(context, SmoothChocoPlayerService::class.java).also { intent ->
                        context.bindService(intent, serviceConnection, BIND_AUTO_CREATE)
                    }
                }

                override fun onStop(owner: LifecycleOwner) {
                    super.onStop(owner)
                    // フォアグラウンド化する
                    Intent(context, SmoothChocoPlayerService::class.java).also { intent ->
                        intent.putExtra(KEY_SERVICE_FOREGROUND_TYPE, true)
                        ContextCompat.startForegroundService(context, intent)
                    }
                    // バインドを解除
                    context.unbindService(serviceConnection)
                }
            })
            awaitClose()
        }

        /**
         * サービスを終了させる
         *
         * @param context [Context]
         */
        fun stopService(context: Context) {
            Intent(context, SmoothChocoPlayerService::class.java).also { intent ->
                context.stopService(intent)
            }
        }
    }

}