package io.github.takusan23.chocodroid.database.dao

import androidx.room.*
import io.github.takusan23.chocodroid.database.entity.HistoryDBEntity
import kotlinx.coroutines.flow.Flow

/**
 * データベースへアクセスする関数たち
 *
 * コルーチン以外でアクセスできないようにsuspend付けてます。
 *
 * suspend化するには「androidx.room:room-ktx」依存関係が必要です。
 * */
@Dao
interface HistoryDBDao {

    /** 全部取得 */
    @Query("SELECT * FROM history_db")
    suspend fun getAll(): List<HistoryDBEntity>

    /** Flowで全部取得。順番は追加日時順 */
    @Query("SELECT * FROM history_db ORDER BY insert_date DESC")
    fun flowGetAll(): Flow<List<HistoryDBEntity>>

    /** 追加 */
    @Insert
    suspend fun insert(historyDBEntity: HistoryDBEntity)

    /** 削除 */
    @Delete
    suspend fun delete(historyDBEntity: HistoryDBEntity)

    /** 更新 */
    @Update
    suspend fun update(historyDBEntity: HistoryDBEntity)

    /** 動画IDを指定して取得 */
    @Query("SELECT * FROM history_db WHERE video_id = :videoId")
    suspend fun getHistoryFromVideoId(videoId: String): HistoryDBEntity

    /**
     * 動画タイトルを部分一致検索してFlowで返す。更新日時の新しい順になります
     *
     * @param search 部分一致検索ワード
     * @param limit 上限
     * */
    @Query(
        value = """
        SELECT * FROM history_db WHERE title LIKE '%' || :search || '%' 
        ORDER BY update_date DESC
        LIMIT :limit
        """
    )
    fun flowTitleSearch(search: String, limit: Int = 5): Flow<List<HistoryDBEntity>>

    /**
     * 履歴DBに存在するか
     *
     * @param videoId 動画ID
     * @return 存在すればtrue
     * */
    @Query("SELECT EXISTS (SELECT * FROM history_db WHERE video_id = :videoId)")
    suspend fun existsHistoryData(videoId: String): Boolean

    /** 全件削除 */
    @Query("DELETE FROM history_db")
    suspend fun deleteAll()

}