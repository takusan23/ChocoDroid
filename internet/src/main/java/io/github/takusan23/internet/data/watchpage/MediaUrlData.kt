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
 * @param mixTrackUrl 生放送時はhlsアドレス
 * */
data class MediaUrlData(
    val videoTrackUrl: String? = null,
    val audioTrackUrl: String? = null,
    val quality: String? = null,
    val mixTrackUrl: String? = null,
)