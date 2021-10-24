package io.github.takusan23.internet.html

import io.github.takusan23.internet.data.watchpage.WatchPageResponseJSONData
import io.github.takusan23.internet.data.watchpage.WatchPageData
import io.github.takusan23.internet.data.watchpage.WatchPageInitialJSONData
import io.github.takusan23.internet.magic.AlgorithmParser
import io.github.takusan23.internet.magic.data.AlgorithmFuncNameData
import io.github.takusan23.internet.magic.data.AlgorithmInvokeData
import io.github.takusan23.internet.tool.SerializationTool
import io.github.takusan23.internet.tool.SingletonOkHttpClientTool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import org.jsoup.Jsoup

object WatchPageHTML {

    /**
     * 視聴ページから情報を取得する
     *
     * 失敗時は IOException / HttpStatusCodeException をスローします
     * @param videoIdOrHttpUrl 動画IDかURL。Https://から始まっている場合はURLと認識します。
     * @param baseJSURL base.jsのURLです。この値が変わると復号化アルゴリズムが変化したとみなします。初回時はnullでおｋ。
     * @param algorithmFuncNameData base.js内にある復号に使う文字列操作関数名を入れたデータクラス。初回時は以下略
     * @param algorithmInvokeList [algorithmFuncNameData]を呼ぶ順番をいれたデータクラス。初回時は以下略
     * @return 視聴ページデータ
     * */
    suspend fun getWatchPage(
        videoIdOrHttpUrl: String = "",
        baseJSURL: String? = null,
        algorithmFuncNameData: AlgorithmFuncNameData? = null,
        algorithmInvokeList: List<AlgorithmInvokeData>? = null,
    ) = withContext(Dispatchers.IO) {

        val url = if (videoIdOrHttpUrl.startsWith("https://")) {
            videoIdOrHttpUrl
        } else {
            "https://www.youtube.com/watch?v=$videoIdOrHttpUrl"
        }
        // 失敗したらこの時点で例外
        val responseBody = SingletonOkHttpClientTool.executeGetRequest(url)

        // 成功時。HTMLにあるJSONを取り出す
        val document = Jsoup.parse(responseBody)
        val ytInitialPlayerResponse = document
            .getElementsByTag("script")
            .find { element -> element.html().contains("ytInitialPlayerResponse") }!!
            .html()
        // なんか後ろにJavaScript付いてるのでオブジェクトだけ取るためのJS
        val regex = Regex("(\\{.*?\\});")
        val ytInitialPlayerResponseJSON = regex.find(ytInitialPlayerResponse)!!.groupValues[1]
        val watchPageJSONResponseData = SerializationTool.jsonSerialization.decodeFromString<WatchPageResponseJSONData>(ytInitialPlayerResponseJSON)

        // もう一つのJSONもほしい
        val ytInitialData = document
            .getElementsByTag("script")
            .find { element -> element.html().contains("ytInitialData ") }!!
            .html()
        val ytInitialDataJSON = regex.find(ytInitialData)!!.groupValues[1]
        val watchPageJSONInitialData = SerializationTool.jsonSerialization.decodeFromString<WatchPageInitialJSONData>(ytInitialDataJSON)

        // base.jsのURLを取得
        val currentBaseJsUrl = "https://www.youtube.com" + document
            .getElementsByTag("script")
            .find { element -> element.attr("src").contains("base.js") }!!
            .attr("src")

        // 比較して、復号アルゴリズムが変化しているか確認する
        if (baseJSURL == currentBaseJsUrl && algorithmFuncNameData != null && algorithmInvokeList != null) {
            // アルゴリズムに変化がないので、引数に入れた復号システムが使えます
            WatchPageData(watchPageJSONResponseData, watchPageJSONInitialData, currentBaseJsUrl, algorithmFuncNameData, algorithmInvokeList)
        } else {
            // アルゴリズムが変化しました。復号システムを再構築します
            val baseJSCode = SingletonOkHttpClientTool.executeGetRequest(currentBaseJsUrl)
            val funcNameData = AlgorithmParser.funcNameParser(baseJSCode)
            val invokeList = AlgorithmParser.algorithmFuncParser(baseJSCode)
            // 再構築したデータで返す
            WatchPageData(watchPageJSONResponseData, watchPageJSONInitialData, currentBaseJsUrl, funcNameData, invokeList)
        }
    }

}