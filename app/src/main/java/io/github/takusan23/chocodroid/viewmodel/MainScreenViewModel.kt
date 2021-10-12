package io.github.takusan23.chocodroid.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.github.takusan23.htmlparse.data.WatchPageResponseData
import io.github.takusan23.htmlparse.html.WatchPageHTML
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * [io.github.takusan23.chocodroid.MainActivity]で使うViewModel
 *
 * Composeでも使います（画面回転しんどいので）
 * */
class MainScreenViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext

    private val _watchPageResponseData = MutableStateFlow<WatchPageResponseData?>(null)
    private val _isLoadingFlow = MutableStateFlow(false)
    private val _errorMessageFlow = MutableStateFlow<String?>(null)

    /** コルーチン起動時の引数に指定してね。例外を捕まえ、Flowに流します */
    private val errorHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        viewModelScope.launch {
            _errorMessageFlow.emit(throwable.message)
            _isLoadingFlow.emit(false)
        }
    }

    /** 動画情報データクラスを保持するFlow。外部公開用は受け取りのみ */
    val watchPageResponseDataFlow = _watchPageResponseData as Flow<WatchPageResponseData?>

    /** 読み込み中？ */
    val isLoadingFlow = _isLoadingFlow as Flow<Boolean>

    /** エラーメッセージ送信用Flow */
    val errorMessageFlow = _errorMessageFlow as Flow<String?>

    /**
     * ようつべ視聴ページを読み込む。レスポンスはflowに流れます
     *
     * 失敗しても失敗用Flowに流れます
     *
     * @param videoId 動画ID
     * */
    fun loadWatchPage(videoId: String) {
        // 再生中なら中止
        closePlayer()
        // 叩く
        viewModelScope.launch(errorHandler + Dispatchers.Default) {
            _isLoadingFlow.emit(true)
            // HTML解析とURL（復号処理含めて）取得
            val watchPageResponseData = WatchPageHTML.getWatchPage(videoId)
            _watchPageResponseData.emit(watchPageResponseData)
            _isLoadingFlow.emit(false)
        }
    }


    /**
     * プレイヤー終了。データクラス保持Flowにnullを入れます
     * */
    fun closePlayer() {
        viewModelScope.launch { _watchPageResponseData.emit(null) }
    }

}