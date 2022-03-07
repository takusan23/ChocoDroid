package io.github.takusan23.chocodroid.ui.screen

import androidx.navigation.navOptions

/**
 * 遷移先一覧
 *
 * Jetpack Composeの命名規則で、定数はPascalCaseでかけって言われてるので従う
 * */
object NavigationLinkList {

    /** お気に入り画面 */
    const val FavouriteScreen = "favourite"

    /** 検索画面 */
    private const val SearchScreen = "search"

    /**
     * 検索画面。
     *
     * @param query 検索ワードを
     * */
    fun getSearchScreenLink(query: String) = "$SearchScreen?query=${query}"

    /** 履歴画面 */
    const val HistoryScreen = "history"

    /** ダウンロード画面 */
    const val DownloadScreen = "download"

    /** チャンネル画面。*/
    private const val ChannelScreen = "channel"

    /**
     * チャンネル画面。
     *
     * @param channelId チャンネルID。
     * */
    fun getChannelScreenLink(channelId: String) = "$ChannelScreen?channel_id=$channelId"

    /** 戻るキーを押したときに戻す画面 */
    val NavOptions = navOptions { popUpTo(FavouriteScreen) }

    /**
     * 設定画面
     *
     * 各詳細ページは[io.github.takusan23.chocodroid.ui.screen.setting.SettingNavigationLinkList]を参照
     * */
    const val SettingScreen = "setting"

    /**
     * 検索入力画面
     */
    const val ChocoDroidBridgeSearchScreen = "bridge_search"

}