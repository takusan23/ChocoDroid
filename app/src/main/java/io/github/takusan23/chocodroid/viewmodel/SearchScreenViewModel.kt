package io.github.takusan23.chocodroid.viewmodel

import android.app.Application
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.github.takusan23.chocodroid.setting.SettingKeyObject
import io.github.takusan23.chocodroid.setting.dataStore
import io.github.takusan23.internet.data.search.VideoContent
import io.github.takusan23.internet.api.SearchAPI
import io.github.takusan23.internet.data.CommonVideoData
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 検索結果で使うViewModel
 * */
class SearchScreenViewModel(application: Application, private val query: String, private val sort: String) : AndroidViewModel(application) {
    private val context = application.applicationContext

    /** 非公式検索APIを叩く */
    private val searchAPI = SearchAPI()

    private val _searchResultListFlow = MutableStateFlow<List<CommonVideoData>>(listOf())
    private val _isLoadingFlow = MutableStateFlow(false)
    private val _errorMessageFlow = MutableStateFlow<String?>(null)
    private val _queryFlow = MutableStateFlow(query)
    private val _sortFlow = MutableStateFlow(sort)

    /** これ以上追加読み込みしない */
    private var isEOL = false

    /** 検索が失敗したときに例外を拾う */
    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        _errorMessageFlow.value = throwable.message
        _isLoadingFlow.value = false
    }

    /** 検索結果 */
    val searchResultListFlow = _searchResultListFlow as StateFlow<List<CommonVideoData>>

    /** 読み込み中？ */
    val isLoadingFlow = _isLoadingFlow as StateFlow<Boolean>

    /** エラー出たら呼ばれます */
    val errorMessageFlow = _errorMessageFlow as StateFlow<String?>

    /** 検索ワード */
    val queryFlow = _queryFlow as StateFlow<String>

    /** 並び順 */
    val sortFlow = _sortFlow as StateFlow<String>

    init {
        viewModelScope.launch(errorHandler + Dispatchers.Default) {
            // DataStoreにAPIキーが保存されているかも
            val setting = context.dataStore.data.first()
            val apiKey = setting[SettingKeyObject.API_KEY]
            // 初期化
            searchAPI.init(apiKey)
            // 検索する
            search(query, sort)
        }
        // APIキーを保存しておく
        viewModelScope.launch {
            searchAPI.apiKeyFlow.collect { apiKey ->
                if (apiKey != null) {
                    context.dataStore.edit { setting -> setting[SettingKeyObject.API_KEY] = apiKey }
                }
            }
        }
    }

    /** 再検索をする */
    fun reSearch() {
        viewModelScope.launch(errorHandler + Dispatchers.Default) {
            search(_queryFlow.value, _sortFlow.value)
        }
    }

    /**
     * 検索する関数。検索ワードやソート条件が変わったときは呼び直してください
     *
     * 結果はFlow、失敗してもFlowに流します。
     *
     * @param query 検索ワード
     * @param sort ソート。[SearchAPI.PARAMS_SORT_RELEVANCE]など
     * */
    private suspend fun search(query: String, sort: String = SearchAPI.PARAMS_SORT_RELEVANCE) = withContext(errorHandler) {
        _isLoadingFlow.value = true
        _searchResultListFlow.value = searchAPI.search(query, sort) ?: listOf()
        _isLoadingFlow.value = false
    }

    /**
     * 追加読み込みする際は呼んでください。
     *
     * 結果はFlowに流れます。
     * */
    fun moreLoad() {
        viewModelScope.launch(errorHandler + Dispatchers.Default) {
            // これ以上読み込まない場合 or 追加読み込み中ならreturn
            if (isEOL || _isLoadingFlow.value) return@launch

            _isLoadingFlow.value = true
            val moreSearchResult = searchAPI.moreSearch() ?: listOf()
            if (moreSearchResult.isNotEmpty()) {
                // 連結させる。というかディープ（意味深）コピーしないとFlowに差分検知が行かない。参照渡しとかそのへんの話だと思う
                _searchResultListFlow.value = (_searchResultListFlow.value + moreSearchResult)
            } else {
                // もう読み込まない
                isEOL = true
            }
            _isLoadingFlow.value = false
        }
    }

    /**
     * 検索ワードをセット
     * @param query 検索ワード
     * */
    fun setQuery(query: String) {
        _queryFlow.value = query
    }

    /**
     * ソート条件をセット
     * @param sort [SearchAPI.PARAMS_SORT_REVIEW]など
     * */
    fun setSort(sort: String) {
        _sortFlow.value = sort
    }

}