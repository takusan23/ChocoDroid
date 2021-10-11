package io.github.takusan23.htmlparse.exception

/**
 * 200以外のときに吐く例外。
 * @param message ステータスコード
 * */
class HttpStatusCodeException(statusCode: Int, message: String) : Exception("$statusCode : $message")