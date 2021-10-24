package io.github.takusan23.chocodroid.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * データベースに突っ込む内容
 *
 * @param id 主キー
 * @param videoId 動画ID
 * @param title 動画タイトル
 * @param publishedDate 投稿日時
 * @param localWatchCount ローカルでの視聴回数？
 * @param ownerName 投稿者の名前
 * @param thumbnailUrl サムネ（意味深）URL
 * @param duration 再生時間
 * @param insertDate 追加したときの日付を入れてください。System.currentTimeMillisを入れてください
 * @param updateDate 更新したときの日付。UnixTimeのミリ秒
 * */
@Entity(tableName = "history_db")
data class HistoryDBEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "video_id") val videoId: String,
    @ColumnInfo(name = "insert_date") val insertDate: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "update_date") val updateDate: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "published_date") val publishedDate: String,
    @ColumnInfo(name = "local_watch_count") val localWatchCount: Int = 1,
    @ColumnInfo(name = "owner_name") val ownerName: String,
    @ColumnInfo(name = "thumbnail_url") val thumbnailUrl: String,
    @ColumnInfo(name = "duration") val duration: String,
)