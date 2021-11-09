package io.github.takusan23.internet.magic

import io.github.takusan23.internet.tool.SingletonOkHttpClientTool
import kotlinx.coroutines.runBlocking
import org.junit.Test

class DecryptMagicTest {

    @Test
    fun decryptTest() {

    }

    @Test
    fun findParamFixJSCode() {
        val baseJSUrl = ""
        runBlocking {
            val fixCode = UnlockMagic.getParamFixJavaScriptCode(SingletonOkHttpClientTool.executeGetRequest(baseJSUrl))
            println(fixCode)
        }
    }

}