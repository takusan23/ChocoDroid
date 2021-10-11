package io.github.takusan23.htmlparse.tool

import okhttp3.OkHttpClient

/** シングルトンのOkHttpクライアント */
object SingletonOkHttpClientTool {

    /** ユーザーエージェント */
    const val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.71 Safari/537.36"

    /** 使い回すOkHttpのクライアント */
    val client = OkHttpClient()

}