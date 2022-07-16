package io.github.takusan23.chocodroid.ui.screen.bottomsheet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.edit
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.setting.SettingKeyObject
import io.github.takusan23.chocodroid.setting.dataStore
import io.github.takusan23.chocodroid.ui.component.M3Scaffold
import kotlinx.coroutines.launch

/**
 * プレイヤーのメニューを押したときに表示する
 *
 * 音量調整とか
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoPlayerMenuScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dataStore = remember { context.dataStore }
    val currentSettings = dataStore.data.collectAsState(initial = null)

    M3Scaffold(
        modifier = Modifier.fillMaxHeight(0.5f),
        content = {
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
                    text = stringResource(id = R.string.player_menu),
                )
                currentSettings.value?.also { preferences ->
                    // 音量調整
                    VolumeSettingItem(
                        currentVolume = preferences[SettingKeyObject.PLAYER_VOLUME] ?: 1f,
                        onVolumeChange = { volume ->
                            scope.launch {
                                dataStore.edit {
                                    it[SettingKeyObject.PLAYER_VOLUME] = volume
                                }
                            }
                        }
                    )
                }
            }
        }
    )
}

/**
 * 音量調整
 *
 * @param currentVolume 現在の音量
 * @param onVolumeChange 音量変化時に呼ばれる
 */
@Composable
private fun VolumeSettingItem(
    currentVolume: Float,
    onVolumeChange: (Float) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                modifier = Modifier
                    .padding(10.dp),
                painter = painterResource(id = R.drawable.ic_outline_volume_up_24),
                contentDescription = null
            )
            Column {
                Text(
                    modifier = Modifier
                        .padding(3.dp),
                    text = stringResource(id = R.string.player_menu_volume),
                    fontSize = 18.sp
                )
                Slider(
                    modifier = Modifier
                        .padding(3.dp),
                    value = currentVolume,
                    onValueChange = onVolumeChange
                )
            }
        }
    }
}

/**
 * 表示の際に渡すクラス
 */
class VideoPlayerMenuScreenInitData() : BottomSheetInitData(BottomSheetScreenList.PlayerMenu)