package io.github.takusan23.chocodroid.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.takusan23.chocodroid.database.db.HistoryDB
import io.github.takusan23.chocodroid.database.entity.HistoryDBEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/** 履歴画面で使うViewModel */
class HistoryScreenViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext

    /** データベース */
    private val historyDB by lazy { HistoryDB.getInstance(context) }

    /** 履歴一覧 */
    val historyDBDataListFlow = historyDB.historyDao().flowGetAll()

    /** 履歴を全部消す */
    fun deleteAllDB() {
        viewModelScope.launch {
            historyDB.historyDao().deleteAll()
        }
    }


}