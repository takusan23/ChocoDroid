package io.github.takusan23.chocodroid.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * お気に入り動画をまとめておくフォルダ的なやつのテーブル
 *
 * @param id 主キー
 * @param folderName フォルダ名
 * @param insertDate 追加日時
 * @param updateDate 更新日時
 * */
@Entity(tableName = "favorite_folder_table")
data class FavoriteFolderDBEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "folder_name") val folderName: String,
    @ColumnInfo(name = "insert_date") val insertDate: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "update_date") val updateDate: Long = System.currentTimeMillis(),
)