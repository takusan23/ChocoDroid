package io.github.takusan23.chocodroid.player

import android.content.Context
import androidx.datastore.preferences.core.edit
import io.github.takusan23.chocodroid.database.db.HistoryDB
import io.github.takusan23.chocodroid.database.entity.HistoryDBEntity
import io.github.takusan23.chocodroid.setting.SettingKeyObject
import io.github.takusan23.chocodroid.setting.dataStore
import io.github.takusan23.chocodroid.tool.DownloadContentManager
import io.github.takusan23.chocodroid.tool.TimeFormatTool
import io.github.takusan23.chocodroid.tool.WebViewJavaScriptEngine
import io.github.takusan23.internet.data.watchpage.WatchPageData
import io.github.takusan23.internet.html.WatchPageHTML
import io.github.takusan23.internet.magic.AlgorithmSerializer
import io.github.takusan23.internet.magic.UnlockMagic
import io.github.takusan23.internet.magic.data.AlgorithmFuncNameData
import io.github.takusan23.internet.magic.data.AlgorithmInvokeData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

/**
 * 動画情報やURLを取得する
 *
 * @param context [Context]
 */
class ChocoDroidContentLoader(private val context: Context) {

    /** 履歴DB */
    private val historyDB by lazy { HistoryDB.getInstance(context) }

    /** ダウンロードコンテンツマネージャー */
    private val downloadContentManager by lazy { DownloadContentManager(context) }

    /**
     * ようつべ視聴ページを読み込む
     *
     * @param videoIdOrHttpUrl 動画IDもしくは動画URL
     * @return [WatchPageData]
     */
    suspend fun loadWatchPage(videoIdOrHttpUrl: String) = withContext(Dispatchers.Default) {
        // 設定読み出し
        val setting = context.dataStore.data.first()
        val baseJsURL = setting[SettingKeyObject.WATCH_PAGE_BASE_JS_URL]
        val funcNameData = AlgorithmSerializer.toData<AlgorithmFuncNameData?>(setting[SettingKeyObject.WATCH_PAGE_JS_FUNC_NAME_JSON])
        val funcInvokeDataList = AlgorithmSerializer.toData<List<AlgorithmInvokeData>>(setting[SettingKeyObject.WATCH_PAGE_JS_INVOKE_LIST_JSON])
        val urlParamsFixJSCode = setting[SettingKeyObject.WATCH_PAGE_JS_PARAM_FIX_JS_CODE]

        // HTML解析とURL（復号処理含めて）取得
        val (watchPageData, decryptData) = WatchPageHTML.getWatchPage(videoIdOrHttpUrl, baseJsURL, funcNameData, funcInvokeDataList, urlParamsFixJSCode)

        // View（Compose）にデータを渡す
        // 動画の場合はURLのパラメーターを修正する
        // ここらへんどうにかしたい
        val fixedWatchPageData = if (!watchPageData.isLiveContent) {
            UnlockMagic.fixUrlParam(watchPageData, decryptData.urlParamFixJSCode) { evalCode ->
                withContext(Dispatchers.Main) { WebViewJavaScriptEngine.evalJavaScriptFromWebView(context, evalCode).replace("\"", "") }
            }
        } else watchPageData

        // 2回目以降のアルゴリズムの解析をスキップするために解読情報を永続化する
        context.dataStore.edit { updateSetting ->
            updateSetting[SettingKeyObject.WATCH_PAGE_BASE_JS_URL] = decryptData.baseJsURL
            updateSetting[SettingKeyObject.WATCH_PAGE_JS_FUNC_NAME_JSON] = AlgorithmSerializer.toJSON(decryptData.algorithmFuncNameData)
            updateSetting[SettingKeyObject.WATCH_PAGE_JS_INVOKE_LIST_JSON] = AlgorithmSerializer.toJSON(decryptData.decryptInvokeList)
            updateSetting[SettingKeyObject.WATCH_PAGE_JS_PARAM_FIX_JS_CODE] = decryptData.urlParamFixJSCode
        }

        // 履歴に追加する
        insertDBOrWatchCountIncrement(watchPageData)

        return@withContext fixedWatchPageData
    }

    /**
     * ダウンロード済み動画を読み込む
     *
     * @param videoId 動画ID
     * @return [WatchPageData]
     */
    suspend fun loadWatchPageFromLocal(videoId: String) = withContext(Dispatchers.IO) {
        // データを読み出す
        val watchPageData = downloadContentManager.getWatchPageData(videoId)
        // 視聴履歴インクリメント
        downloadContentManager.incrementLocalWatchCount(videoId)
        return@withContext watchPageData
    }

    /**
     * 視聴履歴へ追加 or 視聴回数のインクリメント をする関数
     *
     * @param watchPageData 動画情報
     */
    private suspend fun insertDBOrWatchCountIncrement(watchPageData: WatchPageData) {
        // 存在するかどうか
        val watchPageJSONResponseData = watchPageData.watchPageResponseJSONData
        if (historyDB.historyDao().existsHistoryData(watchPageJSONResponseData.videoDetails.videoId)) {
            // ある
            val historyDBEntity = historyDB.historyDao().getHistoryFromVideoId(watchPageJSONResponseData.videoDetails.videoId)
            val updateData = historyDBEntity.copy(updateDate = System.currentTimeMillis(), localWatchCount = historyDBEntity.localWatchCount + 1)
            historyDB.historyDao().update(updateData)
        } else {
            // ない
            val entity = HistoryDBEntity(
                videoId = watchPageJSONResponseData.videoDetails.videoId,
                duration = TimeFormatTool.videoDurationToFormatText(watchPageJSONResponseData.videoDetails.lengthSeconds.toLong()),
                title = watchPageJSONResponseData.videoDetails.title,
                ownerName = watchPageJSONResponseData.videoDetails.author,
                publishedDate = watchPageJSONResponseData.microformat.playerMicroformatRenderer.publishDate,
                thumbnailUrl = watchPageJSONResponseData.videoDetails.thumbnail.thumbnails.last().url,
            )
            historyDB.historyDao().insert(entity)
        }
    }

}