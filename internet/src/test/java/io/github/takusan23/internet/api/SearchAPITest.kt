package io.github.takusan23.internet.api

import kotlinx.coroutines.runBlocking
import org.junit.Test

class SearchAPITest {

    @Test
    fun searchText() {
        runBlocking {
            val searchAPI = SearchAPI()
            searchAPI.init()
            val searchResponseData = searchAPI.search("さくらんぼキッス", SearchAPI.PARAMS_SORT_UPLOAD_DATE)
            searchResponseData?.forEach {
                println(it)
                println("---")
            }
        }
    }

}