package io.github.takusan23.internet.data.watchpage

import io.github.takusan23.internet.tool.SerializationTool
import kotlinx.serialization.encodeToString

/**
 * 視聴ページ取得関数から、HTML内にあるJSONと動画の配列を入れたデータクラス
 *
 * 生放送時は[contentUrlList]は一つだけです
 *
 * @param watchPageResponseJSONData 動画情報など
 * @param watchPageInitialJSONData 投稿者のアイコンURLはここにあります
 * @param contentUrlList 映像と音声のデータクラスの配列です。復号化済みであら簡単
 * */
data class WatchPageData(
    val watchPageResponseJSONData: WatchPageResponseJSONData,
    val watchPageInitialJSONData: WatchPageInitialJSONData,
    val contentUrlList: List<MediaUrlData>,
) {

    /** [watchPageInitialJSONData]を文字列にする */
    fun encodeWatchPageInitialDataToString(): String {
        return SerializationTool.jsonSerialization.encodeToString(watchPageInitialJSONData)
    }

    /** [watchPageResponseJSONData]を文字列にする */
    fun encodeWatchPageResponseDataToString(): String {
        return SerializationTool.jsonSerialization.encodeToString(watchPageResponseJSONData)
    }

    /** 生放送時はtrue */
    fun isLiveStream() = watchPageResponseJSONData.videoDetails.isLive ?: false

    /**
     * 指定した画質の[MediaUrlData]を返す
     *
     * @param quality 画質
     * */
    fun getMediaUrlDataFromQuality(quality: String = "360p") = contentUrlList.find { it.quality == quality }!!

}