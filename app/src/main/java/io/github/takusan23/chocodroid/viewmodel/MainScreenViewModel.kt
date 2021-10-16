package io.github.takusan23.chocodroid.viewmodel

import android.app.Application
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.github.takusan23.chocodroid.setting.SettingKeyObject
import io.github.takusan23.chocodroid.setting.dataStore
import io.github.takusan23.htmlparse.html.WatchPageHTML
import io.github.takusan23.htmlparse.data.watchpage.WatchPageData
import io.github.takusan23.htmlparse.magic.AlgorithmSerializer
import io.github.takusan23.htmlparse.magic.data.AlgorithmFuncNameData
import io.github.takusan23.htmlparse.magic.data.AlgorithmInvokeData
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * [io.github.takusan23.chocodroid.MainActivity]で使うViewModel
 *
 * Composeでも使います（画面回転しんどいので）
 * */
class MainScreenViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext

    private val _watchPageResponseData = MutableStateFlow<WatchPageData?>(null)
    private val _isLoadingFlow = MutableStateFlow(false)
    private val _errorMessageFlow = MutableStateFlow<String?>(null)

    /** コルーチン起動時の引数に指定してね。例外を捕まえ、Flowに流します */
    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        _errorMessageFlow.value = throwable.message
        _isLoadingFlow.value = false
    }

    /** ローカルに保持してる解析アルゴリズム。flowで更新されるはず */
    private var localAlgorithmData: Triple<String?, AlgorithmFuncNameData?, List<AlgorithmInvokeData>?>? = null

    /** 動画情報データクラスを保持するFlow。外部公開用は受け取りのみ */
    val watchPageResponseDataFlow = _watchPageResponseData as Flow<WatchPageData?>

    /** 読み込み中？ */
    val isLoadingFlow = _isLoadingFlow as Flow<Boolean>

    /** エラーメッセージ送信用Flow */
    val errorMessageFlow = _errorMessageFlow as Flow<String?>

    init {
        viewModelScope.launch {
            // データを集める
            context.dataStore.data.collect { setting ->
                val baseJsURL = setting[SettingKeyObject.WATCH_PAGE_BASE_JS_URL]
                val funcNameData = AlgorithmSerializer.toData<AlgorithmFuncNameData?>(setting[SettingKeyObject.WATCH_PAGE_JS_FUNC_NAME_JSON])
                val funcInvokeDataList = AlgorithmSerializer.toData<List<AlgorithmInvokeData>>(setting[SettingKeyObject.WATCH_PAGE_JS_INVOKE_LIST_JSON])
                localAlgorithmData = Triple(baseJsURL, funcNameData, funcInvokeDataList)
            }
        }
    }

    /**
     * ようつべ視聴ページを読み込む。レスポンスはflowに流れます
     *
     * 失敗しても失敗用Flowに流れます
     *
     * @param videoId 動画ID
     * */
    fun loadWatchPage(videoId: String) {
        // 叩く
        viewModelScope.launch(errorHandler + Dispatchers.Default) {
            _isLoadingFlow.value = true

            // HTML解析とURL（復号処理含めて）取得
            val watchPageResponseData = WatchPageHTML.getWatchPage(videoId, localAlgorithmData?.first, localAlgorithmData?.second, localAlgorithmData?.third)

            // View（Compose）にデータを渡す
            _watchPageResponseData.value = watchPageResponseData
            _isLoadingFlow.value = false

            // 2回目以降のアルゴリズムの解析をスキップするために解読情報を永続化する
            context.dataStore.edit { setting ->
                setting[SettingKeyObject.WATCH_PAGE_BASE_JS_URL] = watchPageResponseData.baseJsURL
                setting[SettingKeyObject.WATCH_PAGE_JS_FUNC_NAME_JSON] = AlgorithmSerializer.toJSON(watchPageResponseData.algorithmFuncNameData)
                setting[SettingKeyObject.WATCH_PAGE_JS_INVOKE_LIST_JSON] = AlgorithmSerializer.toJSON(watchPageResponseData.decryptInvokeList)
            }

        }
    }


    /**
     * プレイヤー終了。データクラス保持Flowにnullを入れます
     * */
    fun closePlayer() {
        viewModelScope.launch { _watchPageResponseData.value = null }
    }

}