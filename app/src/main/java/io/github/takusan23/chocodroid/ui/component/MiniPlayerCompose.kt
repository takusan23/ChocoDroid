package io.github.takusan23.chocodroid.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

/**
 * ミニプレイヤー。KotlinとComposeでできている
 *
 * これを親にするのでプレイヤーの裏側になにか描画したい内容があれば[backgroundContent]を使ってください。
 *
 * @param modifier Modifier
 * @param backgroundContent ミニプレイヤーの後ろになにか描画するものがあればどうぞ（こいつが覆いかぶさる形になっているので）
 * @param playerContent プレイヤー部分に描画するもの。プレイヤー
 * @param detailContent 動画説明文の部分
 * @param state プレイヤーの状態を公開するために使う。状態変更コールバック関数もこいつにある
 * @param isDisableMiniPlayer ドラッグ操作を禁止する場合はtrue
 * @param isFullScreenMode 全画面モードにするならtrueに
 * */
@Composable
fun MiniPlayerCompose(
    modifier: Modifier = Modifier,
    state: MiniPlayerState = rememberMiniPlayerState(),
    isDisableMiniPlayer: Boolean = false,
    isFullScreenMode: Boolean = false,
    backgroundContent: @Composable () -> Unit,
    playerContent: @Composable (BoxScope.() -> Unit),
    detailContent: @Composable (BoxScope.() -> Unit),
) {
    // 親Viewの大きさを取るのに使った。
    BoxWithConstraints(modifier = modifier) {
        val boxWidth = constraints.maxWidth
        val boxHeight = constraints.maxHeight

        // ミニプレイヤー時の高さ
        val miniPlayerHeight = ((boxWidth / 2) / 16) * 9

        Box(modifier = Modifier) {

            // 後ろに描画するものがあれば
            backgroundContent()

            // ミニプレイヤーの位置。translationなんたらみたいなやつ
            val offsetX = remember { mutableStateOf(0f) }
            val offsetY = remember { mutableStateOf(0f) }

            // 現在ミニプレイヤーかどうか
            val isCurrentMiniPlayer = state.currentState.value == MiniPlayerStateValue.MiniPlayer
            // 現在終了モードか
            val isCurrentEndMode = state.currentState.value == MiniPlayerStateValue.End
            // パーセンテージ。offsetYの値が変わると自動で変わる
            val progress = offsetY.value / (boxHeight - miniPlayerHeight)
            // ミニプレイヤーの大きさをFloatで。1fから0.5fまで
            val playerWidthProgress = remember { mutableStateOf(1f) }
            // 操作中はtrue
            val isDragging = remember { mutableStateOf(false) }
            // アニメーションしながら戻る。isMiniPlayerの値が変わると動く
            // ちなみにJetpack Composeのアニメーションはスタートの値の指定がない。スタートの値はアニメーション開始前の値になる。
            val playerWidthEx = animateFloatAsState(targetValue = when {
                isDragging.value -> playerWidthProgress.value // 操作中なら操作中の場所へ
                isCurrentMiniPlayer && !isCurrentEndMode -> 0.5f // ミニプレイヤー遷移命令ならミニプレイヤーのサイズへ
                isCurrentEndMode -> 0.5f // 終了時はミニプレイヤーのまま
                else -> 1f // それ以外
            }, finishedListener = { playerWidthProgress.value = it })
            val offSetYEx = animateFloatAsState(targetValue = when {
                isDragging.value -> offsetY.value // 操作中なら操作中の値
                isCurrentMiniPlayer && !isCurrentEndMode -> (boxHeight - miniPlayerHeight).toFloat() // ミニプレイヤー遷移命令ならミニプレイヤーのサイズへ
                isCurrentEndMode -> boxHeight.toFloat()
                else -> 1f // それ以外
            }, finishedListener = { offsetY.value = it })

            // ミニプレイヤーの状態を高階関数で提供する
            val currentState = when {
                isCurrentMiniPlayer && !isCurrentEndMode -> MiniPlayerStateValue.MiniPlayer // ミニプレイヤー時
                isCurrentEndMode -> MiniPlayerStateValue.End // 終了時
                else -> MiniPlayerStateValue.Default // 通常時
            }
            // 更新を通知
            LaunchedEffect(key1 = currentState, block = {
                println(currentState)
                state.onStateChange(currentState)
            })

            // 外部にプレイヤーの進捗を公開する
            state.progress.value = offSetYEx.value / (boxHeight - miniPlayerHeight)

            // ミニプレイヤー部分
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .offset { IntOffset(x = offsetX.value.roundToInt(), y = offSetYEx.value.roundToInt()) }, // ずらす位置
                //  verticalArrangement = Arrangement.Bottom, // 下に行くように
                horizontalAlignment = Alignment.End,// 右に行くように
            ) {
                if (isFullScreenMode) {
                    // 全画面再生時は動画説明文は表示しないので
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        content = playerContent
                    )
                } else {
                    // プレイヤー部分
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF252525))
                            .fillMaxWidth(playerWidthEx.value) // 引数で大きさを決められる
                            .aspectRatio(1.7f) // 16:9を維持
                            .draggable(
                                enabled = !isDisableMiniPlayer,
                                startDragImmediately = true,
                                orientation = Orientation.Vertical,
                                state = rememberDraggableState { delta ->
                                    // どれだけ移動したかが渡される
                                    val currentOffsetY = offsetY.value.toInt()
                                    when {
                                        currentOffsetY in 0..(boxHeight - miniPlayerHeight) -> {
                                            // 通常
                                            offsetY.value += delta.toInt()
                                            playerWidthProgress.value = 1f - (progress / 2)
                                        }
                                        currentOffsetY > (boxHeight - miniPlayerHeight) -> {
                                            // 終了させる
                                            offsetY.value += delta.toInt()
                                        }
                                        else -> {
                                            // 画面外突入
                                            offsetY.value = when {
                                                currentOffsetY <= 0 -> 0f
                                                currentOffsetY > (boxHeight - miniPlayerHeight) -> boxHeight.toFloat()
                                                else -> (boxHeight - miniPlayerHeight).toFloat()
                                            }
                                        }
                                    }
                                },
                                onDragStopped = { velocity ->
                                    // スワイプ速度が渡される
                                    state.currentState.value = when {
                                        progress < 0.5f -> {
                                            MiniPlayerStateValue.Default
                                        }
                                        progress in 0.5f..1f -> {
                                            MiniPlayerStateValue.MiniPlayer
                                        }
                                        else -> {
                                            MiniPlayerStateValue.End
                                        }
                                    }
                                    isDragging.value = false
                                },
                                onDragStarted = {
                                    // ドラッグ開始
                                    isDragging.value = true
                                }
                            ),
                        content = playerContent
                    )
                    // 動画説明文の部分
                    if (progress < 1f) {
                        Box(
                            modifier = Modifier.alpha(1f - progress), // 本家みたいに薄くしてみる
                            content = detailContent
                        )
                    }
                }
            }
        }
    }
}

