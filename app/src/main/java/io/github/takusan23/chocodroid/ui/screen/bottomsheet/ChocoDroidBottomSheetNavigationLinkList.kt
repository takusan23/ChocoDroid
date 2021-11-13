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
     * */
    fun getVideoListMenu(videoId: String, videoTitle: String, folderId: String? = null) =
        "$VideoListMenu?video_id=$videoId&video_title=$videoTitle&folder_id=$folderId"

}