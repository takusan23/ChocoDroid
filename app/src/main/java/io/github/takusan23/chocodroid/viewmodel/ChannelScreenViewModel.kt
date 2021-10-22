package io.github.takusan23.chocodroid.viewmodel

import android.app.Application
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.github.takusan23.chocodroid.setting.SettingKeyObject
import io.github.takusan23.chocodroid.setting.dataStore
import io.github.takusan23.internet.api.ChannelAPI
import io.github.takusan23.internet.data.channel.ChannelResponseData
import io.github.takusan23.internet.data.channel.GridVideoRenderer
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * チャンネル画面で使うViewModel
 *
 * @param channelId チャンネルID
 * */
class ChannelScreenViewModel(application: Application, private val channelId: String) : AndroidViewModel(application) {

    private val context = application.applicationContext

    private val _uploadVideoListFlow = MutableStateFlow<List<GridVideoRenderer>>(emptyList())
    private val _channelResponseData = MutableStateFlow<ChannelResponseData?>(null)
    private val _isLoadingFlow = MutableStateFlow(false)
    private val _errorMessageFlow = MutableStateFlow<String?>(null)

    /** チャンネルAPI */
    private val channelAPI = ChannelAPI()

    /** これ以上追加読み込みしない */
    private var isEOL = false

    /** 検索が失敗したときに例外を拾う */
    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        _errorMessageFlow.value = throwable.message
        _isLoadingFlow.value = false
    }

    /** チャンネル情報 */
    val channelResponseDataFlow = _channelResponseData as StateFlow<ChannelResponseData?>

    /** 投稿動画 */
    val uploadVideoListFlow = _uploadVideoListFlow as StateFlow<List<GridVideoRenderer>>

    /** 読み込み中？ */
    val isLoadingFlow = _isLoadingFlow as StateFlow<Boolean>

    /** エラー出たら呼ばれます */
    val errorMessageFlow = _errorMessageFlow as StateFlow<String?>

    init {
        viewModelScope.launch(errorHandler + Dispatchers.Default) {
            // DataStoreにAPIキーが保存されているかも
            val setting = context.dataStore.data.first()
            val apiKey = setting[SettingKeyObject.API_KEY]
            // 初期化
            channelAPI.init(apiKey)
            // 投稿動画取得
            getUploadVideo()
        }
        // APIキーを保存しておく
        viewModelScope.launch {
            channelAPI.apiKeyFlow.collect { apiKey ->
                if (apiKey != null) {
                    context.dataStore.edit { setting -> setting[SettingKeyObject.API_KEY] = apiKey }
                }
            }
        }
    }

    /** チャンネル投稿動画再取得 */
    fun getReGetUploadVideo() {
        viewModelScope.launch(errorHandler + Dispatchers.Default) {
            getUploadVideo()
        }
    }

    /**
     * チャンネル投稿動画を取得する
     *
     * 結果はFlowに流れます。
     * */
    private suspend fun getUploadVideo() {
        _isLoadingFlow.value = true
        _channelResponseData.value = channelAPI.getChannelUploadVideo(channelId)
        _uploadVideoListFlow.value = _channelResponseData.value!!.getVideoList() ?: emptyList()
        _isLoadingFlow.value = false
    }

    /**
     * チャンネル投稿動画を追加読み込みする
     *
     * もう追加読み込み出来ない場合と読み込み中の場合は何もしません。
     * */
    fun moreUploadVideoLoad() {
        viewModelScope.launch(errorHandler + Dispatchers.Default) {
            // これ以上読み込まない場合 or 追加読み込み中ならreturn
            if (isEOL || _isLoadingFlow.value) return@launch

            _isLoadingFlow.value = true
            val moreSearchResult = channelAPI.moreChannelUploadVideo()
            if (moreSearchResult.isNotEmpty()) {
                // 連結させる。というかディープ（意味深）コピーしないとFlowに差分検知が行かない。参照渡しとかそのへんの話だと思う
                _uploadVideoListFlow.value = (_uploadVideoListFlow.value + moreSearchResult)
            } else {
                // もう読み込まない
                isEOL = true
            }
            _isLoadingFlow.value = false
        }
    }

}