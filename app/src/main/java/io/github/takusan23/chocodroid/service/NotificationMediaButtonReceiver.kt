package io.github.takusan23.chocodroid.service

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.media.session.MediaButtonReceiver

/**
 * [MediaButtonReceiver]がリピートモードに対応していないので自前でBroadcastReceiverを書く。だっる
 *
 * 終わったら[release]を呼んで下さい
 *
 * @param context [Context]
 * @param transportControls Broadcastで来たイベントを[MediaControllerCompat.TransportControls]にあるそれぞれの関数を呼ぶ
 * */
class NotificationMediaButtonReceiver(
    private val context: Context,
    private val transportControls: MediaControllerCompat.TransportControls,
) {

    /** PendingIntentのRequestCodeをユニークにするために */
    private var requestCodeIncrement = 0

    /** イベント受け取り */
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            println(intent?.action)
            when (intent?.action) {
                ACTION_PLAY -> transportControls.play()
                ACTION_PAUSE -> transportControls.pause()
                ACTION_SKIP_TO_NEXT -> transportControls.skipToNext()
                ACTION_SKIP_TO_PREVIOUS -> transportControls.skipToPrevious()
                ACTION_STOP -> transportControls.stop()
                ACTION_REPEAT_ONE -> transportControls.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ONE)
                ACTION_REPEAT_ALL -> transportControls.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ALL)
            }
        }
    }

    init {
        // Broadcast登録
        context.registerReceiver(broadcastReceiver, IntentFilter().apply {
            addAction(ACTION_PLAY)
            addAction(ACTION_PAUSE)
            addAction(ACTION_SKIP_TO_NEXT)
            addAction(ACTION_SKIP_TO_PREVIOUS)
            addAction(ACTION_STOP)
            addAction(ACTION_REPEAT_ONE)
            addAction(ACTION_REPEAT_ALL)
        })
    }

    /**
     * PendingIntentを生成する
     *
     * @param action [ACTION_PLAY]とか
     * */
    fun buildPendingIntent(action: String) = PendingIntent.getBroadcast(
        context,
        requestCodeIncrement++,
        Intent(action),
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
    )

    /** BroadcastReceiverを解除する */
    fun release() {
        context.unregisterReceiver(broadcastReceiver)
    }

    /** BroadcastReceiverのaddAction()に追記しないと呼ばれません！ */
    companion object {
        /** 再生 */
        val ACTION_PLAY = "io.github.takusan23.chocodroid.service.ACTION_PLAY"

        /** 一時停止 */
        val ACTION_PAUSE = "io.github.takusan23.chocodroid.service.ACTION_PAUSE"

        /** 次の曲 */
        val ACTION_SKIP_TO_NEXT = "io.github.takusan23.chocodroid.service.ACTION_SKIP_TO_NEXT"

        /** 前の曲 */
        val ACTION_SKIP_TO_PREVIOUS = "io.github.takusan23.chocodroid.service.ACTION_SKIP_TO_PREVIOUS"

        /** 終了 */
        val ACTION_STOP = "io.github.takusan23.chocodroid.service.ACTION_STOP"

        /** 単発リピート */
        val ACTION_REPEAT_ONE = "io.github.takusan23.chocodroid.service.ACTION_REPEAT_ONE"

        /** 全曲リピート */
        val ACTION_REPEAT_ALL = "io.github.takusan23.chocodroid.service.ACTION_REPEAT_ALL"
    }

}