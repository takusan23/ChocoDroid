package io.github.takusan23.internet.data.watchpage

import io.github.takusan23.internet.magic.DecryptMagic
import io.github.takusan23.internet.magic.data.AlgorithmFuncNameData
import io.github.takusan23.internet.magic.data.AlgorithmInvokeData
import io.github.takusan23.internet.tool.SerializationTool
import kotlinx.serialization.encodeToString
import java.net.URLDecoder

/**
 * 視聴ページ取得関数の戻り値
 *
 * @param watchPageResponseJSONData 動画情報など
 * @param watchPageInitialJSONData 投稿者のアイコンURLはここにあります
 * @param baseJsURL 暗号解除のJSコードのURL
 * @param algorithmFuncNameData 暗号解除で使うの文字列操作する関数名を入れたデータクラス
 * @param decryptInvokeList 暗号解除で使う関数を呼ぶ順番に入れた配列
 * */
data class WatchPageData(
    val watchPageResponseJSONData: WatchPageResponseJSONData,
    val watchPageInitialJSONData: WatchPageInitialJSONData,
    val baseJsURL: String,
    val algorithmFuncNameData: AlgorithmFuncNameData,
    val decryptInvokeList: List<AlgorithmInvokeData>,
) {

    /** [watchPageInitialJSONData]を文字列にする */
    fun encodeWatchPageInitialDataToString(): String {
        return SerializationTool.jsonSerialization.encodeToString(watchPageInitialJSONData)
    }

    /** [watchPageResponseJSONData]を文字列にする */
    fun encodeWatchPageResponseDataToString(): String {
        return SerializationTool.jsonSerialization.encodeToString(watchPageResponseJSONData)
    }

    /**
     * 復号化済みURLを返します
     * @param signatureCipher 署名とURLがついてる文字列
     * @return 復号化済みURL。アクセスできます
     * */
    fun decryptURL(signatureCipher: String): String {
        val params = signatureCipher
            .split("&")
            .map { URLDecoder.decode(it.split("=")[1], "utf-8") } // key=value の文字列を value だけにしてパーセントエンコーディングを戻す
        val decryptKey = DecryptMagic.decrypt(params.first(), algorithmFuncNameData, decryptInvokeList)
        return "${params[2]}&sig=$decryptKey"
    }

    /**
     * URLが署名されているか。署名されている場合は[decryptURL]を使って正規のURLへ変換する必要があります。
     *
     * 生放送時は使わないでください。
     *
     * @return URLが署名されている場合はtrue。
     * */
    fun isSignatureUrl(): Boolean {
        return watchPageResponseJSONData.streamingData.formats?.first()?.signatureCipher != null
    }

    /** 生放送時はtrue */
    fun isLiveStream() = watchPageResponseJSONData.videoDetails.isLive ?: false

    /**
     * 指定した画質の映像トラック、音声トラックを取り出す
     *
     * 映像のコーデックはH.264を取ってきます。
     *
     * @param quality 映像の画質。
     * @return 映像と音声のURLを入れたデーラクラス
     * */
    fun getMediaUrl(quality: String = "360p"): MediaUrlData {
        return if (isSignatureUrl()) {
            val videoTrackUrl = watchPageResponseJSONData.streamingData.adaptiveFormats.find { it.qualityLabel?.contains(quality) == true && it.mimeType.contains("video") && it.mimeType.contains("avc1") }!!.signatureCipher!!
            // 音声はとりあえず一番いいやつ
            val audioTrackUrl = watchPageResponseJSONData.streamingData.adaptiveFormats.find { it.mimeType.contains("audio") }!!.signatureCipher!!
            val decryptVideoTrackUrl = decryptURL(videoTrackUrl)
            val decryptAudioTrackUrl = decryptURL(audioTrackUrl)
            MediaUrlData(decryptVideoTrackUrl, decryptAudioTrackUrl)
        } else {
            val videoTrackUrl = watchPageResponseJSONData.streamingData.adaptiveFormats.find { it.qualityLabel?.contains(quality) == true && it.mimeType.contains("video") && it.mimeType.contains("avc1") }!!.url!!
            // 音声はとりあえず一番いいやつ
            val audioTrackUrl = watchPageResponseJSONData.streamingData.adaptiveFormats.find { it.mimeType.contains("audio") }!!.url!!
            MediaUrlData(videoTrackUrl, audioTrackUrl)
        }
    }

}