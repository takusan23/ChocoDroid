package io.github.takusan23.chocodroid.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.tool.TimeFormatTool
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
 * */
@Composable
fun VideoControlUI(
    watchPageData: WatchPageData,
    controller: ExoPlayerComposeController,
    state: MiniPlayerState,
    mediaUrlData: MediaUrlData,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    // プレイヤー押したらプレイヤーUIを非表示にしたいので
    val isShowPlayerUI = remember { mutableStateOf(true) }

    // 一定時間後にfalseにする
    LaunchedEffect(key1 = isShowPlayerUI.value, block = {
        delay(3000L)
        isShowPlayerUI.value = false
    })

    // プレイヤーのUIの大きさがほしいのでBoxWithなんたらをつかう
    BoxWithConstraints {
        // まとめて色を変えられる
        Surface(
            contentColor = Color.White, // アイコンとかテキストの色をまとめて指定
            color = Color.Transparent,
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            isShowPlayerUI.value = !isShowPlayerUI.value
                        },
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
                                        state.setState(if (state.currentState.value == MiniPlayerStateValue.MiniPlayer) MiniPlayerStateValue.Default else MiniPlayerStateValue.MiniPlayer)
                                    }
                                },
                                content = {
                                    Icon(
                                        painter = painterResource(id = if (state.currentState.value == MiniPlayerStateValue.MiniPlayer) R.drawable.ic_outline_expand_less_24 else R.drawable.ic_outline_expand_more_24),
                                        contentDescription = null
                                    )
                                }
                            )
                            Text(
                                text = watchPageData.watchPageResponseJSONData.videoDetails.title,
                                modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                                maxLines = 1,
                            )
                        }
                        // ミニプレイヤー時はこれ以降表示しない
                        if (state.progress.value < 0.5f) {
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
                                        onClick = { }
                                    )
                                }
                                RepeatButton(
                                    isEnableRepeat = controller.isRepeatEnable.value,
                                    onRepeatChange = { controller.setRepeatMode(it) }
                                )
                            }
                        }
                    }
                    // ミニプレイヤー時はこれ以降表示しない
                    if (state.progress.value < 0.5f) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clickable { controller.exoPlayer.playWhenReady = !controller.exoPlayer.playWhenReady },
                                painter = painterResource(id = if (controller.exoPlayer.playWhenReady) R.drawable.ic_outline_pause_24 else R.drawable.ic_outline_play_arrow_24),
                                colorFilter = ColorFilter.tint(Color.White),
                                contentDescription = null
                            )
                        }

                        // 生放送時はシークバー出さない
                        if (!watchPageData.isLiveStream()) {
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
                                    // 操作中でなければ
                                    LaunchedEffect(key1 = controller.currentPosition.value, block = {
                                        if (!isTouchingSlider.value) {
                                            progressFloat.value = (controller.currentPosition.value / controller.duration.value.toFloat())
                                        }
                                    })
                                    Slider(
                                        modifier = Modifier
                                            .padding(5.dp)
                                            .weight(1f),
                                        value = progressFloat.value,
                                        colors = SliderDefaults.colors(
                                            thumbColor = MaterialTheme.colorScheme.primary,
                                            activeTrackColor = MaterialTheme.colorScheme.primary
                                        ),
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
                            }
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