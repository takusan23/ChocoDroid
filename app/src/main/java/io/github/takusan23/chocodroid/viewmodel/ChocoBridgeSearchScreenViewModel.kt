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
 *
 * @param initialSearchWord 検索ワード
 * */
class ChocoBridgeSearchScreenViewModel(application: Application, initialSearchWord: String) : BaseAndroidViewModel(application) {
    private val context = application.applicationContext

    private val _searchSuggestList = MutableStateFlow<List<String>>(listOf())
    private val _searchWord = MutableStateFlow(initialSearchWord)

    /** 検索サジェスト結果を流すFlow */
    val searchSuggestList = _searchSuggestList as StateFlow<List<String>>

    /** 入力中テキスト */
    val searchWord = _searchWord as StateFlow<String>

    init {
        getSuggestWord(initialSearchWord)
    }

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

    /**
     * 検索ワードをセットする
     *
     * @param searchWord 検索ワード
     * */
    fun setSearchWord(searchWord: String) {
        _searchWord.value = searchWord
    }

}