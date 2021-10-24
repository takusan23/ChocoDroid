package io.github.takusan23.chocodroid.database.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import io.github.takusan23.chocodroid.database.dao.HistoryDBDao
import io.github.takusan23.chocodroid.database.entity.HistoryDBEntity

/**
 * 履歴データベース
 * */
@Database(entities = [HistoryDBEntity::class], version = 1)
abstract class HistoryDB : RoomDatabase() {
    abstract fun historyDao(): HistoryDBDao

    companion object {

        private var historyDB: HistoryDB? = null

        /**
         * データベースのインスタンスはシングルトンにする必要があるため
         * @param context Context
         * @return
         * */
        fun getInstance(context: Context): HistoryDB {
            if (historyDB == null) {
                historyDB = Room.databaseBuilder(context, HistoryDB::class.java, "history_db.db").build()
            }
            return historyDB!!
        }

    }

}