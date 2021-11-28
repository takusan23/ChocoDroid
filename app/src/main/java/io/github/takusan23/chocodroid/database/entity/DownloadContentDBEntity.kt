package io.github.takusan23.chocodroid.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * ダウンロードコンテンツデータベースのテーブル
 *
 * 動画情報とかダウンロードして動画パスをDBに入れる。RoomならFlowで変更が取れる
 *
 * こいつは履歴とかお気に入りと違って[watchPageResponseJSON]を入れてるけど、動画再生時に動画情報をオフラインでも見れるようにするためです。
 *
 * @param id 主キー
 * @param videoId 動画ID
 * @param videoTitle 動画タイトル
 * @param watchPageInitialJSON 動画情報JSONです
 * @param watchPageResponseJSON 動画情報JSONその2です
 * @param contentPath 音声だけなら音声ファイルパス、動画あるなら動画のパスです
 * @param isAudio 音声のみの場合はtrue
 * @param insertDate 追加日時
 * @param updateDate 変更日時。[localWatchCount]更新時に更新してください
 * @param localWatchCount 視聴回数
 * @param lastWatchPos どこまで再生したか。続きから再生のために
 * @param thumbnailPath サムネイル画像のパス
 * */
@Entity(tableName = "download_content_db")
data class DownloadContentDBEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "video_id") val videoId: String,
    @ColumnInfo(name = "video_title") val videoTitle: String,
    @ColumnInfo(name = "watch_page_initial_json") val watchPageInitialJSON: String,
    @ColumnInfo(name = "watch_page_response_json") val watchPageResponseJSON: String,
    @ColumnInfo(name = "thumbnail_path") val thumbnailPath: String,
    @ColumnInfo(name = "content_path") val contentPath: String,
    @ColumnInfo(name = "is_audio") val isAudio: Boolean,
    @ColumnInfo(name = "insert_date") val insertDate: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "update_date") val updateDate: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "last_watch_pos") val lastWatchPos: Long = 0,
    @ColumnInfo(name = "local_watch_count") val localWatchCount: Int = 0,
)