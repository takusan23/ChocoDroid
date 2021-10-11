package io.github.takusan23.htmlparse.html

import io.github.takusan23.htmlparse.data.WatchPageResponseData
import io.github.takusan23.htmlparse.exception.HttpStatusCodeException
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
     * @param videoId 動画ID
     * @return 視聴ページデータ
     * */
    suspend fun getWatchPage(videoId: String = "0976Z1s0V1A") = withContext(Dispatchers.IO) {
        val request = Request.Builder().apply {
            url("https://www.youtube.com/watch?v=$videoId")
            addHeader("User-Agent", SingletonOkHttpClientTool.USER_AGENT)
        }.build()
        val response = SingletonOkHttpClientTool.client.newCall(request).execute()
        if (response.isSuccessful) {
            // 成功時。HTMLにあるJSONを取り出す
            val elementText = Jsoup.parse(response.body!!.string())
                .getElementsByTag("script")
                .find { element -> element.html().contains("ytInitialPlayerResponse") }!!
                .html()
            // なんか後ろにJavaScript付いてるのでオブジェクトだけ取るためのJS
            val regex = Regex("\\{.*?\\};")
            val jsonText = regex.find(elementText)!!.value.removeSuffix(";")
            SerializationTool.Serialization.decodeFromString<WatchPageResponseData>(jsonText)
        } else {
            // 失敗時
            throw HttpStatusCodeException(response.code, response.message)
        }
    }

}