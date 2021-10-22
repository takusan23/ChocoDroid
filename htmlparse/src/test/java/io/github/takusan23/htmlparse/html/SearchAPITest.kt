package io.github.takusan23.htmlparse.html

import io.github.takusan23.htmlparse.api.SearchAPI
import kotlinx.coroutines.runBlocking
import org.junit.Test

class SearchAPITest {

    @Test
    fun searchText() {
        runBlocking {
            val searchAPI = SearchAPI()
            searchAPI.init()
            val searchResponseData = searchAPI.search("さくらんぼキッス", SearchAPI.PARAMS_SORT_UPLOAD_DATE)
            println("URL")
            println(searchResponseData.apiKey)
            searchResponseData.videoContentList?.forEach {
                println(it)
                println("---")
            }
        }
    }

}