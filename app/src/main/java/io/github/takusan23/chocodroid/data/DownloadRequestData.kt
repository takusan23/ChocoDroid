package io.github.takusan23.chocodroid.data

import java.io.Serializable

/**
 * 動画ダウンロードをするときにリクエストを詰めたデータ
 *
 * @param videoId 動画ID
 * @param quality 画質
 * @param isAudioOnly 音声のみの場合はtrue
 * */
data class DownloadRequestData(
    val videoId: String,
    val isAudioOnly: Boolean = false,
    val quality: String = "360p",
) : Serializable