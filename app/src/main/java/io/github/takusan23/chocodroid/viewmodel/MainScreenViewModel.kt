package io.github.takusan23.chocodroid.viewmodel

import android.app.Application
import io.github.takusan23.chocodroid.ui.screen.bottomsheet.BottomSheetInitData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * [io.github.takusan23.chocodroid.MainActivity]で使うViewModel
 *
 * Composeでも使います（画面回転しんどいので）
 * */
class MainScreenViewModel(application: Application) : BaseAndroidViewModel(application) {

    private val context = application.applicationContext

    private val _bottomSheetNav = MutableStateFlow<BottomSheetInitData?>(null)
    private val _isFullscreenMode = MutableStateFlow(false)

    /** ボトムシートの画面遷移を管理するFlow */
    val bottomSheetNavigation = _bottomSheetNav as StateFlow<BottomSheetInitData?>

    /** 全画面モード？ */
    val isFullscreenMode = _isFullscreenMode as StateFlow<Boolean>

    /** [BottomSheetInitData]をセットしてボトムシートの画面遷移を行う */
    fun navigateBottomSheet(initData: BottomSheetInitData) {
        _bottomSheetNav.value = initData
    }

}