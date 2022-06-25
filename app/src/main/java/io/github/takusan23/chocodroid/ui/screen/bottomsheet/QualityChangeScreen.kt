package io.github.takusan23.chocodroid.ui.screen.bottomsheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.ui.component.QualityList
import io.github.takusan23.chocodroid.viewmodel.MainScreenViewModel

/**
 * 画質一覧画面。BottomSheetで使う
 *
 * @param mainScreenViewModel メイン画面ViewModel
 * @param onClose 閉じるときに呼ばれる
 * */
@Composable
fun QualityChangeScreen(
    mainScreenViewModel: MainScreenViewModel,
    onClose: () -> Unit,
) {
    val currentQualityData = mainScreenViewModel.mediaUrlData.collectAsState()
    val watchPageResponseJSONData = mainScreenViewModel.watchPageResponseDataFlow.collectAsState()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier.padding(10.dp),
            text = stringResource(id = R.string.quality),
            fontSize = 25.sp
        )
        if (currentQualityData.value?.quality != null && watchPageResponseJSONData.value != null && !watchPageResponseJSONData.value!!.isLiveContent()) {
            // 動画のみ
            QualityList(
                currentQualityLabel = currentQualityData.value?.quality!!,
                qualityLabelList = watchPageResponseJSONData.value!!.contentUrlList.map { it.quality!! },
                onQualityClick = { qualityLabel ->
                    mainScreenViewModel.selectMediaUrl(qualityLabel)
                    onClose()
                }
            )
        }
    }
}

class QualityChangeScreenInitData() : BottomSheetInitData(BottomSheetScreenList.QualityChange)