package io.github.takusan23.chocodroid.tool

import android.content.Context
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.database.db.DownloadContentDB
import io.github.takusan23.chocodroid.database.entity.DownloadContentDBEntity
import io.github.takusan23.chocodroid.setting.dataStore
import io.github.takusan23.downloadpocket.DownloadPocket
import io.github.takusan23.internet.data.CommonVideoData
import io.github.takusan23.internet.data.watchpage.MediaUrlData
import io.github.takusan23.internet.data.watchpage.WatchPageData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File

/**
 * ダウンロード機能に関する関数がある。こいつのおかげでデータベースのDAOへ直接触れることはないはず
 *
 * 映像と音声を合成するコードは[DownloadVideoMuxer]が担当しています。
 *
 * ダウンロード用関数には返り値にPairを返しFlowを取得できます。新しくコルーチンを起動してcollectすると0から100まで受け取ります。
 * 100まで行ったらコルーチンをキャンセルしてガベージコレクションが回収できるようにしてください。
 *
 * @param context Context ファイル読み書きするので
 * */
class DownloadContentManager(private val context: Context) {

    /** 分割ダウンロード時に一時的に使うフォルダを作成するフォルダ */
    private val tempFolderRootFolder by lazy { context.externalCacheDir!! }

    /** ダウンロードしたデータ保存先 */
    private val downloadFolderRootFolder by lazy { File(context.getExternalFilesDir(null), "download").apply { mkdir() } }

    /** ダウンロードコンテンツ収納データベース */
    private val downloadContentDB by lazy { DownloadContentDB.getInstance(context) }

    /** 設定読み取り */
    private val dataStore by lazy { context.dataStore }

    /**
     * 指定したコンテンツの動画IDを削除する。DB操作が絡むのでサスペンド関数
     *
     * @param videoId 動画ID
     * */
    suspend fun deleteContent(videoId: String) {
        File(downloadFolderRootFolder, videoId).deleteRecursively()
        // データベースからも消す
        downloadContentDB.downloadContentDao().deleteFromVideoId(videoId)
    }

    /** データベース内のデータを[CommonVideoData]の配列にして送る */
    fun collectDownloadContentToWatchPageData(): Flow<List<CommonVideoData>> {
        return downloadContentDB.downloadContentDao()
            .flowGetAll()
            .map { list ->
                list.map { dbItem ->
                    val responseJSONData = WatchPageData.decodeWatchPageResponseDataFromString(dbItem.watchPageResponseJSON)
                    CommonVideoData(responseJSONData).copy(
                        watchCount = "${context.getString(R.string.watch_count)} : ${dbItem.localWatchCount}",
                        thumbnailUrl = "file://${dbItem.thumbnailPath}",
                    )
                }
            }
    }

    /**
     * データベース内の視聴履歴をインクリメントする
     *
     * @param videoId 動画ID
     * */
    suspend fun incrementLocalWatchCount(videoId: String) {
        val dbItem = downloadContentDB.downloadContentDao().getDownloadContentDataFromVideoId(videoId)
        val afterDBItem = dbItem.copy(localWatchCount = dbItem.localWatchCount + 1, updateDate = System.currentTimeMillis())
        // 更新
        downloadContentDB.downloadContentDao().update(afterDBItem)
    }

    /**
     * ダウンロード済みかどうかを返す
     *
     * @param videoId 動画ID
     * @return 存在すればtrue
     * */
    suspend fun existsDataFromVideoId(videoId: String) = downloadContentDB.downloadContentDao().existsDataFromDownloadContentDB(videoId)

    /**
     * [WatchPageData]の形でデータを返す
     *
     * 動画パスは[WatchPageData.contentUrlList]の最初に入っています。[MediaUrlData.mixTrackUrl]がそうです。
     *
     * @param videoId 動画ID
     * @return [WatchPageData]
     * */
    suspend fun getWatchPageData(videoId: String): WatchPageData {
        val dbItem = downloadContentDB.downloadContentDao().getDownloadContentDataFromVideoId(videoId)
        val responseJSONData = WatchPageData.decodeWatchPageResponseDataFromString(dbItem.watchPageResponseJSON)
        val initialJSONData = WatchPageData.decodeWatchPageInitialDataFromString(dbItem.watchPageInitialJSON)

        return WatchPageData(
            watchPageInitialJSONData = initialJSONData,
            watchPageResponseJSONData = responseJSONData,
            contentUrlList = listOf(MediaUrlData(mixTrackUrl = dbItem.contentPath))
        )
    }

