package io.github.takusan23.chocodroid.database.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import io.github.takusan23.chocodroid.database.dao.FavoriteChDBDao
import io.github.takusan23.chocodroid.database.entity.FavoriteChDBEntity

/**
 * お気に入りチャンネル！！！！データベース
 * */
@Database(entities = [FavoriteChDBEntity::class], version = 1)
abstract class FavoriteChDB : RoomDatabase() {

    abstract fun favoriteChDao(): FavoriteChDBDao

    companion object {

        private var favoriteChDB: FavoriteChDB? = null

        /**
         * データベースのインスタンスを返す
         * @param context [Context]
         * @return データベースのインスタンス
         * */
        fun getInstance(context: Context): FavoriteChDB {
            if (favoriteChDB == null) {
                favoriteChDB = Room.databaseBuilder(context, FavoriteChDB::class.java, "favorite_ch_db.db").build()
            }
            return favoriteChDB!!
        }
    }

}