package io.github.takusan23.chocodroid.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.github.takusan23.chocodroid.database.db.FavoriteDB
import io.github.takusan23.chocodroid.database.entity.FavoriteFolderDBEntity

/**
 * お気に入りフォルダを追加するViewModel
 * */
class AddFavoriteFolderViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext

    /** お気に入りDB */
    private val favoriteDB = FavoriteDB.getInstance(context)

    /**
     * お気に入りフォルダを追加する
     *
     * @param folderName フォルダ名
     * */
    suspend fun addFolder(folderName: String) {
        val folderDBEntity = FavoriteFolderDBEntity(folderName = folderName)
        favoriteDB.favoriteDao().insert(folderDBEntity)
    }

}