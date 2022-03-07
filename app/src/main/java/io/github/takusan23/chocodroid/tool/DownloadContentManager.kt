package io.github.takusan23.chocodroid.tool

import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.contentValuesOf
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
 * コンテンツIDは動画IDみたいなもの。音声のみDL出来るので一応ね？
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
        File(tempFolderRootFolder, videoId).deleteRecursively()
        // データベースからも消す
        downloadContentDB.downloadContentDao().deleteFromVideoId(videoId)
    }

    /**
     * データベース内のデータをFlowで返す
     *
     * 動画情報だけが欲しい場合は[collectDownloadContentToWatchPageData]の方が良いと思います
     * */
    fun collectDownloadContent(): Flow<List<DownloadContentDBEntity>> {
        return downloadContentDB.downloadContentDao().flowGetAll()
    }

    /** データベース内のデータを[CommonVideoData]の配列にして送る */
    fun collectDownloadContentToWatchPageData(): Flow<List<CommonVideoData>> {
        return collectDownloadContent()
            .map { list -> list.map { dbItem -> dbItem.convertCommonVideoData() } }
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
            contentUrlList = listOf(MediaUrlData(urlType = MediaUrlData.MediaUrlType.TYPE_OFFLINE, mixTrackUrl = dbItem.contentPath)),
            type = "download"
        )
    }

    /**
     * [CommonVideoData]形式でデータを返す
     *
     * @param videoId 動画ID
     * @return [WatchPageData]
     * */
    suspend fun getCommonVideoData(videoId: String): CommonVideoData {
        val dbItem = downloadContentDB.downloadContentDao().getDownloadContentDataFromVideoId(videoId)
        return dbItem.convertCommonVideoData()
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
     * @param quality 画質。nullを渡すと最高画質で落とします
     * @param splitCount ファイル分割数
     * */
    suspend fun downloadAudioFile(watchPageData: WatchPageData, quality: String? = "360p", splitCount: Int = 5): Pair<File, DownloadPocket> {
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
     * @param quality 画質。nullを渡すと最高画質で落とします
     * @param splitCount ファイル分割数
     * */
    suspend fun downloadVideoFile(watchPageData: WatchPageData, quality: String? = "360p", splitCount: Int = 5): Pair<File, DownloadPocket> {
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
     * ダウンロードしたコンテンツを端末のギャラリー（Videoフォルダ）へコピーする
     *
     * 音声の場合は Musicフォルダ に入る
     *
     * MediaStore API を使う
     *
     * @param videoId 動画ID
     * */
    suspend fun copyFileToVideoOrMusicFolder(videoId: String) {
        val contentResolver = context.contentResolver
        // DBから動画情報を取得
        val dbItem = downloadContentDB.downloadContentDao().getDownloadContentDataFromVideoId(videoId)
        val isAudioOnly = dbItem.contentPath.endsWith(".aac")
        val extension = if (isAudioOnly) "aac" else "mp4"
        val folderPath = if (isAudioOnly) Environment.DIRECTORY_MUSIC else Environment.DIRECTORY_MOVIES
        // MediaStoreに入れる中身
        val contentValues = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValuesOf(
                MediaStore.MediaColumns.DISPLAY_NAME to "${dbItem.videoTitle}.$extension",
                MediaStore.MediaColumns.RELATIVE_PATH to "$folderPath/ChocoDroid"
            )
        } else {
            contentValuesOf(
                MediaStore.MediaColumns.DISPLAY_NAME to "${dbItem.videoTitle}.$extension",
            )
        }
        // MediaStoreへ登録
        val contentUri = if (isAudioOnly) MediaStore.Audio.Media.EXTERNAL_CONTENT_URI else MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val uri = contentResolver.insert(contentUri, contentValues) ?: return
        // コピーする
        val outputStream = contentResolver.openOutputStream(uri)
        // コピー対象
        val inputStream = File(dbItem.contentPath).inputStream()
        outputStream?.write(inputStream.readBytes())
        // リリース
        outputStream?.close()
        inputStream.close()
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