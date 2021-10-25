package io.github.takusan23.internet.data

import io.github.takusan23.internet.data.channel.GridVideoRenderer
import io.github.takusan23.internet.data.search.VideoRenderer
import io.github.takusan23.internet.data.watchpage.WatchPageResponseJSONData

/**
 * 検索、チャンネル投稿動画、、、等動画情報のデータクラスがバラバラなので、
 *
 * 共通している項目はまとめようプロジェクト。
 *
 * これで動画一覧表示等が使い回せるように
 *
 * @param videoId 動画ID
 * @param videoTitle 動画タイトル
 * @param ownerName 投稿者。変態糞土方
 * @param publishDate 投稿日。ライブ配信時はnull
 * @param thumbnailUrl サムネイルURL
 * @param watchCount 視聴回数
 * @param duration 再生時間。ライブ配信時はnull
 * */
open class CommonVideoData(
    val videoId: String,
    val videoTitle: String,
    val duration: String?,
    val watchCount: String,
    val publishDate: String?,
    val ownerName: String,
    val thumbnailUrl: String,
) {

    /** [GridVideoRenderer]から[CommonVideoData]へ変換 */
    constructor(gridVideoRenderer: GridVideoRenderer) : this(
        videoId = gridVideoRenderer.videoId,
        videoTitle = gridVideoRenderer.title.runs[0].text,
        duration = gridVideoRenderer.thumbnailOverlays[0].thumbnailOverlayTimeStatusRenderer!!.text.simpleText,
        watchCount = gridVideoRenderer.viewCountText.simpleText,
        publishDate = gridVideoRenderer.publishedTimeText.simpleText,
        ownerName = "",
        thumbnailUrl = gridVideoRenderer.thumbnail.thumbnails.last().url,
    )

    /** [VideoRenderer]から[CommonVideoData]へ変換 */
    constructor(videoRenderer: VideoRenderer) : this(
        videoId = videoRenderer.videoId,
        videoTitle = videoRenderer.title.runs.last().text,
        duration = videoRenderer.lengthText?.simpleText,
        watchCount = videoRenderer.viewCountText?.simpleText ?: videoRenderer.viewCountText?.runs?.joinToString(separator = " ") { it.text } ?: "",
        publishDate = videoRenderer.publishedTimeText?.simpleText,
        ownerName = videoRenderer.ownerText.runs.last().text,
        thumbnailUrl = videoRenderer.thumbnail.thumbnails.last().url,
    )

    /** [watchPageResponseJSONData]から[CommonVideoData]ヘ変換 */
    constructor(watchPageResponseJSONData: WatchPageResponseJSONData) : this(
        videoId = watchPageResponseJSONData.videoDetails.videoId,
        videoTitle = watchPageResponseJSONData.videoDetails.title,
        duration = watchPageResponseJSONData.videoDetails.shortDescription,
        watchCount = watchPageResponseJSONData.videoDetails.viewCount,
        publishDate = watchPageResponseJSONData.microformat.playerMicroformatRenderer.publishDate,
        ownerName = watchPageResponseJSONData.videoDetails.author,
        thumbnailUrl = watchPageResponseJSONData.videoDetails.thumbnail.thumbnails.last().url,
    )

}