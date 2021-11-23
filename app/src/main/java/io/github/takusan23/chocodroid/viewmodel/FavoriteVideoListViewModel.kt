package io.github.takusan23.chocodroid.viewmodel

import android.app.Application
import io.github.takusan23.chocodroid.database.db.FavoriteDB
import io.github.takusan23.chocodroid.database.entity.FavoriteFolderDBEntity
import kotlinx.coroutines.flow.map

/**
 * お気に入りフォルダ内の動画を表示する画面で使うViewModel
 *
 * @param folderId フォルダID。[FavoriteFolderDBEntity.id]
 * */
class FavoriteVideoListViewModel(application: Application, folderId: Int) : BaseAndroidViewModel(application) {

    private val context = application.applicationContext

    /** お気に入りデータベース */
    private val favoriteDB = FavoriteDB.getInstance(context)

    /** お気に入りフォルダ内の動画をFlowでもらう */
    val folderVideoList = favoriteDB.favoriteDao().getFavFolderVideoFromFolderId(folderId)
        .map { it.map { it.convertToCommonVideoData() } }

    /** フォルダの情報をFlowでもらう */
    val folderInfo = favoriteDB.favoriteDao().getFolderInfoFromFolderId(folderId)

}