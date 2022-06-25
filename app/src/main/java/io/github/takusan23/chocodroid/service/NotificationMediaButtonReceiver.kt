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
            when (intent?.action) {
                PlayerControlType.ACTION_PLAY.action -> transportControls.play()
                PlayerControlType.ACTION_PAUSE.action -> transportControls.pause()
                PlayerControlType.ACTION_SKIP_TO_NEXT.action -> transportControls.skipToNext()
                PlayerControlType.ACTION_SKIP_TO_PREVIOUS.action -> transportControls.skipToPrevious()
                PlayerControlType.ACTION_STOP.action -> transportControls.stop()
                PlayerControlType.ACTION_REPEAT_ONE.action -> transportControls.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ONE)
                PlayerControlType.ACTION_REPEAT_ALL.action -> transportControls.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ALL)
            }
        }
    }

    init {
        // Broadcast登録
        context.registerReceiver(broadcastReceiver, IntentFilter().apply {
            addAction(PlayerControlType.ACTION_PLAY.action)
            addAction(PlayerControlType.ACTION_PAUSE.action)
            addAction(PlayerControlType.ACTION_SKIP_TO_NEXT.action)
            addAction(PlayerControlType.ACTION_SKIP_TO_PREVIOUS.action)
            addAction(PlayerControlType.ACTION_STOP.action)
            addAction(PlayerControlType.ACTION_REPEAT_ONE.action)
            addAction(PlayerControlType.ACTION_REPEAT_ALL.action)
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
    )!!

    /** BroadcastReceiverを解除する */
    fun release() {
        context.unregisterReceiver(broadcastReceiver)
    }

    /** BroadcastReceiverのaddAction()に追記しないと呼ばれません！ */
    enum class PlayerControlType(val action: String) {
        /** 再生 */
        ACTION_PLAY("io.github.takusan23.chocodroid.service.ACTION_PLAY"),

        /** 一時停止 */
        ACTION_PAUSE("io.github.takusan23.chocodroid.service.ACTION_PAUSE"),

        /** 次の曲 */
        ACTION_SKIP_TO_NEXT("io.github.takusan23.chocodroid.service.ACTION_SKIP_TO_NEXT"),

        /** 前の曲 */
        ACTION_SKIP_TO_PREVIOUS("io.github.takusan23.chocodroid.service.ACTION_SKIP_TO_PREVIOUS"),

        /** 終了 */
        ACTION_STOP("io.github.takusan23.chocodroid.service.ACTION_STOP"),

        /** 単発リピート */
        ACTION_REPEAT_ONE("io.github.takusan23.chocodroid.service.ACTION_REPEAT_ONE"),

        /** 全曲リピート */
        ACTION_REPEAT_ALL("io.github.takusan23.chocodroid.service.ACTION_REPEAT_ALL"),
    }

}