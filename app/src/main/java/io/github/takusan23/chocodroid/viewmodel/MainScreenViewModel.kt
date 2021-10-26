package io.github.takusan23.chocodroid.viewmodel

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.github.takusan23.chocodroid.database.db.HistoryDB
import io.github.takusan23.chocodroid.database.entity.HistoryDBEntity
import io.github.takusan23.chocodroid.setting.SettingKeyObject
import io.github.takusan23.chocodroid.setting.dataStore
import io.github.takusan23.chocodroid.tool.DownloadContentManager
import io.github.takusan23.chocodroid.tool.TimeFormatTool
import io.github.takusan23.internet.data.watchpage.MediaUrlData
import io.github.takusan23.internet.data.watchpage.WatchPageData
import io.github.takusan23.internet.html.WatchPageHTML
import io.github.takusan23.internet.magic.AlgorithmSerializer
import io.github.takusan23.internet.magic.data.AlgorithmFuncNameData
import io.github.takusan23.internet.magic.data.AlgorithmInvokeData
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * [io.github.takusan23.chocodroid.MainActivity]で使うViewModel
 *
 * Composeでも使います（画面回転しんどいので）
 * */
class MainScreenViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext

    private val _watchPageData = MutableStateFlow<WatchPageData?>(null)
    private val _mediaUrlDataFlow = MutableStateFlow<MediaUrlData?>(null)
    private val _isLoadingFlow = MutableStateFlow(false)
    private val _errorMessageFlow = MutableStateFlow<String?>(null)

    /** コルーチン起動時の引数に指定してね。例外を捕まえ、Flowに流します */
    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        _errorMessageFlow.value = throwable.message
        _isLoadingFlow.value = false
    }

    /** ローカルに保持してる解析アルゴリズム。flowで更新されるはず */
    private var localAlgorithmData: Triple<String?, AlgorithmFuncNameData?, List<AlgorithmInvokeData>?>? = null

    /** 履歴DB。 */
    private val historyDB by lazy { HistoryDB.getInstance(context) }

    /** ダウンロードコンテンツマネージャー */
    private val downloadContentManager by lazy { DownloadContentManager(context) }

    /** 動画情報データクラスを保持するFlow。外部公開用は受け取りのみ */
    val watchPageResponseDataFlow = _watchPageData as StateFlow<WatchPageData?> // 初期値nullだけどnull流したくないので

    /** 動画パス、生放送HLSアドレス等を入れたデータクラス流すFlow */
    val mediaUrlDataFlow = _mediaUrlDataFlow as StateFlow<MediaUrlData?>

    /** 読み込み中？ */
    val isLoadingFlow = _isLoadingFlow as Flow<Boolean>

    /** エラーメッセージ送信用Flow */
    val errorMessageFlow = _errorMessageFlow as StateFlow<String?>

    init {
        viewModelScope.launch {
            // データを集める
            context.dataStore.data.collect { setting ->
                val baseJsURL = setting[SettingKeyObject.WATCH_PAGE_BASE_JS_URL]
                val funcNameData = AlgorithmSerializer.toData<AlgorithmFuncNameData?>(setting[SettingKeyObject.WATCH_PAGE_JS_FUNC_NAME_JSON])
                val funcInvokeDataList = AlgorithmSerializer.toData<List<AlgorithmInvokeData>>(setting[SettingKeyObject.WATCH_PAGE_JS_INVOKE_LIST_JSON])
                localAlgorithmData = Triple(baseJsURL, funcNameData, funcInvokeDataList)
            }
        }
    }

    /**
     * ようつべ視聴ページを読み込む。レスポンスはflowに流れます
     *
     * 失敗しても失敗用Flowに流れます
     *
     * @param videoId 動画ID
     * */
    fun loadWatchPage(videoId: String) {
        // 叩く
        viewModelScope.launch(errorHandler + Dispatchers.Default) {
            _isLoadingFlow.value = true

            // HTML解析とURL（復号処理含めて）取得
            val (watchPageData, decryptData) = WatchPageHTML.getWatchPage(videoId, localAlgorithmData?.first, localAlgorithmData?.second, localAlgorithmData?.third)

            // View（Compose）にデータを渡す
            _watchPageData.value = watchPageData
            getMediaUrl()
            _isLoadingFlow.value = false

            // 2回目以降のアルゴリズムの解析をスキップするために解読情報を永続化する
            context.dataStore.edit { setting ->
                setting[SettingKeyObject.WATCH_PAGE_BASE_JS_URL] = decryptData.baseJsURL
                setting[SettingKeyObject.WATCH_PAGE_JS_FUNC_NAME_JSON] = AlgorithmSerializer.toJSON(decryptData.algorithmFuncNameData)
                setting[SettingKeyObject.WATCH_PAGE_JS_INVOKE_LIST_JSON] = AlgorithmSerializer.toJSON(decryptData.decryptInvokeList)
            }

            // 履歴に追加する
            insertDBOrWatchCountIncrement(watchPageData)
        }
    }

    /**
     * ダウンロード済み動画を読み込む。DBから動画情報を、ストレージから動画(音声)とサムネを読み出す
     *
     * Flowに値を流します
     *
     * @param videoId 動画ID
     * */
    fun loadWatchPageFromLocal(videoId: String) {
        viewModelScope.launch(errorHandler + Dispatchers.Default) {
            // データを読み出す
            val watchPageData = downloadContentManager.getWatchPageData(videoId)
            // Composeへデータを流す
            _watchPageData.value = watchPageData
            _mediaUrlDataFlow.value = _watchPageData.value?.contentUrlList?.first()
            // 視聴履歴インクリメント
            downloadContentManager.incrementLocalWatchCount(videoId)
        }
    }

    /**
     * 映像、音声URLを取得する
     *
     * 取得した値はFlowで流します
     *
     * @param quality 画質
     * */
    fun getMediaUrl(quality: String = "360p") {
        _mediaUrlDataFlow.value = _watchPageData.value?.getMediaUrlDataFromQuality(quality)
    }

    /**
     * プレイヤー終了。データクラス保持Flowにnullを入れます
     * */
    fun closePlayer() {
        viewModelScope.launch { _watchPageData.value = null }
    }

    /**
     * 視聴履歴へ追加 or 視聴回数のインクリメント をする関数
     *
     * @param watchPageData 動画情報
     * */
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

    /**
     * ダウンロードしたコンテンツを削除する
     *
     * @param videoId 動画ID
     * */
    fun deleteDownloadContent(videoId: String) {
        viewModelScope.launch(errorHandler + Dispatchers.Default) {
            downloadContentManager.deleteContent(videoId)
            // 再生中なら消す
            if (videoId == watchPageResponseDataFlow.value?.watchPageResponseJSONData?.videoDetails?.videoId) {
                closePlayer()
            }
        }
    }

}