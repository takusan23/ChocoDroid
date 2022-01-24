package io.github.takusan23.chocodroid.viewmodel

import android.app.Application
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.viewModelScope
import io.github.takusan23.chocodroid.setting.SettingKeyObject
import io.github.takusan23.chocodroid.setting.dataStore
import io.github.takusan23.chocodroid.ui.tool.SearchSortScreenTool
import io.github.takusan23.internet.api.SearchAPI
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * 検索画面のソート変更ボトムシートで使うViewModel
 * */
class SearchSortScreenViewModel(application: Application) : BaseAndroidViewModel(application) {

    private val context = application.applicationContext

    /** 現在のソートの種類を取得する */
    val currentSortType = context.dataStore.data.map { it[SettingKeyObject.SEARCH_SORT_TYPE] ?: SearchAPI.PARAMS_SORT_RELEVANCE }

    /**
     * ソートを変更する
     *
     * @param sortType [SearchSortScreenTool.SearchSortType.Relevance]など
     * */
    fun setSortType(sortType: SearchSortScreenTool.SearchSortType) {
        viewModelScope.launch(errorHandler) {
            context.dataStore.edit {
                it[SettingKeyObject.SEARCH_SORT_TYPE] = SearchSortScreenTool.serialize(sortType)
            }
        }
    }

}