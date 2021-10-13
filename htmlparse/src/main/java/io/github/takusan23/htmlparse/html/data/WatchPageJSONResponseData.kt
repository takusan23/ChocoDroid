package io.github.takusan23.htmlparse.html.data

import io.github.takusan23.htmlparse.magic.DecryptMagic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.net.URLDecoder

/**
 * HTML内にあるJSON。動画情報とかURLとか
 * */
@Serializable
data class WatchPageJSONResponseData(
    val streamingData: StreamingData,
    val videoDetails: VideoDetails,
)

@Serializable
@SerialName("streamingData")
data class StreamingData(
    val formats: List<StreamingDataFormat>,
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
