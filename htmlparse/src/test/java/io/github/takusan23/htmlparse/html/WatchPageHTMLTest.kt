package io.github.takusan23.htmlparse.html

import kotlinx.coroutines.runBlocking
import org.junit.Test

/**
 * 人生初のテストコード。開発機のJavaVMで関数が動かせるので便利だね。もっと早くからやってればよかった
 * */
class WatchPageHTMLTest {

    @Test
    fun getWatchPage() {
        runBlocking {
            val watchPageData = WatchPageHTML.getWatchPage("https://www.youtube.com/watch?v=Ng_fZau-olQ", null, null, null)

            if (watchPageData.isLiveStream()) {
                println("生放送 HLS アドレス")
                println(watchPageData.watchPageJSONResponseData.streamingData.hlsManifestUrl!!)
            } else {
                if (watchPageData.isSignatureUrl()) {
                    println("復号URL---")
                    println(watchPageData.decryptURL(watchPageData.watchPageJSONResponseData.streamingData.formats?.last()?.signatureCipher!!))
                    println("アダプティブ---")
                    watchPageData.watchPageJSONResponseData.streamingData.adaptiveFormats.forEach {
                        println(it.mimeType)
                        println(it.qualityLabel ?: "audio")
                        println(watchPageData.decryptURL(it.signatureCipher!!))
                    }
                } else {
                    println("復号が必要ない")
                    println(watchPageData.watchPageJSONResponseData.streamingData.formats?.last()?.url!!)
                    println("アダプティブ---")
                    watchPageData.watchPageJSONResponseData.streamingData.adaptiveFormats.forEach {
                        println(it.mimeType)
                        println(it.qualityLabel ?: "audio")
                        println(it.url)
                    }
                }
            }

            println("投稿者。の画像URL")
            watchPageData.watchPageJSONInitialData.contents.twoColumnWatchNextResults.results.results.contents[1].videoSecondaryInfoRenderer?.owner?.videoOwnerRenderer?.thumbnail?.thumbnails?.forEach {
                println(it.url)
            }

            println("関連動画")
            watchPageData.watchPageJSONInitialData.contents.twoColumnWatchNextResults.secondaryResults.secondaryResults.results.mapNotNull { it.compactVideoRenderer }.forEach {
                println(it)
            }


            println("アルゴリズム---")
            println(watchPageData.algorithmFuncNameData)
            println(watchPageData.decryptInvokeList)
        }
    }
}