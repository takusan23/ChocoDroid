package io.github.takusan23.htmlparse.data

import io.github.takusan23.htmlparse.magic.DecryptMagic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.net.URLDecoder

@Serializable
data class WatchPageResponseData(
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
 * @param signatureCipher [url]がnullの場合は[decryptionMagic]を呼んで復号化したURLを取得してください。
 * */
@Serializable
data class StreamingDataFormat(
    val url: String? = null,
    val signatureCipher: String? = null,
) {
    /**
     * [signatureCipher]から正規のURLを作成する。このままではアクセスしても403返ってくるので
     *
     * てかそもそもURLの形じゃない
     * @return [signatureCipher]がnullじゃない場合は正規のURLを返します。
     * */
    fun decryptionMagic(): String? {
        if (signatureCipher == null) return null
        val params = signatureCipher
            .split("&")
            .map { URLDecoder.decode(it.split("=")[1], "utf-8") } // key=value の文字列を value だけにしてパーセントエンコーディングを戻す
        // 一個目が暗号化の鍵？鍵も加工してURLにつける
        val decryptKey = DecryptMagic.decrypt(params.first())
        // URL完成
        return "${params[2]}&sig=$decryptKey"
    }
}

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
