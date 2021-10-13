package io.github.takusan23.htmlparse.html

import io.github.takusan23.htmlparse.magic.DecryptMagic
import kotlinx.coroutines.runBlocking

import org.junit.Test
import java.net.URLDecoder

/**
 * 人生初のテストコード。開発機のJavaVMで関数が動かせるので便利だね。もっと早くからやってればよかった
 * */
class WatchPageHTMLTest {

    @Test
    fun getWatchPage() {
        runBlocking {
            val result = WatchPageHTML.getWatchPage("https://www.youtube.com/watch?v=NyUTYwZe_l4")
            println(result.streamingData.formats.last().url)
            if (result.streamingData.formats[0].signatureCipher != null) {
                println(result.streamingData.formats[0].decryptionMagic())
            }
        }
    }
}