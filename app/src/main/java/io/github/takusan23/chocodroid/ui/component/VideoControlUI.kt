package io.github.takusan23.chocodroid.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.player.ChocoDroidPlayer
import io.github.takusan23.chocodroid.player.CurrentPositionData
import io.github.takusan23.chocodroid.player.VideoData
import io.github.takusan23.chocodroid.setting.SettingKeyObject
import io.github.takusan23.chocodroid.setting.dataStore
import io.github.takusan23.chocodroid.tool.TimeFormatTool
import io.github.takusan23.chocodroid.ui.screen.bottomsheet.BottomSheetInitData
import io.github.takusan23.chocodroid.ui.screen.bottomsheet.QualityChangeScreenInitData
import io.github.takusan23.internet.data.watchpage.MediaUrlData
import io.github.takusan23.internet.data.watchpage.WatchPageData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 動画プレイヤーのUI。重ねる
 *
 * @param chocoDroidPlayer Applicationにあるコントローラー
 * @param videoData 動画データ情報
 * @param currentPositionData プレイヤーの現在位置
 * @param watchPageData 視聴ページデータ
 * @param mediaUrlData ストリーミング情報
 * @param onBottomSheetNavigate BottomSheetの表示と画面遷移をしてほしいときに呼ばれる
 * @param miniPlayerState ミニプレーヤーの状態変更するやつ
 */
