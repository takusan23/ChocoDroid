package io.github.takusan23.chocodroid.viewmodel

import android.app.Application
import android.graphics.Rect
import io.github.takusan23.chocodroid.ui.screen.bottomsheet.BottomSheetInitData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * [io.github.takusan23.chocodroid.MainActivity]で使うViewModel
 *
 * Composeでも使います（画面回転しんどいので）
 * */
class MainScreenViewModel(application: Application) : BaseAndroidViewModel(application) {

    private val context = application.applicationContext

    private val _bottomSheetNav = MutableStateFlow<BottomSheetInitData?>(null)
    private val _isFullscreenMode = MutableStateFlow(false)
    private val _pictureInPictureRect = MutableStateFlow<Rect?>(null)
    private val _pictureInPictureMode = MutableStateFlow(false)

    /** ボトムシートの画面遷移を管理するFlow */
    val bottomSheetNavigation = _bottomSheetNav.asStateFlow()

    /** 全画面モード？ */
    val isFullscreenMode = _isFullscreenMode.asStateFlow()

    /** プレイヤーの座標が変化したら呼ばれる */
    val pictureInPictureRect = _pictureInPictureRect.asStateFlow()

    /** ピクチャーインピクチャーの状態が変化したら呼ばれる */
    val pictureInPictureMode = _pictureInPictureMode.asStateFlow()

    /** [BottomSheetInitData]をセットしてボトムシートの画面遷移を行う */
    fun navigateBottomSheet(initData: BottomSheetInitData) {
        _bottomSheetNav.value = initData
    }

    /**
     * ピクチャーインピクチャー用の座標をセットする
     *
     * @param rect 座標
     */
    fun setPictureInPictureRect(rect: Rect) {
        _pictureInPictureRect.value = rect
    }

    /**
     * ピクチャーインピクチャーの状態変化時に呼び出す
     *
     * @param isPictureInPicture ピクチャーインピクチャーならtrue
     */
    fun setPictureInPictureMode(isPictureInPicture: Boolean) {
        _pictureInPictureMode.value = isPictureInPicture
    }

}