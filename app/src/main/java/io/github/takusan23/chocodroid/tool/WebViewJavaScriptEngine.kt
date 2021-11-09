package io.github.takusan23.chocodroid.tool

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.WebView
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * WebViewをJavaScriptエンジンとして利用する
 *
 * なぜ必要なのかというと internet モジュール内 UnlockMagic を参照
 * */
object WebViewJavaScriptEngine {

    /**
     * WebViewをJavaScriptエンジンとして利用することで、AndroidでもJavaScriptを実行可能に
     *
     * Chromeとかの開発者ツールにある、ConsoleっていうJavaScriptできる部分と同じ動きをするはず。
     *
     * @param context Context
     * @param evalJavaScriptCode 実行するJavaScriptのコード
     * @return 実行結果
     * */
    @SuppressLint("SetJavaScriptEnabled")
    suspend fun evalJavaScriptFromWebView(context: Context, evalJavaScriptCode: String) = suspendCoroutine<String> {
        val webView = WebView(context).apply {
            settings.javaScriptEnabled = true
        }
        webView.evaluateJavascript(evalJavaScriptCode) { result ->
            it.resume(result)
        }
    }

}