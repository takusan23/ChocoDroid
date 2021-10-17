package io.github.takusan23.htmlparse.html

import io.github.takusan23.htmlparse.data.search.*
import io.github.takusan23.htmlparse.tool.SerializationTool
import io.github.takusan23.htmlparse.tool.SingletonOkHttpClientTool
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString

/**
 * 検索APIを叩く用意
 *
 * どうやら検索URLで使うパラメーターを得るために一回HTMLを取得する必要がある模様。
 *
 * - 検索のURLを完成させるためにトップページ（多分なんでもいい）を取得する
 * - 検索する。多分最低限clientオブジェクトにはclientNameとclientVersionを入れないといけない
 * - 検索結果のレスポンスボデーからtokenを控える。正規表現で取った
 * - 追加で検索をかけるときはさっきとったtokenをリクエストボディーにつけてPOST飛ばす
 * - 追加検索時は若干レスポンスボデーが違う
 * - さらいに追加で検索かけるときは追加検索時のレスポンスボデーのtokenをリクエストボデーにつけてPOSTする
 *
 * */
class SearchAPI {

    /** ようつべトップURL */
    private val TOP_PAGE_URL = "https://www.youtube.com/"

    /** 検索URL */
    private var SEARCH_API_URL: String? = null

    /** 次検索用 */
    private var NEXT_PAGE_CONTINUATION: String? = null

    /** 次検索するときに使うtokenを取得する正規表現 */
    private val tokenGetRegex = "\"token\": \"(.*?)\"".toRegex()

    companion object {
        /** 並べ替え。関連度順 */
        const val PARAMS_SORT_RELEVANCE = "CAASAhAB"

        /** 並べ替え。アップロード日時順 */
        const val PARAMS_SORT_UPLOAD_DATE = "CAI%253D"

        /** 並べ替え。視聴回数順 */
        const val PARAMS_SORT_WATCH_COUNT = "CAMSAhAB"

        /** 並べ替え。評価順 */
        const val PARAMS_SORT_REVIEW = "CAESAhAB"
    }

    /**
     * 検索機能を使う場合はまず最初にこの関数を呼んでください。検索APIのURLを作成します
     * */
    suspend fun init() {
        // とりあえずトップページリクエスト
        val responseBody = SingletonOkHttpClientTool.executeGetRequest(TOP_PAGE_URL)
        // 正規表現で検索APIのURLのパラメーターを取得
        val paramGetRegex = "\"INNERTUBE_API_KEY\":\"(.*?)\"".toRegex()
        val parameterValue = paramGetRegex.find(responseBody)!!.groupValues[1]
        // URLを完成させる
        SEARCH_API_URL = "https://www.youtube.com/youtubei/v1/search?key=$parameterValue"
    }

    /**
     * 検索APIを叩く。
     *
     * [init]を最初に呼ばないとぬるぽを吐きます。それ以外に通信に失敗すればIOException/HttpStatusCodeExceptionを吐きます。
     *
     * @param searchWord 検索ワード
     * @param sort 並び替え。[SearchRequestData.PARAMS_SORT_RELEVANCE]などを参照してください
     * @return 検索結果
     * */
    suspend fun search(searchWord: String, sort: String = PARAMS_SORT_RELEVANCE): List<VideoContent>? {
        // リクエストボディー作成
        val requestData = SearchRequestData(context = Context(Client()), query = searchWord, params = sort)
        val requestBody = SerializationTool.jsonSerialization.encodeToString(requestData)
        val postResponseBody = SingletonOkHttpClientTool.executePostRequest(SEARCH_API_URL!!, requestBody)
        val searchResponseData = SerializationTool.jsonSerialization.decodeFromString<SearchResponseData>(postResponseBody)
        // 次回検索用
        NEXT_PAGE_CONTINUATION = tokenGetRegex.find(postResponseBody)!!.groupValues[1]
        return searchResponseData.contents.twoColumnSearchResultsRenderer.primaryContents.sectionListRenderer.contents[0].itemSectionRenderer?.contents
    }

    /**
     * 追加で検索結果を取得するときに読んでください。
     *
     * [init]となんか検索[search]を呼んだあとに使ってください。
     *
     * それ以外に通信に失敗すればIOException/HttpStatusCodeExceptionを吐きます。
     *
     * @return 検索結果。なんか前回検索した内容が入ってるっぽい？
     * */
    suspend fun moreSearch(): List<VideoContent>? {
        // リクエストボディー作成
        val requestData = MoreSearchRequestData(context = Context(Client()), continuation = NEXT_PAGE_CONTINUATION!!)
        val requestBody = SerializationTool.jsonSerialization.encodeToString(requestData)
        val postResponseBody = SingletonOkHttpClientTool.executePostRequest(SEARCH_API_URL!!, requestBody)
        // 追加検索では検索結果JSONが若干違う
        val moreSearchResponseData = SerializationTool.jsonSerialization.decodeFromString<MoreSearchResponseData>(postResponseBody)
        // 次回検索用
        NEXT_PAGE_CONTINUATION = tokenGetRegex.find(postResponseBody)!!.groupValues[1]
        return moreSearchResponseData.onResponseReceivedCommands[0].appendContinuationItemsAction.continuationItems[0].itemSectionRenderer?.contents
    }

}