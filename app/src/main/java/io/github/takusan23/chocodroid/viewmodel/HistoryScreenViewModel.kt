package io.github.takusan23.chocodroid.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import io.github.takusan23.chocodroid.database.db.HistoryDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/** 履歴画面で使うViewModel */
class HistoryScreenViewModel(application: Application) : BaseAndroidViewModel(application) {
    private val context = application.applicationContext

    /** データベース */
    private val historyDB by lazy { HistoryDB.getInstance(context) }

    /** 履歴一覧 */
    val historyDBDataListFlow = historyDB.historyDao().flowGetAll().map {
        it.map { historyDBEntity -> historyDBEntity.convertToCommonVideoData(context) }
    }

    /** 履歴を全部消す */
    fun deleteAllDB() {
        viewModelScope.launch(errorHandler + Dispatchers.IO) {
            historyDB.historyDao().deleteAll()
        }
    }


}