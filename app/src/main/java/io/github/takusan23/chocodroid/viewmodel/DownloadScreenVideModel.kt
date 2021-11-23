package io.github.takusan23.chocodroid.viewmodel

import android.app.Application
import io.github.takusan23.chocodroid.tool.DownloadContentManager

/**
 * ダウンロード一覧画面で使うViewModel
 * */
class DownloadScreenVideModel(application: Application) : BaseAndroidViewModel(application) {

    private val context = application.applicationContext

    /** ダウンロードコンテンツ管理クラス */
    private val downloadContentManager by lazy { DownloadContentManager(context) }

    /** ダウンロード済みコンテンツを送るFlow */
    val downloadContentFlow by lazy { downloadContentManager.collectDownloadContentToWatchPageData() }

}