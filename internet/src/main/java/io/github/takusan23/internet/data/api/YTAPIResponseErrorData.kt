package io.github.takusan23.internet.data.api

import kotlinx.serialization.Serializable

/**
 * 検索APIで失敗したときに出るメッセージ
 * */
@Serializable
data class YTAPIResponseErrorData(
    val error: Error,
)

@Serializable
data class Error(
    val status: String,
    val code: Int,
)