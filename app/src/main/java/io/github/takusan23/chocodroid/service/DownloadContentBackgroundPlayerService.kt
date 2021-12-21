package io.github.takusan23.chocodroid.service

import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.MediaBrowserServiceCompat
import io.github.takusan23.chocodroid.setting.SettingKeyObject
import io.github.takusan23.chocodroid.setting.dataStore
import io.github.takusan23.chocodroid.tool.DownloadContentManager
import io.github.takusan23.internet.data.CommonVideoData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * ダウンロード済みのコンテンツはバックグラウンドで連続再生できるようにする
 * */
class DownloadContentBackgroundPlayerService : MediaBrowserServiceCompat() {

    private val MEDIASESSION_TAG = "io.github.takusan23.chocodroid.service.DOWNLOAD_CONTENT_BACKGROUND_PLAYER_SERVICE"

    /** システムが最後の曲を要求している場合はこっち */
    private val ROOT_REQUIRE_RECENT = ""

    /** それ以外 */
    private val ROOT = ""

    /** データ読み込み等で使うコルーチンスコープ */
    private val scope = CoroutineScope(Dispatchers.Default)

    /** ダウンロードコンテンツマネージャー */
    private val downloadContentManager by lazy { DownloadContentManager(this) }

    /** MediaSession。外部に音楽情報を提供するやつ */
    private val mediaSession by lazy {
        MediaSessionCompat(this, MEDIASESSION_TAG).apply {
            setCallback(callback)
        }
    }

    /** MediaSessionで受け付けている操作のコールバック */
    private val callback = object : MediaSessionCompat.Callback() {
        override fun onPause() {
            super.onPause()
        }

        override fun onPlay() {
            super.onPlay()
        }

        override fun onSkipToNext() {
            super.onSkipToNext()
        }

        override fun onSkipToPrevious() {
            super.onSkipToPrevious()
        }

        override fun onSeekTo(pos: Long) {
            super.onSeekTo(pos)
        }
    }

    override fun onCreate() {
        super.onCreate()
        // Token登録
        sessionToken = mediaSession.sessionToken
    }

    /** 外部からの接続が来たとき */
    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot? {
        // 最後の曲をリクエストしている場合はtrue
        val isRequestRecentMusic = rootHints?.getBoolean(BrowserRoot.EXTRA_RECENT) ?: false
        // BrowserRootに入れる値を変える
        val rootPath = if (isRequestRecentMusic) ROOT_REQUIRE_RECENT else ROOT
        return BrowserRoot(rootPath, null)
    }

    /** クライアント一覧へ返す曲をここで設定する */
    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        // とりあえず
        result.detach()
        if (parentId == ROOT_REQUIRE_RECENT) {
            scope.launch {
                // 最後に再生したコンテンツのIDとそれから情報を取得
                val latestPlayingId = getLatestPlayVideoId()
                if (latestPlayingId != null) {
                    val watchPageData = downloadContentManager.getWatchPageData(latestPlayingId)
                    result.sendResult(mutableListOf(createMediaItem(CommonVideoData(watchPageData.watchPageResponseJSONData))))
                }
            }
        }
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

    /** [onLoadChildren]で返すデータを作成する */
    private fun createMediaItem(commonVideoData: CommonVideoData): MediaBrowserCompat.MediaItem {
        val mediaDescriptionCompat = MediaDescriptionCompat.Builder().apply {
            setTitle(commonVideoData.videoTitle)
            setSubtitle(commonVideoData.ownerName)
            setMediaId(commonVideoData.videoId)
            setIconBitmap(BitmapFactory.decodeFile(commonVideoData.thumbnailUrl))
        }.build()
        return MediaBrowserCompat.MediaItem(mediaDescriptionCompat, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE)
    }

    /** 後始末 */
    override fun onDestroy() {
        super.onDestroy()
        mediaSession.release()
        scope.cancel()
    }

}