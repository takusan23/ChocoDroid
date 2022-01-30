package io.github.takusan23.chocodroid.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
 * @param onBottomSheetNavigate ボトムシートを出してほしいときに呼ばれる
 * */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChocoDroidBottomSheetNavigation(
    mainScreenViewModel: MainScreenViewModel,
    bottomSheetInitData: BottomSheetInitData?,
    modalBottomSheetState: ModalBottomSheetState,
    onBottomSheetNavigate: (BottomSheetInitData) -> Unit,
) {
    val scope = rememberCoroutineScope()

    Surface(color = MaterialTheme.colorScheme.primaryContainer) {
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
                    .background(color = MaterialTheme.colorScheme.primary, RoundedCornerShape(50))
            )
            // 画面遷移
            when (bottomSheetInitData?.screen) {
                // お気に入りフォルダ作成
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
                        initData = bottomSheetInitData as VideoListMenuScreenInitData,
                        onClose = { scope.launch { modalBottomSheetState.hide() } },
                        onBottomSheetNavigate = onBottomSheetNavigate
                    )
                }
                // お気に入りフォルダへ追加
                BottomSheetInitData.BottomSheetScreenList.AddVideoToFavoriteFolder -> {
                    AddVideoToFavoriteFolderScreen(initData = bottomSheetInitData as AddVideoToFavoriteFolderScreenInitData)
                }
                // 動画ダウンロード
                BottomSheetInitData.BottomSheetScreenList.VideoDownload -> {
                    VideoDownloadScreen(initData = bottomSheetInitData as VideoDownloadScreenInitData)
                }
                // 検索並び替え
                BottomSheetInitData.BottomSheetScreenList.SearchSortChange -> {
                    SearchSortScreen(onClose = { scope.launch { modalBottomSheetState.hide() } })
                }
                else -> {
                    // The initial value must have an associated anchor. 対策。何もない状態だとエラーが出るので適当においておく
                    Spacer(modifier = Modifier.size(10.dp))
                }
            }
        }
    }
}