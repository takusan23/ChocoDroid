package io.github.takusan23.htmlparse.html

import kotlinx.coroutines.runBlocking
import org.junit.Test

class SearchAPITest {

    @Test
    fun searchText() {
        runBlocking {
            val searchAPI = SearchAPI()
            searchAPI.init()
            searchAPI.search("エロゲソングメドレー")
        }
    }

}