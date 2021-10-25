package io.github.takusan23.chocodroid.service

import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.data.DownloadRequestData
import io.github.takusan23.chocodroid.setting.SettingKeyObject
import io.github.takusan23.chocodroid.setting.dataStore
import io.github.takusan23.chocodroid.tool.DownloadContentManager
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
 * 映像と音声を合成するコードはDownloadVideoMuxerを見てください。
 *
 * Intentに以下の内容を詰めてください。
 *
 * DownloadRequestData | KDoc見て
 *
 * このサービスは通知を出しまくるわけですが、ダウンロード進捗通知は[downloadProgressNotificationId]を1000からインクリメントして使っています。
 *
 * 終了時には1000になるまで通知をキャンセルしていってます。
 *
 * 画質とかはそのうち
 * */
class ContentDownloadService : Service() {

    /** フォアグラウンドサービス起動中通知ID */
    private val FOREGROUND_NOTIFICATION_ID = 811

    /** ダウンロード中通知をまとめる通知の通知ID */
    private val DOWNLOAD_PROGRESS_GROUP_NOTIFICATION_ID = 816

    /** ダウンロード進捗通知IDが被らないように。これをインクリメントして使っていく。他と被らないように1000からスタートしてる */
    private var downloadProgressNotificationId = 1000

    /** 通知チャンネルID */
    private val NOTIFICATION_CHANNEL_ID = "io.github.takusan23.chocodroid.download_service_notification"

    /** 通知出すやつ */
    private val notificationManagerCompat by lazy { NotificationManagerCompat.from(this) }

    /** 通知チャンネルとは別に通知をまとめるための通知グループ */
    private val DOWNLOAD_PROGRESS_NOTIFICATION_GROUP = "io.github.takusan23.chocodroid.download_service_progress_notification"

    /** ダウンロード予定動画 */
    private val downloadList = arrayListOf<DownloadRequestData>()

    /** まとめて終了するためにコルーチンスコープ */
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    /** ダウンロード関係のクラス */
    private val downloadContentManager by lazy { DownloadContentManager(this) }

    /** ダウンロード時のファイル分割数 */
    private val downloadSplitCount = 5

    /** ダウンロード中動画を数えてる */
    private val downloadingItemList = arrayListOf<DownloadRequestData>()

