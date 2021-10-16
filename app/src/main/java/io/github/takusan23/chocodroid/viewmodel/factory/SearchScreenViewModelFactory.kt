package io.github.takusan23.chocodroid.viewmodel.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.takusan23.chocodroid.viewmodel.SearchScreenViewModel
import io.github.takusan23.htmlparse.html.SearchAPI

/**
 * SearchScreenViewModelへ引数を渡すためのクラス
 *
 * @param application application
 * @param query 検索ワード
 * @param sort ソート。[SearchAPI.PARAMS_SORT_RELEVANCE]など参照
 * */
class SearchScreenViewModelFactory(private val application: Application, private val query: String, private val sort: String = SearchAPI.PARAMS_SORT_RELEVANCE) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SearchScreenViewModel(application, query, sort) as T
    }

}
