package io.github.takusan23.htmlparse.data.watchpage

/**
 * 映像と音声のURLを入れるだけのデータクラス
 *
 * @param videoTrackUrl 映像トラックのURL
 * @param audioTrackUrl 音声トラックのURL
 * */
data class MediaUrlData(
    val videoTrackUrl: String,
    val audioTrackUrl: String
)