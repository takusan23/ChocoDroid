package io.github.takusan23.chocodroid.database.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import io.github.takusan23.internet.data.CommonVideoData

/**
 * フォルダ名を入れた[FavoriteVideoDBEntity]
 *
 * fav_folder_idとかは結合するテーブルと名前がかぶってるので無理やり変えた
 *
 * @param folderName フォルダ名
 * @param folderId お気に入りフォルダデータベースの主キー
 * @param videoDBEntity お気に入りフォルダ内の動画データ。nullなのはフォルダが空っぽのときです
 * */
data class FavoriteConcatData(
    @ColumnInfo(name = "folder_name") val folderName: String,
    @ColumnInfo(name = "fav_folder_id") val folderId: Int,
    @Embedded val videoDBEntity: FavoriteVideoDBEntity?,
) {

    /**
     * [CommonVideoData]形式へ変換する。[videoDBEntity]がnullの場合はnullを返しますので
     *
     * @return [CommonVideoData]
     * */
    fun convertToCommonVideoData(): CommonVideoData? = videoDBEntity?.let {
        CommonVideoData(
            videoId = it.videoId,
            videoTitle = it.title,
            duration = it.duration,
            watchCount = "",
            publishDate = it.publishedDate,
            ownerName = it.ownerName,
            thumbnailUrl = it.thumbnailUrl,
        )
    }

}