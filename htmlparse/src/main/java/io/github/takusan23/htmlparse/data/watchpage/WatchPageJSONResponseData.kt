package io.github.takusan23.htmlparse.data.watchpage

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * HTML内にあるJSON。動画情報とかURLとか
 * */
@Serializable
data class WatchPageJSONResponseData(
    val streamingData: StreamingData,
    val videoDetails: VideoDetails,
)

/**
 * 映像データの情報。
 *
 * [formats]の方は音声と映像が一つになったURLが提供されるんだけど、画質が選べない。
 * ついでにくっっそゴミ帯域幅でストリーミングだとまともに見れない。ｐｔｐｔすぎる。
 *
 * ちなみに[formats]を快適に読み込みたいならQUICプロトコルでやり取りすると多分快適に見れる（ブラウザではそうだった）
 *
 * じゃあ[adaptiveFormats]なら快適かと言われたらそうでもない。こっちの方は見る速度と同じ速度で読み込むぐらい帯域幅を節約してる模様。
 * 全然先読みしない。今回は[adaptiveFormats]を読み込んでる。ExoPlayerならMergingMediaSource？で行ける
 *
 * @param adaptiveFormats
 * @param formats
 * */
@Serializable
@SerialName("streamingData")
data class StreamingData(
    val formats: List<StreamingDataFormat>,
    val adaptiveFormats: List<AdaptiveFormat>
)

/**
 * 動画ファイルの情報です。映像+音声のリンクは画質があんま良くないので、
 * 本来は音声と映像が別々になったファイルを読み込むべき
 *
 * @param url 動画URL。[signatureCipher]がnullの場合はそのまま再生できます
 * @param signatureCipher [url]がnullの場合は[WatchPageData.decryptURL]を呼んで復号化したURLを取得してください。
 * */
@Serializable
data class StreamingDataFormat(
    val url: String? = null,
    val signatureCipher: String? = null,
)

@Serializable
data class AdaptiveFormat(
    val mimeType: String,
    val qualityLabel: String? = null,
    val url: String? = null,
    val signatureCipher: String? = null,
)

@Serializable
@SerialName("videoDetails")
data class VideoDetails(
    val videoId: String,
    val title: String,
    val lengthSeconds: Int,
    val keywords: List<String>? = null,
    val channelId: String,
    val shortDescription: String,
    val viewCount: String,
    val author: String,
)
