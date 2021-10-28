package io.github.takusan23.chocodroid.setting

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

/**
 * Preference DataStoreのキー
 *
 * SharedPreferenceの後継
 * */
object SettingKeyObject {

    /** 視聴ページのbase.jsのurl */
    val WATCH_PAGE_BASE_JS_URL = stringPreferencesKey("watchpage_basejs_url")

    /** base.jsで復号に使ってる関数名を保存してるJSON */
    val WATCH_PAGE_JS_FUNC_NAME_JSON = stringPreferencesKey("watchpage_js_func_name_json")

    /** base.jsで関数を呼ぶ順番の配列を保存してるJSON */
    val WATCH_PAGE_JS_INVOKE_LIST_JSON = stringPreferencesKey("watchpage_js_invoke_list_json")

    /** 動画検索や、チャンネルの投稿動画取得で使うAPIキーを保存しておく */
    val API_KEY = stringPreferencesKey("api_key")

    /** リピート再生有効かどうか */
    val PLAYER_REPEAT_PLAY = booleanPreferencesKey("player_repeat_play")

    /** Android 12以降で使えるダイナミックカラーを有効にするか */
    val ENABLE_DYNAMIC_THEME = booleanPreferencesKey("enable_dynamic_theme")
}