package io.github.takusan23.chocodroid.player

import android.content.Context
import androidx.datastore.preferences.core.edit
import io.github.takusan23.chocodroid.database.db.HistoryDB
import io.github.takusan23.chocodroid.database.entity.HistoryDBEntity
import io.github.takusan23.chocodroid.setting.SettingKeyObject
import io.github.takusan23.chocodroid.setting.dataStore
import io.github.takusan23.chocodroid.tool.DownloadContentManager
import io.github.takusan23.chocodroid.tool.StacktraceToString
import io.github.takusan23.chocodroid.tool.TimeFormatTool
import io.github.takusan23.chocodroid.tool.WebViewJavaScriptEngine
import io.github.takusan23.internet.data.watchpage.MediaUrlData
import io.github.takusan23.internet.data.watchpage.WatchPageData
import io.github.takusan23.internet.html.WatchPageHTML
import io.github.takusan23.internet.magic.AlgorithmSerializer
import io.github.takusan23.internet.magic.UnlockMagic
import io.github.takusan23.internet.magic.data.AlgorithmFuncNameData
import io.github.takusan23.internet.magic.data.AlgorithmInvokeData
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * 動画情報やURLを取得する
 *
 * @param context [Context]
 * @param chocoDroidPlayer プレイヤー。なんか引数に持たせるべきじゃない気がする、、、
 */
class ChocoDroidContentLoader(private val context: Context, private val chocoDroidPlayer: ChocoDroidPlayer) {

