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

    /** base.js内にあるURLのパラメーターを修正するJavaScriptコード */
    val WATCH_PAGE_JS_PARAM_FIX_JS_CODE = stringPreferencesKey("watchpage_js_url_params_fix_js_code")

    /** 動画検索や、チャンネルの投稿動画取得で使うAPIキーを保存しておく */
    val API_KEY = stringPreferencesKey("api_key")

    /** 単発リピート再生有効かどうか */
    val PLAYER_REPEAT_MODE = booleanPreferencesKey("player_repeat_mode")

    /** Android 12以降で使えるダイナミックカラーを有効にするか */
    val ENABLE_DYNAMIC_THEME = booleanPreferencesKey("enable_dynamic_theme")

    /** 最後に再生した曲を保存する */
    val DOWNLOAD_CONTENT_BACKGROUND_PLAYER_LATEST_PLAYING_ID = stringPreferencesKey("download_content_background_player_latest_playing_id")

    /** ダウンロード用バックグラウンド再生で、単発リピートモードが有効かどうか */
    val DOWNLOAD_CONTENT_BACKGROUND_PLAYER_REPEAT_MODE = booleanPreferencesKey("download_content_background_player_repeat_mode")

    /** 検索の並び替え */
    val SEARCH_SORT_TYPE = stringPreferencesKey("search_sort_type")

    /** 前回選択した画質。動画バージョン */
    val PLAYER_QUALITY_VIDEO = stringPreferencesKey("player_quality_video")
}