package io.github.takusan23.chocodroid.database.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import io.github.takusan23.chocodroid.database.dao.FavoriteDBDao
import io.github.takusan23.chocodroid.database.entity.FavoriteFolderDBEntity
import io.github.takusan23.chocodroid.database.entity.FavoriteVideoDBEntity

/**
 * お気に入りデータベース
 * */
@Database(entities = [FavoriteFolderDBEntity::class, FavoriteVideoDBEntity::class], version = 1)
abstract class FavoriteDB : RoomDatabase() {

    abstract fun favoriteDao(): FavoriteDBDao

    companion object {

        private var favoriteDB: FavoriteDB? = null

        /**
         * データベースのインスタンスを返す
         * @param context [Context]
         * @return データベースのインスタンス
         * */
        fun getInstance(context: Context): FavoriteDB {
            if (favoriteDB == null) {
                favoriteDB = Room.databaseBuilder(context, FavoriteDB::class.java, "favorite_db.db").build()
            }
            return favoriteDB!!
        }
    }

}