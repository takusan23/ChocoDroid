package io.github.takusan23.internet.api

import kotlinx.coroutines.runBlocking
import org.junit.Test

class SearchSuggestAPITest {

    @Test
    fun testGetSuggestWord() {
        runBlocking {
            val suggestList = SearchSuggestAPI.getSuggestWord("暗黒放送")
            println(suggestList)
        }
    }
}