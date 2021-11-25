package io.github.takusan23.chocodroid.ui.screen.bottomsheet

import android.os.Bundle

/**
 * BottomSheetのナビゲーション遷移先一覧
 * */
object ChocoDroidBottomSheetNavigationLinkList {

    /** お気に入りフォルダ追加 */
    const val AddFavoriteFolder = "add_favorite_folder"

    /** 画質変更ボトムシート */
    const val QualityChange = "quality"

    /** 動画一覧からのメニュー */
    private const val VideoListMenu = "video_list_menu"

    /** BottomSheetに渡したい値一覧 */
    private val videoListMenuArgumentList = listOf(
        "video_id",
        "video_title",
        "folder_id",
        "is_download_content",
    )

    /** Compose NavigationのBottomSheetのルート作成時のパス。引数は取れるようになってます */
    fun getVideoListMenuTemplate() = "$VideoListMenu?" + videoListMenuArgumentList.map {
        it to "{$it}"
    }.toList().joinToString(separator = "&") { "${it.first}=${it.second}" }

    /**
     * 動画一覧からメニューを開くときに使うパスを作る
     *
     * @param data [VideoListMenuData]
     * */
    fun getVideoListMenu(data: VideoListMenuData) =
        "$VideoListMenu?" + mutableMapOf(
            "video_id" to data.videoId,
            "video_title" to data.videoTitle,
            "folder_id" to data.folderId,
            "is_download_content" to data.isDownloadContent
        ).toList().joinToString(separator = "&") { "${it.first}=${it.second}" }

    /**
     * [navArgument]から[VideoListMenuData]を作る関数
     *
     * 引数が多すぎるので別関数が
     *
     * @param navArgument composable()で取れるやつ
     * */
    fun decodeVideoListMenu(navArgument: Bundle) =
        VideoListMenuData(
            navArgument.getString("video_id")!!,
            navArgument.getString("video_title")!!,
            navArgument.getString("folder_id")?.toIntOrNull(),
            navArgument.getString("is_download_content").toBoolean(),
        )

}

/**
 * BottomSheetに渡すデータが多いのでどうにかしたい。
 *
 * @param videoId 動画ID
 * @param videoTitle 動画タイトル
 * @param folderId お気に入りフォルダ内の動画の場合はフォルダIDを入れる
 * @param isDownloadContent ダウンロード済みコンテンツの場合は"true"←これ直したい
 * */
data class VideoListMenuData(
    val videoId: String,
    val videoTitle: String,
    val folderId: Int? = null,
    val isDownloadContent: Boolean = false,
)