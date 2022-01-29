package io.github.takusan23.chocodroid.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.unit.IntOffset
import io.github.takusan23.chocodroid.ui.tool.detectBackComponentTapGestures
import kotlin.math.roundToInt

/**
 * ミニプレイヤー。KotlinとComposeでできている
 *
 * たちみどろいどで使ってたやつ
 *
 * これを親にするのでプレイヤーの裏側になにか描画したい内容があれば[backgroundContent]を使ってください。
 *
 * @param modifier Modifier
 * @param backgroundContent ミニプレイヤーの後ろになにか描画するものがあればどうぞ（こいつが覆いかぶさる形になっているので）
 * @param playerContent プレイヤー部分に描画するもの。プレイヤー
 * @param detailContent 動画説明文の部分
 * @param state プレイヤーの状態を公開するために使う。状態変更コールバック関数もこいつにある
 * */
@Composable
fun MiniPlayerCompose(
    modifier: Modifier = Modifier,
    state: MiniPlayerState = rememberMiniPlayerState(),
    backgroundContent: @Composable () -> Unit,
    playerContent: @Composable (BoxScope.() -> Unit),
    detailContent: @Composable (BoxScope.() -> Unit),
) {
    // ミニプレイヤーになるまでのしきい値。どこまで下にスワイプしたらミニプレイヤーにするかどうか
    val miniPlayerSlideValue = 500f
    // ミニプレイヤー時の横幅パーセント
    val miniPlayerWidthPercent = 0.5f

    // 現在の状態
    val currentState = remember { mutableStateOf(MiniPlayerStateType.Default) }
    // 現在のミニプレーヤーの横幅（パーセント）
    val currentPlayerWidthPercent = remember { mutableStateOf(1f) }
    // ミニプレイヤーの位置。translationなんたらみたいなやつ
    val offsetX = remember { mutableStateOf(0f) }
    val offsetY = remember { mutableStateOf(0f) }
    // ドラッグ中かどうか
    val isDragging = remember { mutableStateOf(false) }
    // 終了可能な場合はtrue
    val isAvailableEndOfLife = remember { mutableStateOf(false) }

    // 親Viewの大きさを取るのに使った。
    BoxWithConstraints(modifier = modifier) {
        // 全体の幅
        val boxWidth = constraints.maxWidth.toFloat()
        // 全体の高さ
        val boxHeight = constraints.maxHeight.toFloat()

        // アニメーションするオフセット値。これを実際のオフセットに入れる（もう一つの方は位置を保持しておくためのもの）
        val animOffsetX = animateFloatAsState(targetValue = when {
            // ミニプレイヤータップ時。通常時に戻します
            !isDragging.value && currentState.value == MiniPlayerStateType.Default -> 0f
            // 何もなければ現状のオフセットを使う
            else -> offsetX.value
        }, finishedListener = { offsetX.value = it })

        val animOffsetY = animateFloatAsState(targetValue = when {
            // ミニプレイヤータップ時。通常時に戻します
            !isDragging.value && currentState.value == MiniPlayerStateType.Default -> 0f
            // 終了アニメーション始まります
            !isDragging.value && isAvailableEndOfLife.value -> boxHeight
            else -> offsetY.value // 何もなければ現状のオフセットを使う
        }, finishedListener = { offsetY.value = it })

        Box(modifier = Modifier) {
            // 後ろに描画するものがあれば
            backgroundContent()

            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .offset { IntOffset(x = animOffsetX.value.roundToInt(), y = animOffsetY.value.roundToInt()) } // 位置
                        .background(Color.Black)
                        .fillMaxWidth(currentPlayerWidthPercent.value)
                        .aspectRatio(1.7f) // 16:9を維持
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { isDragging.value = true },
                                onDragCancel = { isDragging.value = false },
                                onDragEnd = { isDragging.value = false },
                                onDrag = { change, dragAmount ->
                                    change.consumed.downChange = false
                                    val dragY = dragAmount.y
                                    // 0以下だと画面外なので対策
                                    offsetY.value = maxOf(offsetY.value + dragY, 0f)
                                    // 一定まで移動させたらミニプレーヤーへ遷移
                                    if (currentState.value != MiniPlayerStateType.MiniPlayer) {
                                        // パーセント再計算
                                        currentPlayerWidthPercent.value = maxOf(miniPlayerWidthPercent, 1f - (offsetY.value / miniPlayerSlideValue))
                                        // 状態変更
                                        currentState.value = if (currentPlayerWidthPercent.value == miniPlayerWidthPercent) MiniPlayerStateType.MiniPlayer else MiniPlayerStateType.Default
                                        // ちょっとずつY軸もずらすことで真ん中へ
                                        offsetX.value = (boxWidth / 2) * (1f - currentPlayerWidthPercent.value)
                                    } else {
                                        val playerWidth = boxWidth * currentPlayerWidthPercent.value
                                        // ミニプレイヤー時はY軸の操作も可能にする。画面外対策コード
                                        offsetX.value = if (offsetX.value + dragAmount.x in 0f..(boxWidth - playerWidth)) {
                                            offsetX.value + dragAmount.x
                                        } else offsetX.value
                                    }
                                    // 画面外に入れたら終了
                                    val playerWidth = boxWidth * currentPlayerWidthPercent.value
                                    val playerHeight = (playerWidth / 16) * 9
                                    isAvailableEndOfLife.value = offsetY.value >= (boxHeight - playerHeight)
                                }
                            )
                        }
                        .pointerInput(Unit) {
                            // ミニプレイヤー押したら元に戻す
                            detectBackComponentTapGestures {
                                currentState.value = MiniPlayerStateType.Default
                                currentPlayerWidthPercent.value = 1f
                            }
                        },
                    content = playerContent
                )

                // 動画説明文
                if (currentState.value == MiniPlayerStateType.Default) {
                    Box(
                        modifier = Modifier
                            .alpha(currentPlayerWidthPercent.value), // 薄くしていく。重そう
                        content = detailContent
                    )
                }
            }


