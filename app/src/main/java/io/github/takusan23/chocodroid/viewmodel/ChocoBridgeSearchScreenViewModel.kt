package io.github.takusan23.chocodroid.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import io.github.takusan23.internet.api.SearchSuggestAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * 検索入力画面で使うViewModel
 * */
class ChocoBridgeSearchScreenViewModel(application: Application) : BaseAndroidViewModel(application) {
    private val context = application.applicationContext

    private val _searchSuggestList = MutableStateFlow<List<String>>(listOf())

    /** 検索サジェスト結果を流すFlow */
    val searchSuggestList = _searchSuggestList as StateFlow<List<String>>

    /**
     * 検索サジェストを取得する。結果は[searchSuggestList]に流れる。
     *
     * @param searchWord 検索ワード
     * */
    fun getSuggestWord(searchWord: String) {
        viewModelScope.launch(errorHandler + Dispatchers.Default) {
            if (searchWord.isEmpty()) {
                _searchSuggestList.value = listOf()
            } else {
                val suggestWordList = SearchSuggestAPI.getSuggestWord(searchWord)
                _searchSuggestList.value = suggestWordList
            }
        }
    }


}