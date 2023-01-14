package io.github.takusan23.chocodroid.player

// プレイヤーの状態をデータクラスで表現してUIに返す

/** 再生状況 */
sealed class PlayerState {

    /** 再生中 */
    object Play : PlayerState()

    /** 一時停止中 */
    object Pause : PlayerState()

    /** バッファリング中 */
    object Buffering : PlayerState()

    /** 破棄済み */
    object Destroy : PlayerState()

    /** エラー？ */
    object Error : PlayerState()
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
data class VideoMetaData(
    val durationMs: Long,
    val aspectRate: Float,
)