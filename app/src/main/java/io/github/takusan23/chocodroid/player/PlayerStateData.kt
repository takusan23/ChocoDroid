package io.github.takusan23.chocodroid.player

// プレイヤーの状態をデータクラスで表現してUIに返す

/** 再生状況 */
sealed class PlaybackStatus {

    /** 再生中 */
    object Play : PlaybackStatus()

    /** 一時停止中 */
    object Pause : PlaybackStatus()

    /** バッファリング中 */
    object Buffering : PlaybackStatus()

    /** 破棄済み */
    object Destroy : PlaybackStatus()

    /** エラー？ */
    object Error : PlaybackStatus()
}

/**
 * 再生中位置。ミリ秒
 *
 * @param currentPositionMs 再生中位置
 * @param bufferingPositionMs バッファリング中の位置
 */
data class CurrentPositionData(
    val currentPositionMs: Long,
    val bufferingPositionMs: Long,
)

/**
 * 動画の情報
 *
 * @param durationMs 動画時間
 * @param aspectRate アスペクト比。16:9 なら 1.7F
 */
data class VideoData(
    val durationMs: Long,
    val aspectRate: Float,
)