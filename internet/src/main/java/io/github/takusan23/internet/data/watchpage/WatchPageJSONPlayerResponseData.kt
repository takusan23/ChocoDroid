package io.github.takusan23.internet.data.watchpage

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * HTML内にあるJSON。動画情報とかURLとか
 * */
@Serializable
data class WatchPageResponseJSONData(
    val streamingData: StreamingData,
    val videoDetails: VideoDetails,
    val microformat: Microformat,
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
 * 生放送の場合 [VideoDetails.isLive] は [hlsManifestUrl] を使ってください。生放送の場合は [formats] が無いと思います。
 *
 * [dashManifestUrl]について ---
 *
 * [adaptiveFormats]に記載されているURLで、一部の画質以外 404 を返す場合、[dashManifestUrl]から再生するとうまくいくかもしれません。
 *
 *
 * @param adaptiveFormats 音声と映像が別のURLで配信されてます。一部の動画は[formats]しか入ってない？
 * @param formats 音声と映像が合成済みのURLをくれる。代わりに画質が選べない
 * @param dashManifestUrl MPEG-DASH？HLSの代替手段みたいなやつ。なんか[adaptiveFormats]の画質で404を返すときはこっちを再生するといい？
 * @param hlsManifestUrl アクセスするとマスターのマニフェストファイルが返ってきます。こっから画質を選んで
 * */
@Serializable
data class StreamingData(
    val formats: List<StreamingDataFormat>? = null,
    val adaptiveFormats: List<AdaptiveFormat>,
    val dashManifestUrl: String? = null,
    val hlsManifestUrl: String? = null,
)

/**
 * 動画ファイルの情報です。映像+音声のリンクは画質があんま良くないので、
 * 本来は音声と映像が別々になったファイルを読み込むべき
 *
 * @param url 動画URL。[signatureCipher]がnullの場合はそのまま再生できます
 * @param signatureCipher [url]がnullの場合は[DecryptData.decryptURL]を呼んで復号化したURLを取得してください。
 * */
@Serializable
data class StreamingDataFormat(
    val url: String? = null,
    val signatureCipher: String? = null,
)

@Serializable
data class Microformat(
    val playerMicroformatRenderer: PlayerMicroformatRenderer,
)

@Serializable
data class PlayerMicroformatRenderer(
    val publishDate: String,
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
    val isLive: Boolean? = false,
    val thumbnail: Thumbnail,
)

@Serializable
data class Thumbnail(
    val thumbnails: List<ThumbnailUrl>,
)

@Serializable
data class ThumbnailUrl(
    val url: String,
    val width: Int,
    val height: Int,
)
