package io.github.takusan23.internet.api

import io.github.takusan23.internet.tool.SingletonOkHttpClientTool
import okhttp3.HttpUrl.Companion.toHttpUrl

/**
 * 検索サジェストAPIを叩く
 * */
object SearchSuggestAPI {

    /** サジェストAPI */
    private val SEARCH_SUGGEST_API_URL = "https://suggestqueries-clients6.youtube.com/complete/search"

    /**
     * 検索ワードを入れてサジェストを取得する
     *
     * @param searchWord 検索ワード
     * @return サジェスト
     * */
    suspend fun getSuggestWord(searchWord: String): List<String> {
        // 必須パラメータらしい
        val queryMap = mutableMapOf(
            "client" to "youtube",
            "ds" to "yt", // これないとPC版と違う値が帰ってくる？
            "q" to searchWord
        )
        val urlBuilder = SEARCH_SUGGEST_API_URL.toHttpUrl().newBuilder().apply {
            queryMap.forEach { (key, value) -> addQueryParameter(key, value) }
        }.build()
        // GETリクエスト
        val responseBody = SingletonOkHttpClientTool.executeGetRequest(urlBuilder.toString())
        // なんかJSなので正規表現で取り出す
        val regex = "\\[\\[.*?\\]\\]\\]".toRegex()
        val responseJsonArray = regex.find(responseBody)!!.value
        // なんか面倒なJSON配列してるので...文字列リテラルを正規表現で取り出す
        val stringRegex = "\"(.*?)\"".toRegex()
        // 正規表現の複数箇所選択なので findAll
        val stringList = stringRegex.findAll(responseJsonArray).map { it.groupValues[1] }.toList()
        return stringList
    }

}