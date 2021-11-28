package io.github.takusan23.chocodroid.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * お気に入りチャンネルDBのテーブル
 *
 * @param id 主キー
 * @param channelId チャンネルのID
 * @param name チャンネル名
 * @param thumbnailUrl サムネURL
 * @param insertDate 追加日時
 * */
@Entity(tableName = "favorite_ch_table")
data class FavoriteChDBEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "channel_id") val channelId: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "thumbnail_url") val thumbnailUrl: String,
    @ColumnInfo(name = "insert_date") val insertDate: Long = System.currentTimeMillis(),
)