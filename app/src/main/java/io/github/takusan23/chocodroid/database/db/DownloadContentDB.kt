package io.github.takusan23.chocodroid.database.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import io.github.takusan23.chocodroid.database.dao.DownloadContentDBDao
import io.github.takusan23.chocodroid.database.entity.DownloadContentDBEntity

/**
 * ダウンロードしたファイル情報データベース
 * */
@Database(entities = [DownloadContentDBEntity::class], version = 1)
abstract class DownloadContentDB : RoomDatabase() {
    abstract fun downloadContentDao(): DownloadContentDBDao

    companion object {

        private var downloadContentDB: DownloadContentDB? = null

        /**
         * データベースのインスタンスを返します
         *
         * @param context Context
         * @return データベースのインスタンス
         * */
        fun getInstance(context: Context): DownloadContentDB {
            if (downloadContentDB == null) {
                downloadContentDB = Room.databaseBuilder(context, DownloadContentDB::class.java, "download_content_db.db").build()
            }
            return downloadContentDB!!
        }

    }

}