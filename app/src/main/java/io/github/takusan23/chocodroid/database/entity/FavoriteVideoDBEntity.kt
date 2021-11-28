package io.github.takusan23.chocodroid.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.takusan23.internet.data.CommonVideoData

/**
 * お気に入り動画をいれるテーブル
 *
 * @param id 主キー
 * @param videoId 動画ID
 * @param folderId お気に入りフォルダデータベースの主キー
 * @param insertDate 追加日時
 * @param title 動画タイトル
 * @param publishedDate どうか投稿日時
 * @param thumbnailUrl サムネイルURL
 * @param ownerName 投稿者：変態糞土方
 * @param duration 動画時間
 * */
@Entity(tableName = "favorite_video_table")
data class FavoriteVideoDBEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "folder_id") val folderId: Int,
    @ColumnInfo(name = "video_id") val videoId: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "published_date") val publishedDate: String,
    @ColumnInfo(name = "owner_name") val ownerName: String,
    @ColumnInfo(name = "thumbnail_url") val thumbnailUrl: String,
    @ColumnInfo(name = "duration") val duration: String,
    @ColumnInfo(name = "insert_date") val insertDate: Long = System.currentTimeMillis(),
) {

    /**
     * [CommonVideoData]形式へ変換する
     *
     * @return [CommonVideoData]
     * */
    fun convertToCommonVideoData() = CommonVideoData(
        videoId = videoId,
        videoTitle = title,
        duration = duration,
        watchCount = "",
        publishDate = publishedDate,
        ownerName = ownerName,
        thumbnailUrl = thumbnailUrl,
    )

}