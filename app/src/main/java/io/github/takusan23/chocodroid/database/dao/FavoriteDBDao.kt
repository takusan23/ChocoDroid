package io.github.takusan23.chocodroid.database.dao

import androidx.room.*
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

    /** フォルダIDから[FavoriteVideoDBEntity]を作成する */
    @Query("SELECT * FROM favorite_video_table WHERE folder_id = :folderId")
    fun getFavFolderVideoFromFolderId(folderId: Int): Flow<List<FavoriteVideoDBEntity>>

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
     * @param folderId フォルダID
     * */
    @Query("DELETE FROM favorite_folder_table WHERE id = :folderId")
    suspend fun deleteFavoriteFolderFromFolderId(folderId: Int)

}