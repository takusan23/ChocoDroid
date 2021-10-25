package io.github.takusan23.internet.html

import io.github.takusan23.internet.data.watchpage.*
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
     * @return Pairを返します。視聴ページデータと復号で使うデータです
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

        // パーサーにかける
        parseWatchPage(responseBody, baseJSURL, algorithmFuncNameData, algorithmInvokeList)
    }

    /**
     * レスポンスボディー(HTML)からデータクラスを作成する
     *
     * @param responseBody 視聴ページHTML
     * @param baseJSURL base.jsのURLです。この値が変わると復号化アルゴリズムが変化したとみなします。初回時はnullでおｋ。
     * @param algorithmFuncNameData base.js内にある復号に使う文字列操作関数名を入れたデータクラス。初回時は以下略
     * @param algorithmInvokeList [algorithmFuncNameData]を呼ぶ順番をいれたデータクラス。初回時は以下略
     * @return Pairを返します。視聴ページデータと復号で使うデータです
     * */
    private suspend fun parseWatchPage(
        responseBody: String,
        baseJSURL: String? = null,
        algorithmFuncNameData: AlgorithmFuncNameData? = null,
        algorithmInvokeList: List<AlgorithmInvokeData>? = null,
    ) = withContext(Dispatchers.Default) {
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
            val decryptData = DecryptData(baseJSURL, algorithmFuncNameData, algorithmInvokeList)
            val watchPageData = WatchPageData(watchPageJSONResponseData, watchPageJSONInitialData, getContentUrlList(watchPageJSONResponseData, decryptData))
            // Pairを返す
            watchPageData to decryptData
        } else {
            // アルゴリズムが変化しました。復号システムを再構築します
            val baseJSCode = SingletonOkHttpClientTool.executeGetRequest(currentBaseJsUrl)
            val funcNameData = AlgorithmParser.funcNameParser(baseJSCode)
            val invokeList = AlgorithmParser.algorithmFuncParser(baseJSCode)
            val decryptData = DecryptData(currentBaseJsUrl, funcNameData, invokeList)
            val watchPageData = WatchPageData(watchPageJSONResponseData, watchPageJSONInitialData, getContentUrlList(watchPageJSONResponseData, decryptData))
            // Pairを返す
            watchPageData to decryptData
        }
    }

    /**
     * 音声と映像のデータクラスを入れた配列を返す
     *
     * @param watchPageJSONResponseData 視聴ページ内のJSON
     * @param decryptData 復号で使うデータ
     * @return [MediaUrlData]を入れた配列です。
     * */
    private fun getContentUrlList(watchPageJSONResponseData: WatchPageResponseJSONData, decryptData: DecryptData): List<MediaUrlData> {
        // 生放送時
        return if (watchPageJSONResponseData.videoDetails.isLive == true) {
            listOf(MediaUrlData(mixTrackUrl = watchPageJSONResponseData.streamingData.hlsManifestUrl!!))
        } else {
            // 音声ファイル選ぶ
            val audioTrack = watchPageJSONResponseData.streamingData.adaptiveFormats.find { it.mimeType.contains("audio") }!!
            val audioTrackUrl = if (audioTrack.signatureCipher != null) decryptData.decryptURL(audioTrack.signatureCipher) else audioTrack.url
            // 音声、映像データクラスの配列
            watchPageJSONResponseData.streamingData.adaptiveFormats
                .filter { it.mimeType.contains("avc1") } // 映像のみ
                .map { adaptiveFormat ->
                    // 復号化が必要な場合は復号する
                    if (adaptiveFormat.signatureCipher != null) {
                        val url = decryptData.decryptURL(adaptiveFormat.signatureCipher)
                        MediaUrlData(url, audioTrackUrl, adaptiveFormat.qualityLabel, null)
                    } else {
                        MediaUrlData(adaptiveFormat.url, audioTrackUrl, adaptiveFormat.qualityLabel, null)
                    }
                }
        }
    }

}