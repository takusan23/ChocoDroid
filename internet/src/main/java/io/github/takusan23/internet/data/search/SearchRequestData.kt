package io.github.takusan23.internet.data.search

import io.github.takusan23.internet.api.SearchAPI
import kotlinx.serialization.Serializable

/**
 * 検索APIを叩く際に使うデータクラス。JSONに変換される
 *
 * @param context [Client]には最低限しかない
 * @param query 検索ワード
 * @param params 並べ替えするなら。[PARAMS_SORT_RELEVANCE]など参照
 * */
@Serializable
data class SearchRequestData(
    val context: Context,
    val query: String,
    val params: String = SearchAPI.PARAMS_SORT_RELEVANCE
)

/**
 * 追加で検索するときに呼ぶ
 *
 * @param context [Client]には最低限しかない
 * @param continuation 一回目叩くとtokenがJSONのどっかに入っているので正規表現で取っておく。そして入れる
 * */
@Serializable
data class MoreSearchRequestData(
    val context: Context,
    val continuation: String
)

@Serializable
data class Context(
    val client: Client
)

@Serializable
data class Client(
    val clientName: String = "WEB",
    val clientVersion: String = "2.20211014.01.00"
)