package io.github.takusan23.chocodroid.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.github.takusan23.chocodroid.tool.StacktraceToString
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * エラー送信FlowとかCoroutineExceptionHandlerとかほとんどのViewModelで共通して使うやつ
 *
 * 各ViewModelはこれを継承するといいかも
 * */
open class BaseAndroidViewModel(application: Application) : AndroidViewModel(application) {

    protected val _isLoadingFlow = MutableStateFlow(false)
    private val _errorMessageFlow = MutableStateFlow<String?>(null)

    /** コルーチン起動時の引数に指定してね。例外を捕まえ、Flowに流します */
    protected val errorHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        _errorMessageFlow.value = StacktraceToString.stackTraceToString(throwable)
        _isLoadingFlow.value = false
    }

    /** 読み込み中？ */
    val isLoadingFlow = _isLoadingFlow.asStateFlow()

    /** エラーメッセージ送信用Flow */
    val errorMessageFlow = _errorMessageFlow.asStateFlow()

}