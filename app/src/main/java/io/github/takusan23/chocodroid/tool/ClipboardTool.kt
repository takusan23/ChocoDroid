package io.github.takusan23.chocodroid.tool

import android.content.ClipboardManager
import android.content.Context

/** クリップボードアクセス */
object ClipboardTool {

    /**
     * クリップボードからテキストを取得する
     *
     * @param context Context
     * @return コピーした内容
     * */
    fun getClipboardText(context: Context): String? {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        return clipboardManager.primaryClip?.getItemAt(0)?.text?.toString()
    }

}