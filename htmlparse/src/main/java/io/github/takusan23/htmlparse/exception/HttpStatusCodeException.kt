package io.github.takusan23.htmlparse.exception

/**
 * 200以外のときに吐く例外。
 * @param statusCode ステータスコード
 * @param body レスポンスボデー
 * */
class HttpStatusCodeException(val statusCode: Int, val body: String) : Exception("$statusCode : $body")