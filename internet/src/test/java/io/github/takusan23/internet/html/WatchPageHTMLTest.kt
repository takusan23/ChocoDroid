package io.github.takusan23.internet.html

import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.junit.Test

/**
 * 人生初のテストコード。開発機のJavaVMで関数が動かせるので便利だね。もっと早くからやってればよかった
 * */
class WatchPageHTMLTest {

    @Test
    fun getWatchPage() {
        runBlocking {
            val (watchPageData, decryptData) = WatchPageHTML.getWatchPage("", null, null, null)

            if (watchPageData.isLiveStream()) {
                println("生放送 HLS アドレス")
                println(watchPageData.contentUrlList.first().mixTrackUrl)
            } else {
                println("MPEG-DASH アドレス")
                println(watchPageData.watchPageResponseJSONData.streamingData.dashManifestUrl)
                println("動画URL")
                watchPageData.contentUrlList.forEach { mediaUrlData ->
                    println(mediaUrlData.quality)
                    println(mediaUrlData.videoTrackUrl)
                    println(mediaUrlData.audioTrackUrl)
                    println("----")
                }
            }

            watchPageData.watchPageResponseJSONData.streamingData.adaptiveFormats.forEach {
                println(it.qualityLabel)
                println(it.url)
                println("---")
            }

            println("投稿者。の画像URL")
            watchPageData.watchPageInitialJSONData.contents.twoColumnWatchNextResults.results.results.contents[1].videoSecondaryInfoRenderer?.owner?.videoOwnerRenderer?.thumbnail?.thumbnails?.forEach {
                println(it.url)
            }

            println("関連動画")
            watchPageData.watchPageInitialJSONData.contents.twoColumnWatchNextResults.secondaryResults.secondaryResults.results.mapNotNull { it.compactVideoRenderer }.forEach {
                println(it)
            }

            println("アルゴリズム---")
            println(decryptData.baseJsURL)
            println(decryptData.algorithmFuncNameData)
            println(decryptData.decryptInvokeList)

            println("nパラメーター")
            println(watchPageData.getMediaUrlDataFromQuality().videoTrackUrl?.toHttpUrl()?.queryParameter("n"))
            println("パラメーター変更JavaScriptコード")
            println(decryptData.urlParamFixJSCode)
        }
    }

}