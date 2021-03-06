package io.github.takusan23.chocodroid.database.dao

import androidx.room.*
import io.github.takusan23.chocodroid.database.entity.DownloadContentDBEntity
import kotlinx.coroutines.flow.Flow

/**
 * データベースへアクセスする関数たち
 *
 * 基本は[io.github.takusan23.chocodroid.tool.DownloadContentManager]クラス経由で触ると思う
 * */
@Dao
interface DownloadContentDBDao {

    /** Flowで全部取得。順番は追加日時順 */
    @Query("SELECT * FROM download_content_db ORDER BY insert_date DESC")
    fun flowGetAll(): Flow<List<DownloadContentDBEntity>>

    /** 追加 */
    @Insert
    suspend fun insert(downloadContentDBEntity: DownloadContentDBEntity)

    /** 削除 */
    @Delete
    suspend fun delete(downloadContentDBEntity: DownloadContentDBEntity)

    /** 更新 */
    @Update
    suspend fun update(downloadContentDBEntity: DownloadContentDBEntity)

    /** 動画IDを指定して取得 */
    @Query("SELECT * FROM download_content_db WHERE video_id = :videoId")
    suspend fun getDownloadContentDataFromVideoId(videoId: String): DownloadContentDBEntity

    /** データベース上に存在するか */
    @Query("SELECT EXISTS (SELECT * FROM download_content_db WHERE video_id = :videoId)")
    suspend fun existsDataFromDownloadContentDB(videoId: String): Boolean

    /** 動画IDを利用してデータベースから項目を削除 */
    @Query("DELETE FROM download_content_db WHERE video_id = :videoId")
    suspend fun deleteFromVideoId(videoId: String)

    /**
     * 動画タイトル部分一致検索して配列を返す
     *
     * @param search 検索ワード
     * @param limit 上限
     * */
    @Query("""
        SELECT * FROM download_content_db
            WHERE video_title LIKE '%' || :search || '%' 
            ORDER BY update_date DESC
            LIMIT :limit
    """)
    fun flowTitleSearch(search: String, limit: Int = 5): Flow<List<DownloadContentDBEntity>>

}