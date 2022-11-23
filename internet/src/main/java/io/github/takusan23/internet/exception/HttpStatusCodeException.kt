package io.github.takusan23.internet.exception

/**
 * 200以外のときに吐く例外。
 *
 * @param statusCode ステータスコード
 * @param url URL
 * @param body レスポンスボデー
 */
class HttpStatusCodeException(val statusCode: Int, val url: String, val body: String) : Exception("$statusCode : $url = $body")