package io.github.takusan23.chocodroid.viewmodel.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.takusan23.chocodroid.viewmodel.SearchScreenViewModel

/**
 * SearchScreenViewModelへ引数を渡すためのクラス
 *
 * @param application application
 * @param query 検索ワード
 * */
class SearchScreenViewModelFactory(private val application: Application, private val query: String) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SearchScreenViewModel(application, query) as T
    }

}
