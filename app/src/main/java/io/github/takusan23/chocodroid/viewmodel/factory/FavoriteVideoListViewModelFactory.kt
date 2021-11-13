package io.github.takusan23.chocodroid.viewmodel.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.takusan23.chocodroid.viewmodel.FavoriteVideoListViewModel

/**
 * [FavoriteVideoListViewModel]へ値を渡すためのファクトリークラス
 *
 * @param folderId フォルダID
 * */
class FavoriteVideoListViewModelFactory(private val application: Application, private val folderId: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FavoriteVideoListViewModel(application, folderId) as T
    }
}