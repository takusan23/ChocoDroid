package io.github.takusan23.htmlparse.html

import io.github.takusan23.htmlparse.tool.SingletonOkHttpClientTool

/**
 * APIキーを取得する関数がある。
 *
 * このAPIキーを利用して、動画検索やチャンネル投稿動画を取得する
 * */
object APIKeyHTML {

    /** ようつべトップURL */
    private val TOP_PAGE_URL = "https://www.youtube.com/"

    /**
     * HTML内に埋め込まれているAPIキーを取得する
     *
     * @return APIキー
     * */
    suspend fun getAPIKey(): String {
        // とりあえずトップページリクエスト
        val responseBody = SingletonOkHttpClientTool.executeGetRequest(TOP_PAGE_URL)
        // 正規表現で検索APIのURLのパラメーターを取得
        val getApiKeyRegex = "\"INNERTUBE_API_KEY\":\"(.*?)\"".toRegex()
        val apiKeyValue = getApiKeyRegex.find(responseBody)!!.groupValues[1]
        return apiKeyValue
    }

}