/*
            // 現在ミニプレイヤーかどうか
            val isCurrentMiniPlayer = state.currentState.value == MiniPlayerStateType.MiniPlayer
            // 現在終了モードか
            val isCurrentEndMode = state.currentState.value == MiniPlayerStateType.End
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
                isCurrentMiniPlayer && !isCurrentEndMode -> MiniPlayerStateType.MiniPlayer // ミニプレイヤー時
                isCurrentEndMode -> MiniPlayerStateType.End // 終了時
                else -> MiniPlayerStateType.Default // 通常時
            }
            // 更新を通知
            LaunchedEffect(key1 = currentState, block = { state.onStateChange(currentState) })

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
                if (state.currentState.value == MiniPlayerStateType.Fullscreen) {
                    // 全画面再生時は動画説明文は表示しないので
                    Box(
                        modifier = Modifier
                            .background(Color.Black)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center,
                        content = playerContent
                    )
                } else {
                    // プレイヤー部分
                    Box(
                        modifier = Modifier
                            .background(Color.Black)
                            .fillMaxWidth(playerWidthEx.value) // 引数で大きさを決められる
                            .aspectRatio(1.7f) // 16:9を維持
                            .draggable(
                                enabled = !state.isDisableDragGesture.value,
                                startDragImmediately = false,
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
                                            MiniPlayerStateType.Default
                                        }
                                        progress in 0.5f..1f -> {
                                            MiniPlayerStateType.MiniPlayer
                                        }
                                        else -> {
                                            MiniPlayerStateType.End
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
*/
        }
    }
}

/**
 * ミニプレイヤー操作用クラス？
 * rememberなんちゃらState()をパクりたかった
 *
 * @param initialValue 最初の状態。[MiniPlayerStateType]参照
 * @param onStateChange 状態が変更されたら呼ばれる関数。同じ値の場合は呼ばれません
 * */
class MiniPlayerState(
    initialValue: MiniPlayerStateType = MiniPlayerStateType.Default,
    val onStateChange: (MiniPlayerStateType) -> Unit,
) {
    /** プレイヤーの状態 */
    var currentState = mutableStateOf(initialValue)

    /** プレイヤーの遷移状態。 */
    val progress = mutableStateOf(0f)

    /** ドラッグ操作無効時はtrue */
    val isDisableDragGesture = mutableStateOf(false)

    val offsetX = mutableStateOf(0f)

    /** プレイヤーの状態を更新する */
    fun setState(toState: MiniPlayerStateType) {
        currentState.value = toState
    }

    /** ドラッグ操作を無効にする場合はtrue */
    fun isDisableDraggableGesture(isDisabled: Boolean) {
        isDisableDragGesture.value = isDisabled
    }

    companion object {

        /**
         * MiniPlayerStateの状態を保存、または復元するための関数。
         *
         * @param currentState 保存前の状態
         * @param onStateChange 復元後の変更コールバック
         * */
        fun Saver(
            currentState: MiniPlayerStateType = MiniPlayerStateType.Default,
            onStateChange: (MiniPlayerStateType) -> Unit,
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
    initialState: MiniPlayerStateType = MiniPlayerStateType.Default,
    onStateChange: (MiniPlayerStateType) -> Unit = {},
): MiniPlayerState {
    return rememberSaveable(
        saver = MiniPlayerState.Saver(initialState, onStateChange),
        init = { MiniPlayerState(initialState, onStateChange) },
    )
}

enum class MiniPlayerStateType {
    /** 通常時 */
    Default,

    /** ミニプレーヤー */
    MiniPlayer,

    /** 終了時 */
    End,

    /** フルスクリーン */
    Fullscreen,

    /** 破棄したら */
    Destroy,
}