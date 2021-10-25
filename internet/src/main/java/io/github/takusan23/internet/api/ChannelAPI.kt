package io.github.takusan23.internet.api

import io.github.takusan23.internet.data.CommonVideoData
import io.github.takusan23.internet.data.channel.*
import io.github.takusan23.internet.tool.SerializationTool
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString

class ChannelAPI {

    /** API叩くやつ */
    private val ytAPICall = YTAPICall()

    /** APIキーをFlowで流す */
    val apiKeyFlow = ytAPICall.apiKeyFlow

    /** 検索URL */
    private val CHANNEL_API_BASE_URL = "https://www.youtube.com/youtubei/v1/browse"

    /** 追加読み込みをする際に使う値を取得する正規表現 */
    private val tokenGetRegex = "\"token\": \"(.*?)\"".toRegex()

    /** 次検索用 */
    private var NEXT_PAGE_CONTINUATION: String? = null

    companion object {

        /** 投稿動画 */
        const val PARAMS_UPLOAD_VIDEO = "EgZ2aWRlb3M="

    }

    /**
     * まず最初にこの関数を呼んでください。チャンネル投稿動画を取得するURLを作成します。
     *
     * @param apiKey 検索API等でAPIキーが既にわかっている場合は入れてください。nullにすると取得しに行きます。
     * */
    suspend fun init(apiKey: String? = null) {
        ytAPICall.initAPIKey(apiKey)
    }

    /**
     * チャンネル投稿動画を取得する
     *
     * 実は投稿動画取得には２ルートあって、HTMLの中にあるJSONから取る方法と、APIを叩いて取る方法がある
     *
     * 今回は後者で。どうせ追加読込する際にはAPIキーが必要なので、、、
     * */
    suspend fun getChannelUploadVideo(channelId: String): ChannelResponseData {
        // POSTする中身
        val postData = ChannelRequestData(browseId = channelId, context = Context(Client()), params = PARAMS_UPLOAD_VIDEO)
        val requestBody = SerializationTool.jsonSerialization.encodeToString(postData)
        // リクエスト
        val responseBody = ytAPICall.executeYTAPIPostRequest(CHANNEL_API_BASE_URL, requestBody)
        val channelResponseData = SerializationTool.jsonSerialization.decodeFromString<ChannelResponseData>(responseBody)
        // 追加読み込み用
        NEXT_PAGE_CONTINUATION = tokenGetRegex.find(responseBody)?.groupValues?.get(1)
        return channelResponseData
    }

    /**
     * 追加読み込みをする。一回[getChannelUploadVideo]を呼んでおく必要があります。
     *
     * これ以上呼べない場合は空配列を返します。
     *
     * @return 動画配列
     * */
    suspend fun moreChannelUploadVideo(): List<CommonVideoData> {
        if (NEXT_PAGE_CONTINUATION == null) return emptyList()
        // POSTする中身
        val postData = MoreChannelRequestData(context = Context(Client()), continuation = NEXT_PAGE_CONTINUATION!!)
        val requestBody = SerializationTool.jsonSerialization.encodeToString(postData)
        // リクエスト
        val responseBody = ytAPICall.executeYTAPIPostRequest(CHANNEL_API_BASE_URL, requestBody)
        val moreChannelResponseData = SerializationTool.jsonSerialization.decodeFromString<MoreChannelResponseData>(responseBody)
        // 追加読み込み用。nullになったということはこれ以上ないってこと
        NEXT_PAGE_CONTINUATION = tokenGetRegex.find(responseBody)?.groupValues?.get(1)
        return moreChannelResponseData.onResponseReceivedActions[0].appendContinuationItemsAction.continuationItems.mapNotNull { it.gridVideoRenderer }.map { CommonVideoData(it) }
    }

}