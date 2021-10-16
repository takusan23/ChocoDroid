package io.github.takusan23.chocodroid.ui.screen

import io.github.takusan23.htmlparse.html.SearchAPI

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
    const val DownloadScreen = "cache"

}