    /** コルーチン起動時の引数に指定してね。例外を捕まえ、Flowに流します */
    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        _errorMessageFlow.value = StacktraceToString.stackTraceToString(throwable)
        _isLoadingFlow.value = false
    }
    private val scope = CoroutineScope(Job() + Dispatchers.Default + errorHandler)

    /** 履歴DB */
    private val historyDB by lazy { HistoryDB.getInstance(context) }

    /** ダウンロードコンテンツマネージャー */
    private val downloadContentManager by lazy { DownloadContentManager(context) }

    private val _isLoadingFlow = MutableStateFlow(false)
    private val _errorMessageFlow = MutableStateFlow<String?>(null)
    private val _watchPageData = MutableStateFlow<WatchPageData?>(null)
    private val _mediaUrlData = MutableStateFlow<MediaUrlData?>(null)

    /** 動画情報データクラスを保持するFlow。外部公開用は受け取りのみ */
    val watchPageResponseDataFlow = _watchPageData.asStateFlow() // 初期値nullだけどnull流したくないので

    /** 動画パス、生放送HLSアドレス等を入れたデータクラス流すFlow */
    val mediaUrlData = _mediaUrlData.asStateFlow()

    /** 読み込み中？ */
    val isLoadingFlow = _isLoadingFlow.asStateFlow()

    /** エラーメッセージ送信用Flow */
    val errorMessageFlow = _errorMessageFlow.asStateFlow()

    /**
     * ようつべ視聴ページを読み込む。レスポンスはflowに流れます
     *
     * 失敗しても失敗用Flowに流れます
     *
     * @param videoIdOrHttpUrl 動画IDもしくは動画URL
     */
    fun loadWatchPage(videoIdOrHttpUrl: String) {
        // 叩く
        scope.launch {
            _isLoadingFlow.value = true

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
            _watchPageData.value = if (!watchPageData.isLiveContent) {
                UnlockMagic.fixUrlParam(watchPageData, decryptData.urlParamFixJSCode) { evalCode ->
                    withContext(Dispatchers.Main) { WebViewJavaScriptEngine.evalJavaScriptFromWebView(context, evalCode).replace("\"", "") }
                }
            } else watchPageData
            selectMediaUrl()
            _isLoadingFlow.value = false

            // 2回目以降のアルゴリズムの解析をスキップするために解読情報を永続化する
            context.dataStore.edit { updateSetting ->
                updateSetting[SettingKeyObject.WATCH_PAGE_BASE_JS_URL] = decryptData.baseJsURL
                updateSetting[SettingKeyObject.WATCH_PAGE_JS_FUNC_NAME_JSON] = AlgorithmSerializer.toJSON(decryptData.algorithmFuncNameData)
                updateSetting[SettingKeyObject.WATCH_PAGE_JS_INVOKE_LIST_JSON] = AlgorithmSerializer.toJSON(decryptData.decryptInvokeList)
                updateSetting[SettingKeyObject.WATCH_PAGE_JS_PARAM_FIX_JS_CODE] = decryptData.urlParamFixJSCode
            }

            // プレイヤーにセットする
            setMediaAndPlay(watchPageData, mediaUrlData.value!!)

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
     */
    fun loadWatchPageFromLocal(videoId: String) {
        scope.launch {
            // データを読み出す
            val watchPageData = downloadContentManager.getWatchPageData(videoId)
            // Composeへデータを流す
            _watchPageData.value = watchPageData
            _mediaUrlData.value = _watchPageData.value?.contentUrlList?.first()
            // プレイヤーにセットする
            setMediaAndPlay(watchPageData, mediaUrlData.value!!)
            // 視聴履歴インクリメント
            downloadContentManager.incrementLocalWatchCount(videoId)
        }
    }

    /**
     * プレイヤーに動画をロードする
     *
     * @param mediaUrlData [MediaUrlData]
     */
    private suspend fun setMediaAndPlay(watchPageData: WatchPageData, mediaUrlData: MediaUrlData) = withContext(Dispatchers.Main) {
        // プレイヤー作る
        chocoDroidPlayer.createPlayer()
        preparePlayer(mediaUrlData)
        // ダブルタップシークを実装した際に、初回ロード中にダブルタップすることで即時再生されることを発見したので、
        // わからないレベルで進めておく。これで初回のめっちゃ長い読み込みが解決する？
        if (!watchPageData.isLiveContent) {
            chocoDroidPlayer.currentPositionMs = 10L
        }
    }

    /**
     * 映像、音声URLを取得する
     *
     * 取得した値はFlowで流します
     *
     * @param quality 画質。省略時は前回見ていた画質、それがない場合は360p、それもない場合は最高画質
     */
    fun selectMediaUrl(quality: String? = null) {
        scope.launch {
            // 前回の画質。ない場合は 360p
            val prevQuality = quality ?: context.dataStore.data.map { it[SettingKeyObject.PLAYER_QUALITY_VIDEO] }.first() ?: "360p"
            // 画質を選んでURLをFlowで流す
            val mediaUrlData = _watchPageData.value?.getMediaUrlDataFromQuality(prevQuality) ?: return@launch
            _mediaUrlData.value = mediaUrlData
            withContext(Dispatchers.Main) { preparePlayer(mediaUrlData) }
            // 保存する
            context.dataStore.edit { it[SettingKeyObject.PLAYER_QUALITY_VIDEO] = prevQuality }
        }
    }

    /**
     * プレイヤー終了。データクラス保持Flowにnullを入れます
     * */
    fun destroy() {
        scope.coroutineContext.cancelChildren()
        _watchPageData.value = null
        _mediaUrlData.value = null
    }

    /**
     * 動画をExoPlayerへ渡してロードする
     *
     * @param mediaUrlData [MediaUrlData]
     */
    private fun preparePlayer(mediaUrlData: MediaUrlData) {
        // Hls/DashのManifestがあればそれを読み込む（生放送、一部の動画）。
        // ない場合は映像、音声トラックをそれぞれ渡す
        if (mediaUrlData.mixTrackUrl != null) {
            val isDash = mediaUrlData.urlType == MediaUrlData.MediaUrlType.TYPE_DASH
            chocoDroidPlayer.setMediaSourceUri(mediaUrlData.mixTrackUrl!!, isDash)
        } else {
            chocoDroidPlayer.setMediaSourceVideoAudioUriSupportVer(mediaUrlData.videoTrackUrl!!, mediaUrlData.audioTrackUrl!!)
        }
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