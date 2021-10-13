package io.github.takusan23.htmlparse.magic

import org.junit.Test

/**
 * テスト実行時に Tag mismatch ! が出たらインターネット環境を疑ってください。多分もう一回実行すれば
 * */
class AlgorithmParserTest {


    /** 正規表現で使ってる文字をエスケープする */
    @Test
    fun escapeTest() {
        val escapedText = AlgorithmParser.replaceRegexEscapeChar("""""")
        println(escapedText)
    }

}