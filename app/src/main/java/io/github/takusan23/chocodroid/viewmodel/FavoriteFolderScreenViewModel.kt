package io.github.takusan23.chocodroid.viewmodel

import android.app.Application
import io.github.takusan23.chocodroid.database.db.FavoriteChDB
import io.github.takusan23.chocodroid.database.db.FavoriteDB
import io.github.takusan23.chocodroid.database.entity.FavoriteFolderDBEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

/**
 * お気に入りフォルダ一覧画面で使うViewModel
 * */
class FavoriteFolderScreenViewModel(application: Application) : BaseAndroidViewModel(application) {

    private val context = application.applicationContext

    /** お気に入りDB */
    private val favoriteDB = FavoriteDB.getInstance(context)

    /** お気に入りチャンネルDB */
    private val favoriteChDB = FavoriteChDB.getInstance(context)

    /** お気に入りフォルダ一覧をFlowで返す */
    val favoriteFolderList = favoriteDB.favoriteDao().getAllFavVideoFolder()

    /** フォルダの名前を入れた動画データクラス */
    val favoriteFolderVideoMap = favoriteDB.favoriteDao().flowGetFavoriteFolderAndVideoListMap()
        .map { it.groupBy { FavoriteFolderDBEntity(it.folderId, it.folderName, 0, 0) } } // ここ雑
        .flowOn(Dispatchers.Default)

    /** お気に入りチャンネルの一覧をFlowで流す */
    val favoriteChList = favoriteChDB.favoriteChDao().flowGetAll()

}