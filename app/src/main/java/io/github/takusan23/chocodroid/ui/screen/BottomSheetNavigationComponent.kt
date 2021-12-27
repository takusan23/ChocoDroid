package io.github.takusan23.chocodroid.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.takusan23.chocodroid.ui.screen.bottomsheet.*
import io.github.takusan23.chocodroid.viewmodel.MainScreenViewModel
import kotlinx.coroutines.launch

/**
 * BottomSheetのナビゲーション。画面遷移
 *
 * @param mainScreenViewModel メイン画面ViewModel
 * @param bottomSheetInitData 画面遷移データ。nullでも落ちません
 * @param modalBottomSheetState ボトムシート制御用
 * */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChocoDroidBottomSheetNavigation(
    mainScreenViewModel: MainScreenViewModel,
    bottomSheetInitData: BottomSheetInitData?,
    modalBottomSheetState: ModalBottomSheetState,
) {
    val scope = rememberCoroutineScope()

    Surface {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 引き出す棒
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .width(100.dp)
                    .height(10.dp)
                    .background(color = Color.Gray, RoundedCornerShape(50))
            )
            // 画面遷移
            when (bottomSheetInitData?.screen) {
                // お気に入り追加
                BottomSheetInitData.BottomSheetScreenList.AddFavoriteFolder -> {
                    AddFavoriteFolderScreen(onClose = { scope.launch { modalBottomSheetState.hide() } })
                }
                // 画質変更
                BottomSheetInitData.BottomSheetScreenList.QualityChange -> {
                    QualityChangeScreen(
                        mainScreenViewModel = mainScreenViewModel,
                        onClose = { scope.launch { modalBottomSheetState.hide() } }
                    )
                }
                // メニュー
                BottomSheetInitData.BottomSheetScreenList.VideoListMenu -> {
                    VideoListMenuScreen(
                        data = bottomSheetInitData as VideoListMenuScreenInitData,
                        onClose = { scope.launch { modalBottomSheetState.hide() } }
                    )
                }
                else -> {
                    // The initial value must have an associated anchor. 対策。何もない状態だとエラーが出るので適当においておく
                    Spacer(modifier = Modifier.size(10.dp))
                }
            }
        }
    }
}