package io.github.takusan23.chocodroid.database.dao

import androidx.room.*
import io.github.takusan23.chocodroid.database.entity.FavoriteConcatData
import io.github.takusan23.chocodroid.database.entity.FavoriteFolderDBEntity
import io.github.takusan23.chocodroid.database.entity.FavoriteVideoDBEntity
import kotlinx.coroutines.flow.Flow

/**
 * お気に入り動画データベース
 *
 * Flowで受け取れるの便利～
 * */
@Dao
interface FavoriteDBDao {

    /** [FavoriteFolderDBEntity]を全部返す */
    @Query("SELECT * FROM favorite_folder_table")
    fun getAllFavVideoFolder(): Flow<List<FavoriteFolderDBEntity>>

    /**
     * フォルダIDから[FavoriteVideoDBEntity]を作成する
     *
     * @param folderId フォルダID
     * */
    @Query("SELECT * FROM favorite_video_table WHERE folder_id = :folderId ORDER BY insert_date DESC")
    fun getFavFolderVideoFromFolderId(folderId: Int): Flow<List<FavoriteVideoDBEntity>>

    /**
     * フォルダの名前などを取得する。Flowで
     *
     * @param folderId フォルダID
     * */
    @Query("SELECT * FROM favorite_folder_table WHERE id = :folderId")
    fun flowGetFolderInfoFromFolderId(folderId: Int): Flow<FavoriteFolderDBEntity>

    /** [FavoriteFolderDBEntity]追加 */
    @Insert
    suspend fun insert(favoriteFolderDBEntity: FavoriteFolderDBEntity)

    /** [FavoriteVideoDBEntity]追加 */
    @Insert
    suspend fun insert(favoriteVideoDBEntity: FavoriteVideoDBEntity)

    /** [FavoriteVideoDBEntity]削除 */
    @Delete
    suspend fun delete(favoriteVideoDBEntity: FavoriteVideoDBEntity)

    /** [FavoriteFolderDBEntity]削除 */
    @Delete
    suspend fun delete(favoriteFolderDBEntity: FavoriteFolderDBEntity)

    /** [FavoriteVideoDBEntity]更新 */
    @Update
    suspend fun update(favoriteVideoDBEntity: FavoriteVideoDBEntity)

    /** [FavoriteFolderDBEntity]更新 */
    @Update
    suspend fun update(favoriteFolderDBEntity: FavoriteFolderDBEntity)

    /**
     * フォルダIDを利用してフォルダを削除する
     *
     * @param folderId フォルダID
     * */
    @Query("DELETE FROM favorite_folder_table WHERE id = :folderId")
    suspend fun deleteFavoriteFolderFromFolderId(folderId: Int)

    /**
     * 指定したフォルダIDの動画を削除する
     *
     * @param folderId フォルダID
     * */
    @Query("DELETE FROM favorite_video_table WHERE folder_id = :folderId")
    suspend fun deleteVideoListFromFolderId(folderId: Int)

    /**
     * フォルダから動画を削除する
     *
     * @param folderId 削除する動画があるフォルダ
     * @param videoId 動画ID
     * */
    @Query("DELETE FROM favorite_video_table WHERE folder_id = :folderId AND video_id = :videoId")
    suspend fun deleteVideoItem(folderId: Int, videoId: String)

    /**
     * フォルダIDを利用して既に動画を追加済かどうかを返す
     *
     * @param folderId フォルダID
     * @param videoId 動画ID
     * @return 追加済みの場合true
     * */
    @Query("SELECT EXISTS(SELECT * FROM favorite_video_table WHERE folder_id = :folderId AND video_id = :videoId)")
    suspend fun isExistsVideoItemFromFolderId(folderId: Int, videoId: String): Boolean

    /**
     * フォルダ一覧と、フォルダ内動画を5件ほど入れたデータクラスを返す。カルーセルUIを実装するときに使う
     *
     * @return Map。フォルダ情報とフォルダに保存されてる動画
     * */
    @RewriteQueriesToDropUnusedColumns
    @Query("""
        SELECT folder.folder_name as folder_name, folder.id as fav_folder_id, video.*
            FROM favorite_folder_table folder
            LEFT JOIN favorite_video_table video ON video.folder_id = folder.id
            ORDER BY folder.id ASC, video.insert_date DESC
    """)
    fun flowGetFavoriteFolderAndVideoListMap(): Flow<List<FavoriteConcatData>>

}