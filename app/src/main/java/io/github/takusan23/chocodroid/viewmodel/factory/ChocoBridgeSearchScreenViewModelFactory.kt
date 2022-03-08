package io.github.takusan23.chocodroid.viewmodel.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.takusan23.chocodroid.viewmodel.ChocoBridgeSearchScreenViewModel

/**
 * 検索入力画面で使うViewModelを作成するクラス
 *
 * @param searchWord 検索ワード
 * */
class ChocoBridgeSearchScreenViewModelFactory(private val application: Application, private val searchWord: String) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ChocoBridgeSearchScreenViewModel(application, searchWord) as T
    }

}
