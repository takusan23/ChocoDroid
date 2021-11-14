package io.github.takusan23.chocodroid.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.github.takusan23.chocodroid.database.db.FavoriteDB
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * お気に入りフォルダ一覧画面で使うViewModel
 * */
class FavoriteFolderScreenViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext

    /** お気に入りDB */
    private val favoriteDB = FavoriteDB.getInstance(context)

    /** お気に入りフォルダ一覧をFlowで返す */
    val favoriteFolderList = favoriteDB.favoriteDao().getAllFavVideoFolder()

    init {
        viewModelScope.launch {
            favoriteDB.favoriteDao().getFavoriteFolderAndVideoListMap().collect {
                println(it.keys)
                println(it.values)
                println("----")
            }
        }
    }

}