package io.github.takusan23.chocodroid.tool

import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.*

/** 時間操作まとめ */
object TimeFormatTool {

    /**
     * 変換する関数
     * @param position 時間（秒）
     * @return HH:mm:ss。0以下の場合は空文字
     * */
    fun videoDurationToFormatText(position: Long): String {
        return if (position < 0) {
            ""
        } else {
            DateUtils.formatElapsedTime(position)
        }.toString()
    }

    /**
     * UnixTimeを時刻文字列に変換する
     *
     * @param unixTime UnixTime。ミリ秒
     * @return yyyy/MM/dd HH:mm:ss
     * */
    fun unixTimeToFormatText(unixTime: Long): String {
        return SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(unixTime)
    }

}