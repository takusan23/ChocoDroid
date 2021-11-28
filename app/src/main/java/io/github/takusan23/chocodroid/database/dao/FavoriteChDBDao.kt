package io.github.takusan23.chocodroid.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.github.takusan23.chocodroid.database.entity.FavoriteChDBEntity
import kotlinx.coroutines.flow.Flow

/**
 * お気に入りチャンネルDBへアクセスする関数たち
 * */
@Dao
interface FavoriteChDBDao {

    /** 追加 */
    @Insert
    suspend fun insert(favoriteChDBEntity: FavoriteChDBEntity)

    /** 取得 */
    @Query("SELECT * FROM favorite_ch_table")
    fun flowGetAll(): Flow<List<FavoriteChDBEntity>>

    /**
     * チャンネルIDを指定して削除
     *
     * @param channelId チャンネルID
     * */
    @Query("DELETE FROM favorite_ch_table WHERE channel_id = :channelId")
    suspend fun delete(channelId: String)

    /**
     * お気に入りチャンネル登録済みか
     *
     * @param channelId チャンネルID
     * @return 登録済みならtrue
     * */
    @Query("SELECT EXISTS(SELECT * FROM favorite_ch_table WHERE channel_id = :channelId)")
    fun isAddedDBFromChannelId(channelId: String): Flow<Boolean>

}