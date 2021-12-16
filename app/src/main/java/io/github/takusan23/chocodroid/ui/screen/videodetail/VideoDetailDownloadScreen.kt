package io.github.takusan23.chocodroid.ui.screen.videodetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.data.DownloadRequestData
import io.github.takusan23.chocodroid.ui.component.AndroidSnowConeSwitch
import io.github.takusan23.chocodroid.ui.component.M3Scaffold
import io.github.takusan23.internet.data.watchpage.WatchPageData
import kotlin.math.roundToInt

/**
 * 動画詳細のダウンロード画面
 *
 * @param isOnlineContent ダウンロード済みコンテンツの場合はtrue
 * @param watchPageData 動画情報
 * @param onDownloadClick 動画ダウンロードボタンを押したときに呼ばれる。渡されるデータは画質の指定など
 * @param onDeleteClick 削除ボタン押したときに呼ばれる。渡されるデータは動画ID
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoDetailDownloadScreen(
    watchPageData: WatchPageData,
    onDownloadClick: (DownloadRequestData) -> Unit = {},
    onDeleteClick: (String) -> Unit = {},
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    M3Scaffold(
        snackbarHostState = snackbarHostState,
        content = {
            Box(modifier = Modifier.padding(it)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth(),
                        fontSize = 20.sp,
                        text = stringResource(id = R.string.download),
                    )
                    if (watchPageData.type == WatchPageData.WATCH_PAGE_DATA_TYPE_VIDEO) {

                        // 画質一覧
                        val qualityList = remember { watchPageData.contentUrlList.mapNotNull { it.quality } }
                        // 画質
                        val quality = remember { mutableStateOf(qualityList.first()) }
                        // 分割数
                        val splitCount = remember { mutableStateOf(10) }
                        // 音声のみ
                        val isAudioOnly = remember { mutableStateOf(false) }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 10.dp, end = 10.dp)
                        ) {
                            if (!isAudioOnly.value) {
                                QualitySelectInput(
                                    currentQuality = quality.value,
                                    qualityList = qualityList,
                                    onQualitySelect = { quality.value = it }
                                )
                            }
                            DownloadSplitSlider(
                                value = splitCount.value,
                                onValueChange = { splitCount.value = it }
                            )
                            AudioOnlySwitch(
                                isEnable = isAudioOnly.value,
                                onValueChange = { isAudioOnly.value = it }
                            )
                        }
                        Divider()
                        Button(
                            modifier = Modifier
                                .padding(10.dp)
                                .align(alignment = Alignment.End),
                            shape = RoundedCornerShape(50),
                            onClick = {
                                onDownloadClick(DownloadRequestData(
                                    videoId = watchPageData.watchPageResponseJSONData.videoDetails.videoId,
                                    quality = quality.value,
                                    splitCount = splitCount.value,
                                    isAudioOnly = isAudioOnly.value,
                                ))
                            },
                            content = {
                                Icon(painter = painterResource(id = R.drawable.ic_outline_file_download_24), contentDescription = null)
                                Text(text = stringResource(id = R.string.download))
                            }
                        )
                    }
                }
            }
        },
    )
}

/**
 * 画質選択できるドロップダウン
 *
 * @param qualityList 画質一覧
 * @param onQualitySelect 画質を押したら呼ばれる
 * */
@Composable
private fun QualitySelectInput(currentQuality: String, qualityList: List<String>, onQualitySelect: (String) -> Unit) {
    val isShowDropdown = remember { mutableStateOf(false) }

    Box {
        OutlinedButton(
            modifier = Modifier
                .padding(top = 10.dp, bottom = 10.dp),
            onClick = { isShowDropdown.value = !isShowDropdown.value },
            content = {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = stringResource(id = R.string.quality))
                    Text(text = currentQuality, fontSize = 18.sp)
                }
                Icon(
                    painter = if (isShowDropdown.value) painterResource(id = R.drawable.ic_outline_expand_less_24) else painterResource(id = R.drawable.ic_outline_expand_more_24),
                    contentDescription = null
                )
            }
        )
        DropdownMenu(
            expanded = isShowDropdown.value,
            onDismissRequest = { isShowDropdown.value = false },
            content = {
                qualityList.forEach { qualityText ->
                    DropdownMenuItem(
                        onClick = {
                            onQualitySelect(qualityText)
                            isShowDropdown.value = false
                        },
                        content = { Text(text = qualityText) }
                    )
                }
            }
        )
    }
}

/**
 * ダウンロード分割数
 *
 * @param value 分割数
 * @param onValueChange 分割数が変更したら
 * */
@Composable
private fun DownloadSplitSlider(value: Int, onValueChange: (Int) -> Unit) {
    Column {
        Text(
            text = stringResource(id = R.string.download_split_count),
            modifier = Modifier
                .padding(top = 10.dp, bottom = 5.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Slider(
                modifier = Modifier.weight(1f),
                value = value.toFloat(),
                valueRange = 1f..10f,
                colors = SliderDefaults.colors(
                    thumbColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                    activeTrackColor = androidx.compose.material3.MaterialTheme.colorScheme.primary
                ),
                onValueChange = { onValueChange(it.roundToInt()) }
            )
            Text(
                modifier = Modifier.padding(10.dp),
                text = value.toString()
            )
        }
    }
}

/**
 * 音声のみダウンロードスイッチ
 *
 * @param isEnable ON / OFF
 * @param onValueChange 値変更時に呼ばれる
 * */
@Composable
private fun AudioOnlySwitch(
    isEnable: Boolean,
    onValueChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(id = R.string.download_audio_only)
        )
        AndroidSnowConeSwitch(
            isEnable = isEnable,
            onValueChange = onValueChange
        )
    }
}
