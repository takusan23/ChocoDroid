package io.github.takusan23.downloadpocket

import java.lang.Exception

/**
 * ダウンロードに失敗したらスローする
 *
 * HTTPのステータスコードが200以外のときスローしてる
 * */
class DownloadErrorException(override val message: String) : Exception(message)