package io.github.takusan23.internet.data.watchpage

import io.github.takusan23.internet.tool.SerializationTool
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString

/**
 * 視聴ページ取得関数から、HTML内にあるJSONと動画の配列を入れたデータクラス
 *
 * 生放送時は[contentUrlList]は一つだけです
 *
 * @param watchPageResponseJSONData 動画情報など
 * @param watchPageInitialJSONData 投稿者のアイコンURLはここにあります
 * @param contentUrlList 映像と音声のデータクラスの配列です。復号化済みであら簡単
 * @param type videoかlive。あとダウンロード済みの場合は"download"とか？。
 * */
data class WatchPageData(
    val watchPageResponseJSONData: WatchPageResponseJSONData,
    val watchPageInitialJSONData: WatchPageInitialJSONData,
    val contentUrlList: List<MediaUrlData>,
    val type: String = if (watchPageResponseJSONData.videoDetails.isLive == true) "live" else "video",
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
     * ない場合は今の所最高画質を返してる
     *
     * @param quality 画質
     * */
    fun getMediaUrlDataFromQuality(quality: String? = "360p") = contentUrlList.find { it.quality == quality } ?: contentUrlList.first()

    companion object {

        /** [WatchPageData.type]が動画 */
        const val WATCH_PAGE_DATA_TYPE_VIDEO = "video"

        /** [WatchPageData.type]が生放送 */
        const val WATCH_PAGE_DATA_TYPE_LIVE = "live"

        /** [WatchPageData.type]がダウンロード済み動画。 */
        const val WATCH_PAGE_DATA_TYPE_DOWNLOAD = "video"

        /** 文字列から[WatchPageInitialJSONData]を作成する */
        fun decodeWatchPageInitialDataFromString(initialJSONString: String): WatchPageInitialJSONData {
            return SerializationTool.jsonSerialization.decodeFromString(initialJSONString)
        }

        /** 文字列から[WatchPageResponseJSONData]を作成する */
        fun decodeWatchPageResponseDataFromString(responseJSONString: String): WatchPageResponseJSONData {
            return SerializationTool.jsonSerialization.decodeFromString(responseJSONString)
        }

    }

}