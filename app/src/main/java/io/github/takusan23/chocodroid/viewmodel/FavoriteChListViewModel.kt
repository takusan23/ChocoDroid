package io.github.takusan23.chocodroid.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.github.takusan23.chocodroid.database.db.FavoriteChDB

/**
 * お気に入りチャンネル一覧で使うViewModel
 * */
class FavoriteChListViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext

    /** お気に入りチャンネルDB */
    private val favoriteChDB = FavoriteChDB.getInstance(context)

    /** お気に入りチャンネル一覧をFlowで流す */
    val favoriteChList = favoriteChDB.favoriteChDao().flowGetAll()

}