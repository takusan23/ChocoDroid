package io.github.takusan23.chocodroid.viewmodel

import android.app.Application
import io.github.takusan23.chocodroid.database.db.FavoriteDB
import io.github.takusan23.chocodroid.tool.DownloadContentManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 動画一覧から開くボトムシートのメニューのViewModel
 *
 * DB操作とかをComposeと一緒に書いていいのかはわからん
 * */
class VideoListMenuScreenViewModel(application: Application) : BaseAndroidViewModel(application) {

    private val context = application.applicationContext

    /** ダウンロードコンテンツを管理するやつ */
    private val downloadContentManager by lazy { DownloadContentManager(context) }

    /** お気に入りDB */
    private val favoriteDB by lazy { FavoriteDB.getInstance(context) }

    /**
     * ダウンロードしたコンテンツを削除する
     *
     * @param videoId 動画ID
     * */
    suspend fun deleteDownloadContent(videoId: String) = withContext(errorHandler + Dispatchers.IO) {
        downloadContentManager.deleteContent(videoId)
    }

    /**
     * お気に入りから削除する
     *
     * @param videoId 動画ID
     * @param folderId フォルダーID
     * */
    suspend fun deleteFavoriteVideoItem(videoId: String, folderId: Int) = withContext(errorHandler + Dispatchers.IO) {
        favoriteDB.favoriteDao().deleteVideoItem(folderId = folderId, videoId = videoId)
    }

    /**
     * ダウンロードしたコンテンツを端末のギャラリー（Videoフォルダ）へコピーする
     *
     * 音声の場合はMusicフォルダに入る
     *
     * MediaStore API を使う
     *
     * @param videoId 動画ID
     * */
    suspend fun copyFileToVideoOrMusicFolder(videoId: String) = withContext(errorHandler + Dispatchers.IO) {
        downloadContentManager.copyFileToVideoOrMusicFolder(videoId)
    }

}