package io.github.takusan23.htmlparse.html

import io.github.takusan23.htmlparse.html.data.WatchPageJSONResponseData
import io.github.takusan23.htmlparse.exception.HttpStatusCodeException
import io.github.takusan23.htmlparse.html.data.WatchPageData
import io.github.takusan23.htmlparse.magic.AlgorithmParser
import io.github.takusan23.htmlparse.magic.data.AlgorithmFuncNameData
import io.github.takusan23.htmlparse.magic.data.AlgorithmInvokeData
import io.github.takusan23.htmlparse.tool.SerializationTool
import io.github.takusan23.htmlparse.tool.SingletonOkHttpClientTool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import okhttp3.Request
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
        val request = Request.Builder().apply {
            if (videoIdOrHttpUrl.startsWith("https://")) {
                url(videoIdOrHttpUrl)
            } else {
                url("https://www.youtube.com/watch?v=$videoIdOrHttpUrl")
            }
            // addHeader("User-Agent", SingletonOkHttpClientTool.USER_AGENT)
        }.build()
        val response = SingletonOkHttpClientTool.client.newCall(request).execute()
        if (response.isSuccessful) {
            // 成功時。HTMLにあるJSONを取り出す
            val document = Jsoup.parse(response.body!!.string())
            val elementText = document
                .getElementsByTag("script")
                .find { element -> element.html().contains("ytInitialPlayerResponse") }!!
                .html()
            // なんか後ろにJavaScript付いてるのでオブジェクトだけ取るためのJS
            val regex = Regex("\\{.*?\\};")
            val jsonText = regex.find(elementText)!!.value.removeSuffix(";")
            val watchPageJSONResponseData = SerializationTool.Serialization.decodeFromString<WatchPageJSONResponseData>(jsonText)

            // base.jsのURLを取得
            val currentBaseJsUrl = "https://www.youtube.com" + document
                .getElementsByTag("script")
                .find { element -> element.attr("src").contains("base.js") }!!
                .attr("src")

            // 比較して、復号アルゴリズムが変化しているか確認する
            if (baseJSURL == currentBaseJsUrl && algorithmFuncNameData != null && algorithmInvokeList != null) {
                // アルゴリズムに変化がないので、引数に入れた復号システムが使えます
                WatchPageData(watchPageJSONResponseData, currentBaseJsUrl, algorithmFuncNameData, algorithmInvokeList)
            } else {
                // アルゴリズムが変化しました。復号システムを再構築します
                val baseJSCode = getBaseJSCode(currentBaseJsUrl)
                val funcNameData = AlgorithmParser.funcNameParser(baseJSCode)
                val invokeList = AlgorithmParser.algorithmFuncParser(baseJSCode)
                // 再構築したデータで返す
                WatchPageData(watchPageJSONResponseData, currentBaseJsUrl, funcNameData, invokeList)
            }
        } else {
            // 失敗時
            throw HttpStatusCodeException(response.code, response.message)
        }
    }


    /**
     * base.jsの中身を取得する
     *
     * 失敗したら例外をスローします
     *
     * @param baseJSURL base.jsのURL
     * @return base.jsの中身
     * */
    private suspend fun getBaseJSCode(baseJSURL: String) = withContext(Dispatchers.Default) {
        // リクエスト飛ばす
        val request = Request.Builder().apply {
            url(baseJSURL)
            addHeader("User-Agent", SingletonOkHttpClientTool.USER_AGENT)
        }.build()
        val response = SingletonOkHttpClientTool.client.newCall(request).execute()
        if (response.isSuccessful) {
            return@withContext response.body!!.string()
        } else {
            throw HttpStatusCodeException(response.code, response.message)
        }
    }

}