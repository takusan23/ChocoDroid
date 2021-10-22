package io.github.takusan23.chocodroid.ui.screen

import androidx.navigation.navOptions
import io.github.takusan23.internet.api.SearchAPI

/**
 * 遷移先一覧
 *
 * Jetpack Composeの命名規則で、定数はPascalCaseでかけって言われてるので従う
 * */
object NavigationLinkList {

    /** お気に入り画面 */
    const val FavouriteScreen = "favourite"

    /**
     * 検索画面。queryパラメーターに検索ワードを、sortには並び順です。[SearchAPI.PARAMS_SORT_RELEVANCE]を参照して
     *
     * 例："${NavigationLinkList.SearchScreen}?query=エロゲソングメドレー?sort=${PARAMS_SORT_RELEVANCE}"
     * */
    const val SearchScreen = "search"

    /** 履歴画面 */
    const val HistoryScreen = "history"

    /** ダウンロード画面 */
    const val DownloadScreen = "download"

    /**
     * チャンネル画面。channel_idパラメーターにチャンネルIDを入れてください
     *
     * 例："${ChannelScreen}?channel_id=チャンネルID"
     * */
    const val ChannelScreen = "channel"

    /** 戻るキーを押したときに戻す画面 */
    val NavOptions = navOptions { popUpTo(FavouriteScreen) }

}