@Composable
fun VideoControlUI(
    watchPageData: WatchPageData,
    chocoDroidPlayer: ChocoDroidPlayer,
    videoData: VideoData,
    currentPositionData: CurrentPositionData,
    miniPlayerState: MiniPlayerState,
    mediaUrlData: MediaUrlData,
    onBottomSheetNavigate: (BottomSheetInitData) -> Unit,
) {
    // ダブルタップのシーク量。変更可能にする
    val doubleTapSeekValue = 5_000L

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // プレイヤーはDataStoreの変更を検知してExoPlayerへ自動で反映される
    val dataStore = remember { context.dataStore }
    val currentSetting = dataStore.data.collectAsState(initial = null)

    // プレイヤーの幅
    val playerWidth = remember { mutableStateOf(0) }
    // プレイヤー押したらプレイヤーUIを非表示にしたいので
    val isShowPlayerUI = remember { mutableStateOf(true) }

    // 一定時間後にfalseにする
    LaunchedEffect(key1 = isShowPlayerUI.value) {
        delay(3000L)
        isShowPlayerUI.value = false
    }

    // まとめて色を変えられる
    Surface(
        contentColor = Color.White, // アイコンとかテキストの色をまとめて指定
        color = Color.Transparent,
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { playerWidth.value = it.width }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        isShowPlayerUI.value = !isShowPlayerUI.value
                    },
                    onDoubleTap = {
                        val currentPos = chocoDroidPlayer.currentPositionMs
                        val currentX = it.x
                        // 左半分で前シーク 右半分で次シーク
                        val isNextSeek = (playerWidth.value / 2) < currentX
                        chocoDroidPlayer.currentPositionMs = if (isNextSeek) currentPos + doubleTapSeekValue else currentPos - doubleTapSeekValue
                    }
                )
            }
    ) {
        if (isShowPlayerUI.value) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Black.copy(0.8f))
            ) {
                Column(modifier = Modifier.align(Alignment.TopCenter)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    // 操作しない100ms後に実行
                                    delay(100L)
                                    miniPlayerState.setState(if (miniPlayerState.currentState.value == MiniPlayerStateType.MiniPlayer) MiniPlayerStateType.Default else MiniPlayerStateType.MiniPlayer)
                                }
                            },
                            content = {
                                Icon(
                                    painter = painterResource(id = if (miniPlayerState.currentState.value == MiniPlayerStateType.MiniPlayer) R.drawable.ic_outline_expand_less_24 else R.drawable.ic_outline_expand_more_24),
                                    contentDescription = null
                                )
                            }
                        )
                        Text(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 10.dp, end = 10.dp),
                            text = watchPageData.watchPageResponseJSONData.videoDetails.title,
                            maxLines = 1,
                        )
                    }
                    // ミニプレイヤー時はこれ以降表示しない
                    if (miniPlayerState.progress.value > 0.5f) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 10.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (mediaUrlData.quality != null) {
                                QualityChangeButton(
                                    text = mediaUrlData.quality!!,
                                    onClick = { onBottomSheetNavigate(QualityChangeScreenInitData()) }
                                )
                            }
                            RepeatButton(
                                isEnableRepeat = currentSetting.value?.get(SettingKeyObject.PLAYER_REPEAT_MODE) ?: false,
                                onRepeatChange = { isRepeat ->
                                    scope.launch {
                                        dataStore.edit { it[SettingKeyObject.PLAYER_REPEAT_MODE] = isRepeat }
                                    }
                                }
                            )
                        }
                    }
                }
                // ミニプレイヤー時はこれ以降表示しない
                if (miniPlayerState.progress.value > 0.5f) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            modifier = Modifier
                                .size(60.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple(bounded = false, radius = 30.dp)
                                ) { chocoDroidPlayer.playWhenReady = !chocoDroidPlayer.playWhenReady },
                            painter = painterResource(id = if (chocoDroidPlayer.playWhenReady) R.drawable.ic_outline_pause_24 else R.drawable.ic_outline_play_arrow_24),
                            colorFilter = ColorFilter.tint(Color.White),
                            contentDescription = null
                        )
                    }

                    // 生放送時はシークバー出さない
                    if (!watchPageData.isLiveContent) {
                        Row(
                            modifier = Modifier.align(Alignment.BottomCenter),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier.padding(5.dp),
                                text = TimeFormatTool.videoDurationToFormatText(chocoDroidPlayer.currentPositionMs / 1000),
                            )
                            if (chocoDroidPlayer.currentPositionMs > 0 && videoData.durationMs > 0) {
                                val isTouchingSlider = remember { mutableStateOf(false) }
                                val progressFloat = remember { mutableStateOf(0f) }
                                val progressBuffered = (currentPositionData.bufferingPositionMs / videoData.durationMs.toFloat())

                                // 操作中でなければ
                                LaunchedEffect(key1 = currentPositionData.currentPositionMs) {
                                    if (!isTouchingSlider.value) {
                                        progressFloat.value = (currentPositionData.currentPositionMs / videoData.durationMs.toFloat())
                                    }
                                }
                                BufferSeekbar(
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .weight(1f),
                                    progressFloat = progressFloat.value,
                                    progressBuffered = progressBuffered,
                                    onValueChange = {
                                        isTouchingSlider.value = true
                                        progressFloat.value = it
                                    },
                                    onValueChangeFinished = {
                                        isTouchingSlider.value = false
                                        chocoDroidPlayer.currentPositionMs = (progressFloat.value * videoData.durationMs).toLong()
                                    }
                                )
                            } else Spacer(modifier = Modifier.weight(1f))
                            Text(
                                modifier = Modifier.padding(5.dp),
                                text = TimeFormatTool.videoDurationToFormatText(videoData.durationMs / 1000),
                            )
                            // 全画面ボタン
                            FullscreenButton(
                                modifier = Modifier.padding(5.dp),
                                isFullscreen = miniPlayerState.currentState.value == MiniPlayerStateType.Fullscreen,
                                onFullscreenChange = { miniPlayerState.setState(if (it) MiniPlayerStateType.Fullscreen else MiniPlayerStateType.Default) }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 画質変更ボタン
 *
 * @param text 画質
 * @param onClick 押したとき
 * */
@Composable
fun QualityChangeButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
) {
    TextButton(
        modifier = modifier,
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(containerColor = Color.Transparent, contentColor = Color.White),
        content = {
            Icon(painter = painterResource(id = R.drawable.ic_baseline_photo_filter_24), contentDescription = null)
            Text(text = text)
        }
    )
}