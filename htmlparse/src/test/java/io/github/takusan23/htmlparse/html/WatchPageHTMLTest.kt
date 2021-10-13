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
            val watchPageData = WatchPageHTML.getWatchPage("", null, null, null)
            println(watchPageData)
            val signatureCipher = watchPageData.watchPageJSONResponseData.streamingData.formats.last().signatureCipher
            if (signatureCipher != null) {
                println("復号アルゴリズム---")
                println(watchPageData.decryptURL(signatureCipher))
            } else {
                println(watchPageData.watchPageJSONResponseData.streamingData.formats.last().url)
            }
            println("アルゴリズム---")
            println(watchPageData.algorithmFuncNameData)
            println(watchPageData.decryptInvokeList)
        }
    }
}