    /**
     * サムネイルを保存する。
     *
     * @param watchPageData 視聴ページ
     * */
    suspend fun downloadThumbnailFile(watchPageData: WatchPageData): Pair<File, DownloadPocket> {
        val videoId = watchPageData.watchPageResponseJSONData.videoDetails.videoId
        val thumbUrl = watchPageData.watchPageResponseJSONData.videoDetails.thumbnail.thumbnails.last().url
        val splitFolderName = "${videoId}_thumb"
        val resultFileName = "${videoId}.png"
        return downloadContent(thumbUrl, splitFolderName, videoId, resultFileName, 1)
    }

    /**
     * 音声ファイルを保存する
     *
     * @param watchPageData 視聴ページ
     * @param quality 画質
     * @param splitCount ファイル分割数
     * */
    suspend fun downloadAudioFile(watchPageData: WatchPageData, quality: String = "360p", splitCount: Int = 5): Pair<File, DownloadPocket> {
        val videoId = watchPageData.watchPageResponseJSONData.videoDetails.videoId
        val audioFileUrl = watchPageData.getMediaUrlDataFromQuality(quality).audioTrackUrl
        val splitFolderName = "${videoId}_audio"
        val resultFileName = "${videoId}.aac"
        return downloadContent(audioFileUrl!!, splitFolderName, videoId, resultFileName, splitCount)
    }

    /**
     * 映像ファイルを保存する
     *
     * @param watchPageData 視聴ページ
     * @param quality 画質
     * @param splitCount ファイル分割数
     * */
    suspend fun downloadVideoFile(watchPageData: WatchPageData, quality: String = "360p", splitCount: Int = 5): Pair<File, DownloadPocket> {
        val videoId = watchPageData.watchPageResponseJSONData.videoDetails.videoId
        val videoFileUrl = watchPageData.getMediaUrlDataFromQuality(quality).videoTrackUrl
        val splitFolderName = "${videoId}_video"
        val resultFileName = "${videoId}.mp4"
        return downloadContent(videoFileUrl!!, splitFolderName, videoId, resultFileName, splitCount)
    }

    /**
     * [DownloadVideoMuxer]の合成関数を呼ぶだけの関数
     *
     * 合成したファイルは動画ID_mix.mp4って名前になります。
     *
     * @param videoId 動画ID。フォルダ名になります
     * @param contentPathList 映像と音声のファイルパスを入れてください
     * @return 保存先のFile
     * */
    suspend fun downloadVideoMix(contentPathList: List<String>, videoId: String): File {
        val videoFileFolder = File(downloadFolderRootFolder, videoId).apply { mkdir() }
        val resultPath = File(videoFileFolder, "${videoId}_mix.mp4")
        // 合成開始
        DownloadVideoMuxer.startMixer(contentPathList, resultPath.path)
        return resultPath
    }

    /**
     * データベースへファイルパス等を格納する
     *
     * @param contentUrl 音声か動画の保存先パス
     * @param isAudio 音声のみの場合はtrue
     * @param thumbnailPath サムネパス
     * @param watchPageData 視聴ページデータ
     * */
    suspend fun insertDownloadContentDB(watchPageData: WatchPageData, thumbnailPath: String, contentUrl: String, isAudio: Boolean) {
        val entity = DownloadContentDBEntity(
            videoId = watchPageData.watchPageResponseJSONData.videoDetails.videoId,
            videoTitle = watchPageData.watchPageResponseJSONData.videoDetails.title,
            isAudio = isAudio,
            contentPath = contentUrl,
            watchPageInitialJSON = watchPageData.encodeWatchPageInitialDataToString(),
            watchPageResponseJSON = watchPageData.encodeWatchPageResponseDataToString(),
            thumbnailPath = thumbnailPath,
        )
        downloadContentDB.downloadContentDao().insert(entity)
    }


    /**
     * URLを指定してファイルをダウンロードする関数。
     *
     * @return ダウンロード進捗Flow。FlowはCollect中は一時停止するので
     * */
    private suspend fun downloadContent(url: String, splitFolderName: String, contentFolderName: String, resultFileName: String, splitCount: Int = 5) = withContext(Dispatchers.IO) {
        // 分割ファイル保存先
        val splitFileFolder = File(tempFolderRootFolder, splitFolderName).apply { mkdir() }
        // 最終的なファイル
        val contentFileFolder = File(downloadFolderRootFolder, contentFolderName).apply { if (!exists()) mkdir() }
        val resultContentFile = File(contentFileFolder, resultFileName).apply { createNewFile() }
        // ダウンロード開始
        val downloadPocket = DownloadPocket(
            url = url,
            splitFileFolder = splitFileFolder,
            resultVideoFile = resultContentFile,
            splitCount = splitCount
        )
        return@withContext resultContentFile to downloadPocket
    }


}