package io.github.takusan23.chocodroid.setting

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

/**
 * DataStoreのインスタンス。使い回すのでシングルトン
 * */
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "setting")
