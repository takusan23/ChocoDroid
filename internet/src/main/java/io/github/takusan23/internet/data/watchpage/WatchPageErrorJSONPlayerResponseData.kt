package io.github.takusan23.internet.data.watchpage

import kotlinx.serialization.Serializable

/**
 * HTML内にあるJSON。動画情報とかURLとか
 *
 * @param playabilityStatus 再生できない理由
 * */
@Serializable
data class WatchPageErrorResponseJSONData(
    val videoDetails: VideoDetails,
    val microformat: Microformat,
    val playabilityStatus: PlayabilityStatus,
)

/**
 * 視聴ができない場合はここの情報を見る
 *
 * @param reason 視聴不可のメッセージ本文。「この動画は、一部のユーザーに適さない可能性があります。」など
 * @param status 情報
 * */
@Serializable
data class PlayabilityStatus(
    val reason: String? = null,
    val status: String,
)

/**
 * 失敗時に投げる例外
 *
 * @param errorResponseJSONData エラー時のJSON
 * */
class WatchPageErrorException(val errorResponseJSONData: WatchPageErrorResponseJSONData) : Exception(errorResponseJSONData.playabilityStatus.reason)