/**
 * ミニプレイヤー操作用クラス？
 * rememberなんちゃらState()をパクりたかった
 *
 * @param initialValue 最初の状態。[MiniPlayerStateValue]参照
 * @param onStateChange 状態が変更されたら呼ばれる関数。同じ値の場合は呼ばれません
 * */
class MiniPlayerState(
    initialValue: Int = MiniPlayerStateValue.Default,
    val onStateChange: (Int) -> Unit,
) {
    /** プレイヤーの状態 */
    var currentState = mutableStateOf(initialValue)

    /** プレイヤーの遷移状態。 */
    val progress = mutableStateOf(0f)

    companion object {

        /**
         * MiniPlayerStateの状態を保存、または復元するための関数。
         *
         * @param currentState 保存前の状態
         * @param onStateChange 復元後の変更コールバック
         * */
        fun Saver(
            currentState: Int = MiniPlayerStateValue.Default,
            onStateChange: (Int) -> Unit,
        ): Saver<MiniPlayerState, *> = Saver(
            save = { currentState },
            restore = { restoreState -> MiniPlayerState(restoreState, onStateChange) }
        )

    }

}

/**
 * ミニプレイヤーの操作用クラスを取得する関数
 *
 * 戻り値でプレイヤーの操作ができます。
 *
 * @param isMiniPlayer 初期状態でミニプレイヤーならtrue
 * @param onStateChange 状態変更コールバック
 * */
@Composable
fun rememberMiniPlayerState(
    initialState: Int = MiniPlayerStateValue.Default,
    onStateChange: (Int) -> Unit = {},
): MiniPlayerState {
    return rememberSaveable(
        saver = MiniPlayerState.Saver(initialState, onStateChange),
    ) {
        MiniPlayerState(initialState, onStateChange)
    }
}

object MiniPlayerStateValue {
    /** デフォルトプレイヤーならこれ */
    const val Default = 0

    /** ミニプレイヤーのときはこれ */
    const val MiniPlayer = 1

    /** プレイヤーが終了したらこれ */
    const val End = 2

    /** DisposableEffectが呼ばれたらこれ */
    const val Destroy = 4

}
