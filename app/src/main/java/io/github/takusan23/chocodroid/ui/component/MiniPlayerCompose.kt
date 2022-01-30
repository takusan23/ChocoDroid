package io.github.takusan23.chocodroid.ui.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.ui.tool.detectBackComponentTapGestures
import kotlin.math.roundToInt

/** ミニプレイヤーになるまでのしきい値。どこまで下にスワイプしたらミニプレイヤーにするかどうか */
private const val miniPlayerSlideValue = 500f

/** ミニプレイヤー時の横幅パーセント */
private const val miniPlayerWidthPercent = 0.5f


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
 * */
@Composable
fun MiniPlayerCompose(
    modifier: Modifier = Modifier,
    state: MiniPlayerState = rememberMiniPlayerState(),
    backgroundContent: @Composable () -> Unit,
    playerContent: @Composable (BoxScope.() -> Unit),
    detailContent: @Composable (BoxScope.() -> Unit),
) {

    // もし表示できない場合は速攻return
    if (state.currentState.value == MiniPlayerStateType.EndOrHide) {
        return
    }

    // 現在の状態
    val currentState = state.currentState
    // 現在のミニプレーヤーの横幅（パーセント） 1f ～ miniPlayerWidthPercent の範囲
    val currentPlayerWidthPercent = state.currentPlayerWidthPercent
    // ミニプレイヤーの位置。translationなんたらみたいなやつ
    val offsetX = remember { mutableStateOf(0f) }
    val offsetY = remember { mutableStateOf(0f) }
    // ドラッグ中かどうか
    val isDragging = remember { mutableStateOf(false) }
    // 終了可能な場合はtrue
    val isAvailableEndOfLife = remember { mutableStateOf(false) }

    // 1fから0.5f までのパーセンテージを 1fから0f へ変換して外部へ状態を提供する
    LaunchedEffect(key1 = currentPlayerWidthPercent.value, block = {
        state.progress.value = (currentPlayerWidthPercent.value / miniPlayerWidthPercent) - 1f
    })

    /** ドラッグ操作が終了した際に呼ぶ */
    fun setDragCancel() {
        // ミニプレイヤー遷移中なら戻す
        if (currentState.value != MiniPlayerStateType.MiniPlayer) {
            state.setState(MiniPlayerStateType.Default)
        }
        isDragging.value = false
        // 終了可能フラグが経っている場合は終了させる
        if (isAvailableEndOfLife.value) {
            state.setState(MiniPlayerStateType.EndOrHide)
        }
    }

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

        Box(modifier = Modifier.fillMaxSize()) {

            // 後ろに描画するものがあれば
            backgroundContent()

            Box(
                modifier = Modifier
                    .offset { IntOffset(x = animOffsetX.value.roundToInt(), y = animOffsetY.value.roundToInt()) } // 位置
                    .background(Color.Black)
                    .fillMaxWidth(currentPlayerWidthPercent.value)
                    .aspectRatio(1.7f) // 16:9を維持
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { isDragging.value = true },
                            onDragCancel = { setDragCancel() },
                            onDragEnd = { setDragCancel() },
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
                                // 画面外に入れたら終了可能フラグを立てる
                                val playerWidth = boxWidth * currentPlayerWidthPercent.value
                                val playerHeight = (playerWidth / 16) * 9
                                isAvailableEndOfLife.value = offsetY.value >= (boxHeight - playerHeight)
                            }
                        )
                    }
                    .pointerInput(Unit) {
                        // ミニプレイヤー押したら元に戻すため
                        // 親コンポーネントでもクリックイベントを受け取る独自のやつ
                        detectBackComponentTapGestures {
                            state.setState(MiniPlayerStateType.Default)
                        }
                    },
                content = playerContent
            )

            // ここにドラッグしたら削除できますよ案内
            MiniPlayerDeleteArea(
                modifier = Modifier.align(Alignment.BottomCenter),
                isVisible = isDragging.value && currentState.value == MiniPlayerStateType.MiniPlayer,
            )

            // 動画説明文
            if (currentState.value == MiniPlayerStateType.Default) {
                Column {
                    // 動画再生部分のスペース
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.7f)
                    )
                    Box(
                        modifier = Modifier
                            .alpha(currentPlayerWidthPercent.value), // 薄くしていく。重そう
                        content = detailContent
                    )
                }
            }
        }
    }
}

/**
 * ミニプレイヤーをここにドラッグして削除のUI
 * */
@Composable
private fun MiniPlayerDeleteArea(
    modifier: Modifier = Modifier,
    isVisible: Boolean = false,
) {
    // 親の大きさを取得
    BoxWithConstraints(modifier = modifier) {
        // ミニプレイヤー時の高さだけ表示
        val miniPlayerWidth = maxWidth * miniPlayerWidthPercent
        // 削除部分
        val miniPlayerHeight = (miniPlayerWidth / 16) * 9
        // 表示 / 非表示 で高さが変わるやつ
        val deleteAreaHeight = animateDpAsState(targetValue = if (isVisible) miniPlayerHeight else 0.dp)

        // ここにドラッグしてプレイヤー終了
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(deleteAreaHeight.value),
            color = Color.Red.copy(alpha = 0.8f),
            contentColor = Color.White,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_outline_close_24),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.padding(5.dp))
                Text(text = stringResource(id = R.string.miniplayer_close_area_text))
            }
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
    private val onStateChange: (MiniPlayerStateType) -> Unit,
) {
    /** プレイヤーの状態 */
    val currentState = mutableStateOf(initialValue)

    /** ミニプレイヤーへ遷移しているときに、ここの値が減っていく 1fから[miniPlayerWidthPercent]まで */
    val currentPlayerWidthPercent = mutableStateOf(1f)

    /** プレイヤーの遷移状態。 */
    val progress = mutableStateOf(0f)

    /**
     * プレイヤーの状態を変更する
     *
     * @param miniPlayerStateType [MiniPlayerStateType]
     * */
    fun setState(miniPlayerStateType: MiniPlayerStateType) {
        val isNeedUpdateEvent = currentState.value != miniPlayerStateType
        currentState.value = miniPlayerStateType
        currentPlayerWidthPercent.value = if (miniPlayerStateType == MiniPlayerStateType.MiniPlayer) miniPlayerWidthPercent else 1f
        // 更新通知
        if (isNeedUpdateEvent) {
            onStateChange(miniPlayerStateType)
        }
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

    /** 終了時 or まだ出してない */
    EndOrHide,

    /** フルスクリーン */
    Fullscreen,
}