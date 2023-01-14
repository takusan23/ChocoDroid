package io.github.takusan23.chocodroid.ui.screen.bottomsheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.service.SmoothChocoPlayerService
import io.github.takusan23.chocodroid.ui.component.QualityList

/**
 * 画質一覧画面。BottomSheetで使う
 *
 * @param smoothChocoPlayerService サービスにある動画プレイヤー
 * @param onClose 閉じるときに呼ばれる
 * */
@Composable
fun QualityChangeScreen(
    smoothChocoPlayerService: SmoothChocoPlayerService,
    onClose: () -> Unit,
) {
    val currentQualityData = smoothChocoPlayerService.currentQualityData.collectAsStateWithLifecycle(initialValue = null)
    val watchPageResponseJSONData = smoothChocoPlayerService.watchPageResponseDataFlow.collectAsStateWithLifecycle(initialValue = null)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier.padding(10.dp),
            text = stringResource(id = R.string.quality),
            fontSize = 25.sp
        )
        if (currentQualityData.value?.quality != null && watchPageResponseJSONData.value != null && !watchPageResponseJSONData.value!!.isLiveContent) {
            // 動画のみ
            QualityList(
                currentQualityLabel = currentQualityData.value?.quality!!,
                qualityLabelList = watchPageResponseJSONData.value!!.contentUrlList.map { it.quality!! },
                onQualityClick = { qualityLabel ->
                    smoothChocoPlayerService.selectQuality(qualityLabel)
                    onClose()
                }
            )
        }
    }
}

class QualityChangeScreenInitData() : BottomSheetInitData(BottomSheetScreenList.QualityChange)