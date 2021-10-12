package io.github.takusan23.chocodroid.tool

import android.text.format.DateUtils

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

}