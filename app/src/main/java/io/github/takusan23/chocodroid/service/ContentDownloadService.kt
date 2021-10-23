package io.github.takusan23.chocodroid.service

import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.data.DownloadRequestData
import io.github.takusan23.chocodroid.setting.SettingKeyObject
import io.github.takusan23.chocodroid.setting.dataStore
import io.github.takusan23.downloadpocket.DownloadPocket
import io.github.takusan23.internet.html.WatchPageHTML
import io.github.takusan23.internet.magic.AlgorithmSerializer
import io.github.takusan23.internet.magic.data.AlgorithmFuncNameData
import io.github.takusan23.internet.magic.data.AlgorithmInvokeData
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.File

/**
 * 動画 / 音声 ダウンロードサービス
 *
 * Intentに以下の内容を詰めてください。
 *
 * video_id | String | 動画ID
 *
 * 画質とかはそのうち
 * */
class ContentDownloadService : Service() {

    /** フォアグラウンドサービス起動中通知ID */
    private val FOREGROUND_NOTIFICATION_ID = 811

    /** 通知出すやつ */
    private val notificationManagerCompat by lazy { NotificationManagerCompat.from(this) }

    /** ダウンロード予定動画 */
    private val downloadList = arrayListOf<DownloadRequestData>()

    /** まとめて終了するためにコルーチンスコープ */
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    /** 分割ダウンロード時に一時的に使うフォルダを作成するフォルダ */
    private val tempFolderRootFolder by lazy { externalCacheDir!! }

    /** ダウンロードしたデータ保存先 */
    private val downloadFolderRootFolder by lazy { File(getExternalFilesDir(null), "download").apply { mkdir() } }

    /** ダウンロード中動画を数えてる */
    private val downloadingItemList = arrayListOf<DownloadRequestData>()

