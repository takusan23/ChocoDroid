package io.github.takusan23.chocodroid.ui.screen.favourite

/**
 * お気に入り画面の画面遷移先列挙
 * */
object FavouriteScreenNavigationLinkList {

    /** フォルダの中身表示 */
    private const val FolderVideoItemList = "folder_video_list"

    /** 動画フォルダー一覧 */
    const val FolderList = "folder_list"

    /** お気に入りチャンネル一覧 */
    const val ChannelList = "channel_list"

    /**
     * フォルダの中身を表示する
     * @param folderId フォルダIDの主キー
     * */
    fun getFolderVideoList(folderId: String) = "$FolderVideoItemList?folder_id=$folderId"

}