    /** ブロードキャストレシーバー */
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "service_stop" -> {
                    coroutineScope.launch {
                        // ダウンロード中のコンテンツは削除する。もしくはレジュームするって手もあるけど
                        downloadingItemList.forEach { downloadContentManager.deleteContent(it.videoId) }
                        // Service終了
                        stopSelf()
                    }
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
        // 通知出し直す
        showNotification()

        // データリクエスト。とりあえず動画と音声のURL
        coroutineScope.launch {
            // 動画URL復号化アルゴリズム情報を取得
            val setting = dataStore.data.first()
            val baseJsURL = setting[SettingKeyObject.WATCH_PAGE_BASE_JS_URL]
            val funcNameData = AlgorithmSerializer.toData<AlgorithmFuncNameData?>(setting[SettingKeyObject.WATCH_PAGE_JS_FUNC_NAME_JSON])
            val funcInvokeDataList = AlgorithmSerializer.toData<List<AlgorithmInvokeData>>(setting[SettingKeyObject.WATCH_PAGE_JS_INVOKE_LIST_JSON])
            // リクエスト
            val (watchPageData) = WatchPageHTML.getWatchPage(downloadRequestData.videoId, baseJsURL, funcNameData, funcInvokeDataList)
            val videoId = watchPageData.watchPageResponseJSONData.videoDetails.videoId
            val videoTitle = watchPageData.watchPageResponseJSONData.videoDetails.title
            // 非同期待機リスト
            val awaitList = arrayListOf<Deferred<Pair<File, DownloadPocket>>>()
            // サムネイル
            awaitList += async {
                downloadContentManager.downloadThumbnailFile(watchPageData).apply {
                    showDownloadNotificationFromFlow(second.progressFlow, "${getString(R.string.download_service_progress_thumbnail)} : $videoTitle")
                    second.start()
                }
            }
            // 音声
            awaitList += async {
                downloadContentManager.downloadAudioFile(watchPageData, downloadRequestData.quality, downloadSplitCount).apply {
                    showDownloadNotificationFromFlow(second.progressFlow, "${getString(R.string.download_service_progress_audio)} : $videoTitle")
                    second.start()
                }
            }
            // 映像もダウンロードする場合は
            if (!downloadRequestData.isAudioOnly) {
                awaitList += async {
                    downloadContentManager.downloadVideoFile(watchPageData, downloadRequestData.quality, downloadSplitCount).apply {
                        showDownloadNotificationFromFlow(second.progressFlow, "${getString(R.string.download_service_progress_video)} : $videoTitle")
                        second.start()
                    }
                }
            }
            // 全部待つ。コルーチン便利すぎる
            val pathList = awaitList.map { it.await() }.map { it.first.path }

            val contentPath = if (!downloadRequestData.isAudioOnly) {
                // 映像ファイルと音声ファイルを合成する場合
                // 通知出す
                val notificationId = showVideoMuxerNotification("${getString(R.string.download_service_mix_notification)} : $videoTitle")
                // 合成開始。サムネのasyncがあるのでdrop(1)する
                val file = downloadContentManager.downloadVideoMix(pathList.drop(1), videoId)
                // 通知消す
                notificationManagerCompat.cancel(notificationId)
                file.path
            } else {
                pathList.last()
            }

            // データベースに追加
            downloadContentManager.insertDownloadContentDB(watchPageData, pathList.first(), contentPath, downloadRequestData.isAudioOnly)

            // 他になければ終了？
            downloadingItemList.remove(downloadRequestData)
            if (downloadingItemList.isEmpty()) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ContentDownloadService, getString(R.string.download_service_complete_toast), Toast.LENGTH_SHORT).show()
                    showNotification()
                }
                stopSelf()
            }
        }

        return START_NOT_STICKY
    }

    /**
     * ダウンロード進捗Flowから通知を作成して出す。
     *
     * @param progressFlow Intを流すFlow
     * @param notificationTitle 通知タイトル
     * */
    private fun showDownloadNotificationFromFlow(progressFlow: StateFlow<Int>, notificationTitle: String) {
        coroutineScope.launch {
            // 通知IDを確保
            val notificationId = downloadProgressNotificationId++
            progressFlow.onEach { progress ->
                if (progress == 100) {
                    cancel() // ちゃんと終了させてあげないと多分メモリに残り続ける上に一生一時停止し続ける
                    notificationManagerCompat.cancel(notificationId)
                } else {
                    println("$notificationTitle = $progress")
                    showDownloadProgressNotification(notificationId, notificationTitle, progress)
                }
            }.collect()
        }
    }

    /** フォアグラウンドサービス起動 */
    private fun showNotification() {
        // 後方互換性があるCompatの方を使う
        val notificationChannelCompat = NotificationChannelCompat.Builder(NOTIFICATION_CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_LOW)
            .setName(getString(R.string.download_service_channel_name))
            .build()
        // 登録してない場合は登録
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManagerCompat.getNotificationChannel(NOTIFICATION_CHANNEL_ID) == null) {
            notificationManagerCompat.createNotificationChannel(notificationChannelCompat)
        }
        // キャンセルボタンのための通知
        val notificationCompat = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID).apply {
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

        // ダウンロードをまとめる通知
        val parentGroupNotification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID).apply {
            setContentTitle(getString(R.string.download_service_progress_notification))
            setContentText(getString(R.string.download_service_progress_notification_description))
            setSmallIcon(R.drawable.chocodroid_download)
            // ダウンロード予定を入れる
            setStyle(NotificationCompat.InboxStyle().also { inboxStyle ->
                downloadList.forEach { inboxStyle.addLine("${it.videoId} : ${if (it.isAudioOnly) "音声のみ" else "映像+音声"}") }
            })
            // 強制終了ボタン置いておく
            val pendingIntentFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
            addAction(R.drawable.ic_outline_cancel_24, getString(R.string.cancel), PendingIntent.getBroadcast(this@ContentDownloadService, 10, Intent("service_stop"), pendingIntentFlag))
            // 通知グループにする。班長
            setGroup(DOWNLOAD_PROGRESS_NOTIFICATION_GROUP)
            setGroupSummary(true) // 親に設定
        }.build()
        notificationManagerCompat.notify(DOWNLOAD_PROGRESS_GROUP_NOTIFICATION_ID, parentGroupNotification)
    }

    /**
     * ダウンロード中の進捗を通知で教える
     *
     * @param notificationId 通知ID
     * @param notificationTitle 通知のタイトル
     * @param progress 進捗 0から100まで
     * */
    private fun showDownloadProgressNotification(notificationId: Int, notificationTitle: String, progress: Int) {
        val downloadProgressNotification = NotificationCompat.Builder(this@ContentDownloadService, NOTIFICATION_CHANNEL_ID).apply {
            setSmallIcon(R.drawable.chocodroid_download)
            setContentTitle(notificationTitle)
            setContentText("$progress %")
            setProgress(100, progress, false)
            setGroup(DOWNLOAD_PROGRESS_NOTIFICATION_GROUP)
        }.build()
        notificationManagerCompat.notify(notificationId, downloadProgressNotification)
    }

    /**
     * 映像と音声を合成して動画ファイル作成中通知を出す。合成処理はDownloadVideoMuxerのstart関数を見てください
     *
     * @param notificationTitle 動画タイトルを入れてください
     * @return 通知IDを返します。通知をキャンセルするときに使ってください
     * */
    private fun showVideoMuxerNotification(notificationTitle: String): Int {
        val notificationId = downloadProgressNotificationId++
        val mixNotification = NotificationCompat.Builder(this@ContentDownloadService, NOTIFICATION_CHANNEL_ID).apply {
            setSmallIcon(R.drawable.chocodroid_mix)
            setContentTitle(notificationTitle)
            setContentText(getString(R.string.download_service_mix_notification_description))
            setGroup(DOWNLOAD_PROGRESS_NOTIFICATION_GROUP)
        }.build()
        notificationManagerCompat.notify(notificationId, mixNotification)
        return notificationId
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
        // ダウンロード中通知を閉じる
        notificationManagerCompat.cancel(DOWNLOAD_PROGRESS_GROUP_NOTIFICATION_ID)
        repeat(downloadProgressNotificationId - 1000) {
            notificationManagerCompat.cancel(it - downloadProgressNotificationId)
        }
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