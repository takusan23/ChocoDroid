package io.github.takusan23.chocodroid.ui.screen.bottomsheet

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

    /**
     * 動画一覧からメニューを開くとき
     *
     * @param videoId 動画ID
     * @param videoTitle 動画タイトル
     * @param folderId お気に入りフォルダ内の動画の場合はフォルダIDを入れる
     * @param isDownloadContent ダウンロード済みコンテンツの場合は"true"←これ直したい
     * */
    fun getVideoListMenu(
        videoId: String,
        videoTitle: String,
        folderId: String? = null,
        isDownloadContent: String? = null,
    ) = "$VideoListMenu?" + mutableMapOf(
        "video_id" to videoId,
        "video_title" to videoTitle,
        "folder_id" to folderId,
        "is_download_content" to isDownloadContent
    ).toList().joinToString(separator = "&") { "${it.first}=${it.second}" }

}