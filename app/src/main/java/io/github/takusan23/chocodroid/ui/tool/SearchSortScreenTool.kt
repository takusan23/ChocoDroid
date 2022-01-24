package io.github.takusan23.chocodroid.ui.tool

import io.github.takusan23.internet.api.SearchAPI

/** 検索画面で使うソート関連の関数 */
object SearchSortScreenTool {

    /** 並び替えの定義 */
    enum class SearchSortType {
        /** 関連順 */
        Relevance,

        /** 投稿日時順 */
        UploadDate,

        /** 再生数順 */
        WatchCount,

        /** レビュー順 */
        Review
    }

    /** [SearchAPI.PARAMS_SORT_RELEVANCE]等から[SearchSortType]を変換する */
    fun resolve(string: String) = when (string) {
        SearchAPI.PARAMS_SORT_RELEVANCE -> SearchSortType.Relevance
        SearchAPI.PARAMS_SORT_UPLOAD_DATE -> SearchSortType.UploadDate
        SearchAPI.PARAMS_SORT_WATCH_COUNT -> SearchSortType.WatchCount
        SearchAPI.PARAMS_SORT_REVIEW -> SearchSortType.Review
        else -> SearchSortType.Relevance
    }

    /** [SearchSortType]から[SearchAPI.PARAMS_SORT_RELEVANCE]等へ変換する */
    fun serialize(searchSortType: SearchSortType) = when (searchSortType) {
        SearchSortType.Relevance -> SearchAPI.PARAMS_SORT_RELEVANCE
        SearchSortType.UploadDate -> SearchAPI.PARAMS_SORT_UPLOAD_DATE
        SearchSortType.WatchCount -> SearchAPI.PARAMS_SORT_WATCH_COUNT
        SearchSortType.Review -> SearchAPI.PARAMS_SORT_REVIEW
    }
}
