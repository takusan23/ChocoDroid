package io.github.takusan23.internet.data

import io.github.takusan23.internet.data.search.VideoRenderer
import io.github.takusan23.internet.data.watchpage.CompactVideoRenderer
import io.github.takusan23.internet.data.watchpage.WatchPageResponseJSONData
import java.text.SimpleDateFormat
import java.util.*

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
 * @param watchCount 視聴回数。ライブ配信時は同接
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

    /** [io.github.takusan23.internet.data.channel.VideoRenderer]から[CommonVideoData]へ変換 */
    constructor(videoRenderer: io.github.takusan23.internet.data.channel.VideoRenderer) : this(
        videoId = videoRenderer.videoId,
        videoTitle = videoRenderer.title.runs.first().text,
        duration = videoRenderer.thumbnailOverlays[0].thumbnailOverlayTimeStatusRenderer!!.text.simpleText,
        watchCount = videoRenderer.viewCountText?.simpleText ?: "",
        publishDate = videoRenderer.publishedTimeText?.simpleText,
        ownerName = "",
        thumbnailUrl = videoRenderer.thumbnail.thumbnails.last().url,
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
        duration = watchPageResponseJSONData.videoDetails.lengthSeconds.let { videoLengthSec ->
            val minuteSec = SimpleDateFormat("mm:ss", Locale.getDefault()).format(videoLengthSec * 1000)
            if (videoLengthSec < 3600) {
                // 1時間を超えない場合
                minuteSec
            } else {
                val hourText = (videoLengthSec / 3600)
                    .let { hour -> String.format("%02d", hour) }
                "$hourText:$minuteSec"
            }
        },
        watchCount = watchPageResponseJSONData.videoDetails.viewCount,
        publishDate = watchPageResponseJSONData.microformat.playerMicroformatRenderer.publishDate,
        ownerName = watchPageResponseJSONData.videoDetails.author,
        thumbnailUrl = watchPageResponseJSONData.videoDetails.thumbnail.thumbnails.last().url,
    )

    /** [CompactVideoRenderer]から[CommonVideoData]へ変換 */
    constructor(compactVideoRenderer: CompactVideoRenderer) : this(
        videoId = compactVideoRenderer.videoId,
        videoTitle = compactVideoRenderer.title.simpleText,
        duration = compactVideoRenderer.lengthText?.simpleText,
        watchCount = compactVideoRenderer.shortViewCountText.simpleText ?: compactVideoRenderer.shortViewCountText.runs?.joinToString(separator = "") { it.text }!!,
        publishDate = compactVideoRenderer.publishedTimeText?.simpleText,
        ownerName = compactVideoRenderer.longBylineText.runs[0].text,
        thumbnailUrl = compactVideoRenderer.thumbnail.thumbnails.last().url,
    )

    /** 継承可能と引き換えにデータクラスのcopyが使えなくなったので */
    fun copy(
        videoId: String = this.videoId,
        videoTitle: String = this.videoTitle,
        duration: String? = this.duration,
        watchCount: String = this.watchCount,
        publishDate: String? = this.publishDate,
        ownerName: String = this.ownerName,
        thumbnailUrl: String = this.thumbnailUrl,
    ) = CommonVideoData(videoId, videoTitle, duration, watchCount, publishDate, ownerName, thumbnailUrl)


}