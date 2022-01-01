package io.github.takusan23.chocodroid.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.github.takusan23.chocodroid.database.db.FavoriteDB
import io.github.takusan23.chocodroid.database.entity.FavoriteVideoDBEntity
import io.github.takusan23.internet.data.CommonVideoData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Composeの中にお気に入り登録のためのデータベースの処理書くのはなんかなーって感じなのでViewModelに書く
 * */
class AddVideoToFavoriteFolderViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext

    /** お気に入りDB */
    private val favoriteDB = FavoriteDB.getInstance(context)

    /** フォルダ一覧を取得 */
    val favoriteFolderList = favoriteDB.favoriteDao().getAllFavVideoFolder()

    /**
     * 動画をお気に入りフォルダへ登録する
     *
     * @param folderId フォルダID
     * @param commonVideoData 登録する動画情報
     * @return 登録したらtrue。登録済みならfalse
     * */
    suspend fun addVideoToFavoriteFolder(
        folderId: Int,
        commonVideoData: CommonVideoData,
    ): Boolean = withContext(Dispatchers.IO) {
        // データベース
        val favoriteDB = FavoriteDB.getInstance(context)
        // 既に追加済み？
        val isExists = favoriteDB.favoriteDao().isExistsVideoItemFromFolderId(folderId, commonVideoData.videoId)
        if (isExists) return@withContext false
        // 追加
        favoriteDB.favoriteDao().insert(FavoriteVideoDBEntity(
            folderId = folderId,
            videoId = commonVideoData.videoId,
            title = commonVideoData.videoTitle,
            thumbnailUrl = commonVideoData.thumbnailUrl,
            publishedDate = commonVideoData.publishDate!!,
            ownerName = commonVideoData.ownerName,
            insertDate = System.currentTimeMillis(),
            duration = commonVideoData.duration!!,
        ))
        return@withContext true
    }

}