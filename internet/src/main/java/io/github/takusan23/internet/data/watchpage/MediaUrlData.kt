package io.github.takusan23.internet.data.watchpage

/**
 * 映像と音声のURLを入れるだけのデータクラス
 *
 * 注意：すべてnullableですが、[mixTrackUrl]がnullの場合は[audioTrackUrl]/[videoTrackUrl]/[quality]に値が設定されています。
 *
 * [audioTrackUrl]/[videoTrackUrl]がnullの場合は[mixTrackUrl]に値が設定されています。
 *
 * @param videoTrackUrl 映像トラックのURL
 * @param audioTrackUrl 音声トラックのURL
 * @param mixTrackUrl 生放送時はhlsアドレス、動画の場合でもDashManifestが存在する場合は[videoTrackUrl]ではなくこちらを読み込んでください
 * */
data class MediaUrlData(
    val urlType: MediaUrlType,
    val videoTrackUrl: String? = null,
    val audioTrackUrl: String? = null,
    val quality: String? = null,
    val mixTrackUrl: String? = null,
) {

    /** 配信の種類 */
    enum class MediaUrlType {
        /** HLS形式で配信されている */
        TYPE_HLS,

        /** DASH形式で配信されている */
        TYPE_DASH,

        /** プログレッシブ形式で配信されている */
        TYPE_PROGRESSIVE,

        /** ダウンロード済み。ダウンロード機能がある場合はこれを使おう */
        TYPE_OFFLINE
    }

}