    /** ブロードキャストレシーバー */
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "service_stop" -> {
                    // Service強制終了
                    stopSelf()
                }
            }
        }
    }


    override fun onCreate() {
        super.onCreate()
        // フォアグラウンドサービス起動のために通知出す
        showNotification()
        // サービス終了用ブロードキャストを登録
        initBroadcastReceiver()
    }

    /**
     * サービス起動中にサービス起動をするとonCreateじゃなくてこっちだけが呼ばれる？
     * */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val downloadRequestData = intent?.getSerializableExtra("request")!! as DownloadRequestData
        downloadingItemList.add(downloadRequestData)

        // データリクエスト。とりあえず動画と音声のURL
        coroutineScope.launch {
            // 動画URL復号化アルゴリズム情報を取得
            val setting = dataStore.data.first()
            val baseJsURL = setting[SettingKeyObject.WATCH_PAGE_BASE_JS_URL]
            val funcNameData = AlgorithmSerializer.toData<AlgorithmFuncNameData?>(setting[SettingKeyObject.WATCH_PAGE_JS_FUNC_NAME_JSON])
            val funcInvokeDataList = AlgorithmSerializer.toData<List<AlgorithmInvokeData>>(setting[SettingKeyObject.WATCH_PAGE_JS_INVOKE_LIST_JSON])
            // リクエスト
            val watchPage = WatchPageHTML.getWatchPage(downloadRequestData.videoId, baseJsURL, funcNameData, funcInvokeDataList)
            val videoId = watchPage.watchPageJSONResponseData.videoDetails.videoId
            val mediaUrlData = watchPage.getMediaUrl()

            val awaitList = arrayListOf(async { startDownload(mediaUrlData.audioTrackUrl, videoId, true) })
            // 動画もダウンロードする場合
            if (!downloadRequestData.isAudioOnly) {
                awaitList.add(async { startDownload(mediaUrlData.videoTrackUrl, videoId, false) })
            }
            // 全部待つ。コルーチン便利すぎる
            awaitList.forEach { it.await() }

            // 他になければ終了？
            downloadingItemList.remove(downloadRequestData)
            if (downloadingItemList.isEmpty()) {
                stopSelf()
            }

        }

        // 通知出し直す
        showNotification()

        return START_NOT_STICKY
    }

    /**
     * ダウンロードする関数
     *
     * @param fileName ファイル名
     * @param isAudio 音声ファイルの場合はtrue
     * @param url URL
     * */
    private suspend fun startDownload(url: String, fileName: String, isAudio: Boolean) = withContext(Dispatchers.IO) {
        // ファイル名
        val splitFolderName = if (isAudio) "${fileName}_audio" else "${fileName}_video"
        val resultFileName = if (isAudio) "${fileName}.aac" else "${fileName}.mp4"
        // 分割ファイル保存先
        val splitFileFolder = File(tempFolderRootFolder, splitFolderName).apply { mkdir() }
        // 最終的なファイル
        val videoFileFolder = File(downloadFolderRootFolder, fileName).apply { mkdir() }
        val resultContentFile = File(videoFileFolder, resultFileName).apply { createNewFile() }
        // とりあえずリクエスト
        val downloadPocket = DownloadPocket(
            url = url,
            splitFileFolder = splitFileFolder,
            resultVideoFile = resultContentFile,
            splitCount = 5
        )
        launch {
            downloadPocket.progressFlow
                .onEach { println("${if (isAudio) "音声DL" else "映像DL"} = $it") }
                .onEach { if (it == 100) cancel() } // ちゃんと終了させてあげないと多分メモリに残り続ける上に一生一時停止し続ける
                .collect()
        }
        // ダウンロード終了まで一時停止
        downloadPocket.start()
    }

    /** フォアグラウンドサービス起動 */
    private fun showNotification() {
        val channelId = "download_service"
        // 後方互換性があるCompatの方を使う
        val notificationChannelCompat = NotificationChannelCompat.Builder(channelId, NotificationManagerCompat.IMPORTANCE_LOW)
            .setName(getString(R.string.download_service_channel_name))
            .build()
        // 登録してない場合は登録
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManagerCompat.getNotificationChannel(channelId) == null) {
            notificationManagerCompat.createNotificationChannel(notificationChannelCompat)
        }
        // 通知の中身
        val notificationCompat = NotificationCompat.Builder(this, channelId).apply {
            setContentTitle(getString(R.string.download_service_notification_title))
            setSmallIcon(R.drawable.chocodroid_download)
            // ダウンロード予定を入れる
            setStyle(NotificationCompat.InboxStyle().also { inboxStyle ->
                downloadList.forEach { inboxStyle.addLine(it.videoId) }
            })
            // 強制終了ボタン置いておく
            val pendingIntentFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
            addAction(R.drawable.ic_outline_cancel_24, getString(R.string.cancel), PendingIntent.getBroadcast(this@ContentDownloadService, 10, Intent("service_stop"), pendingIntentFlag))
        }.build()
        startForeground(FOREGROUND_NOTIFICATION_ID, notificationCompat)
    }

    /** 通知ボタンのBroadCastReceiver */
    private fun initBroadcastReceiver() {
        val intentFilter = IntentFilter()
        intentFilter.addAction("service_stop")
        registerReceiver(broadcastReceiver, intentFilter)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    /** 終了時 */
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
        coroutineScope.cancel()
    }

    companion object {

        /**
         * ダウンロードサービスを起動して、動画 / 音声 をダウンロードする
         *
         * 引数のデータクラスはシリアライズ化されてIntentに追加されます。
         *
         * @param context Context
         * @param downloadRequestData 動画IDと画質とかを指定したデータクラス。
         * */
        fun startDownloadService(context: Context, downloadRequestData: DownloadRequestData) {
            val intent = Intent(context, ContentDownloadService::class.java).apply {
                putExtra("request", downloadRequestData)
            }
            ContextCompat.startForegroundService(context, intent)
        }

    }

}