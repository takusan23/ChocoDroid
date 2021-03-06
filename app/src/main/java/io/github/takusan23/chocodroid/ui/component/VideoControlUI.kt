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
 * @param controller ExoPlayer操作用
 * @param watchPageData 視聴ページデータ
 * @param state ミニプレイヤー操作用
 * @param mediaUrlData ストリーミング情報
 * @param onBottomSheetNavigate BottomSheetの表示と画面遷移をしてほしいときに呼ばれる
 * @param miniPlayerState ミニプレーヤーの状態変更するやつ
 * */
@Composable
fun VideoControlUI(
    watchPageData: WatchPageData,
    controller: ExoPlayerComposeController,
    state: MiniPlayerState,
    mediaUrlData: MediaUrlData,
    onBottomSheetNavigate: (BottomSheetInitData) -> Unit,
    miniPlayerState: MiniPlayerState,
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
    LaunchedEffect(key1 = isShowPlayerUI.value, block = {
        delay(3000L)
        isShowPlayerUI.value = false
    })

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
                        val currentPos = controller.currentPosition.value
                        val currentX = it.x
                        // 左半分で前シーク 右半分で次シーク
                        val isNextSeek = (playerWidth.value / 2) < currentX
                        controller.seek(if (isNextSeek) currentPos + doubleTapSeekValue else currentPos - doubleTapSeekValue)
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
                                    state.setState(if (state.currentState.value == MiniPlayerStateType.MiniPlayer) MiniPlayerStateType.Default else MiniPlayerStateType.MiniPlayer)
                                }
                            },
                            content = {
                                Icon(
                                    painter = painterResource(id = if (state.currentState.value == MiniPlayerStateType.MiniPlayer) R.drawable.ic_outline_expand_less_24 else R.drawable.ic_outline_expand_more_24),
                                    contentDescription = null
                                )
                            }
                        )
                        Text(
                            text = watchPageData.watchPageResponseJSONData.videoDetails.title,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 10.dp, end = 10.dp),
                            maxLines = 1,
                        )
                    }
                    // ミニプレイヤー時はこれ以降表示しない
                    if (state.progress.value > 0.5f) {
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
                if (state.progress.value > 0.5f) {
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
                                ) { controller.exoPlayer.playWhenReady = !controller.exoPlayer.playWhenReady },
                            painter = painterResource(id = if (controller.exoPlayer.playWhenReady) R.drawable.ic_outline_pause_24 else R.drawable.ic_outline_play_arrow_24),
                            colorFilter = ColorFilter.tint(Color.White),
                            contentDescription = null
                        )
                    }

                    // 生放送時はシークバー出さない
                    if (!watchPageData.isLiveContent()) {
                        Row(
                            modifier = Modifier.align(Alignment.BottomCenter),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier.padding(5.dp),
                                text = TimeFormatTool.videoDurationToFormatText(controller.currentPosition.value / 1000),
                            )
                            if (controller.currentPosition.value > 0 && controller.duration.value > 0) {
                                val isTouchingSlider = remember { mutableStateOf(false) }
                                val progressFloat = remember { mutableStateOf(0f) }
                                val progressBuffered = (controller.bufferedPosition.value / controller.duration.value.toFloat())

                                // 操作中でなければ
                                LaunchedEffect(key1 = controller.currentPosition.value, block = {
                                    if (!isTouchingSlider.value) {
                                        progressFloat.value = (controller.currentPosition.value / controller.duration.value.toFloat())
                                    }
                                })
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
                                        controller.seek((progressFloat.value * controller.duration.value).toLong())
                                    }
                                )
                            } else Spacer(modifier = Modifier.weight(1f))
                            Text(
                                modifier = Modifier.padding(5.dp),
                                text = TimeFormatTool.videoDurationToFormatText(controller.duration.value / 1000),
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