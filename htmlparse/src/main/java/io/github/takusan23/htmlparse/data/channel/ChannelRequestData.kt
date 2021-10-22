package io.github.takusan23.htmlparse.data.channel

import kotlinx.serialization.Serializable

/**
 * チャンネルAPIを叩くときに投げるJSONのデータクラス
 *
 * @param browseId 多分チャンネルID
 * @param context Clientには最低限clientNameとclientVersionがあればいい
 * @param params [io.github.takusan23.htmlparse.api.ChannelAPI.PARAMS_UPLOAD_VIDEO]など
 * */
@Serializable
data class ChannelRequestData(
    val browseId: String,
    val context: Context,
    val params: String,
)

/**
 * 追加読み込みをするときに飛ばすJSON
 *
 * @param context Clientには最低限clientNameとclientVersionがあればいい
 * @param continuation 追加読み込みする前のレスポンスボディーの中にはいいてるtoken:""の値
 * */
@Serializable
data class MoreChannelRequestData(
    val context: Context,
    val continuation: String,
)

@Serializable
data class Context(
    val client: Client,
)

@Serializable
data class Client(
    val clientName: String = "WEB",
    val clientVersion: String = "2.20211014.01.00",
)