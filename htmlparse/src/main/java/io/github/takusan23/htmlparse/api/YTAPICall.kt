package io.github.takusan23.htmlparse.api

import io.github.takusan23.htmlparse.data.search.YTAPIResponseErrorData
import io.github.takusan23.htmlparse.exception.HttpStatusCodeException
import io.github.takusan23.htmlparse.html.APIKeyHTML
import io.github.takusan23.htmlparse.tool.SerializationTool
import io.github.takusan23.htmlparse.tool.SingletonOkHttpClientTool
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.decodeFromString

/**
 * APIキー変更等に対応するためHTTPクライアントに少し手を加える
 *
 * ところでAPIキー変わんの？
 *
 * APIを叩く関数を呼ぶ前に、[initAPIKey]でAPIキーをセットしてください。
 * */
class YTAPICall {

    private val _APIKeyFlow = MutableStateFlow<String?>(null)

    /** APIキーを流すFlow。 */
    val apiKeyFlow = _APIKeyFlow as StateFlow<String?>

    /**
     * APIキーの準備をします。既にAPIキーを取得している場合は引数に入れてください。
     *
     * @param apiKey すでにある場合は指定してください。ない場合は省略して
     * */
    suspend fun initAPIKey(apiKey: String? = null) {
        _APIKeyFlow.value = apiKey ?: APIKeyHTML.getAPIKey()
    }

    /**
     * APIキー変更により失敗した場合はAPIキーを取得し直してAPIコールをリベンジする関数
     *
     * 通信に失敗した際や、APIキー変更以外のエラーの場合は例外を吐きます。
     *
     * APIキーが変更になった場合は、[apiKeyFlow]で値を流してますのでそこから拾ってください。
     *
     * @param apiBaseUrl APIキーを抜いたURL
     * @param requestBody リクエストボデー
     * @return レスポンスボディー。各クラスでAPIキーを保持していると思うので更新してください。
     * */
    suspend fun executeYTAPIPostRequest(apiBaseUrl: String, requestBody: String): String {
        return try {
            SingletonOkHttpClientTool.executePostRequest("$apiBaseUrl?key=${_APIKeyFlow.value}", requestBody)
        } catch (e: HttpStatusCodeException) {
            val errorJSONData = SerializationTool.jsonSerialization.decodeFromString<YTAPIResponseErrorData>(e.body)
            if (errorJSONData.error.status == "INVALID_ARGUMENT") {
                // 多分APIキー変更なので取り直す
                val newAPIKey = APIKeyHTML.getAPIKey()
                // 再リクエスト
                SingletonOkHttpClientTool.executePostRequest("$apiBaseUrl?key=$newAPIKey", requestBody)
            } else {
                // それ以外は知らんからスロー
                throw e
            }
        }
    }

}