package io.github.takusan23.internet.data.channel

import io.github.takusan23.internet.data.CommonVideoData
import kotlinx.serialization.Serializable

/**
 * チャンネルAPIのレスポンスボデー
 *
 * @param
 * @param contents 階層が深すぎる
 * */
@Serializable
data class ChannelResponseData(
    val contents: Contents,
    val header: Header,
) {

    /** アップロード動画一覧を返す。階層が深すぎるので使ってください。 */
    fun getVideoList(): List<CommonVideoData>? {
        return contents.twoColumnBrowseResultsRenderer.tabs.find { it.tabRenderer?.content != null }!!
            .tabRenderer
            ?.content
            ?.richGridRenderer
            ?.contents
            ?.mapNotNull { it.richItemRenderer?.content?.videoRenderer?.let { it1 -> CommonVideoData(it1) } }
    }

    /** 追加ロード用の Token を取得する */
    fun getContinuationToken(): String? {
        return contents.twoColumnBrowseResultsRenderer.tabs.find { it.tabRenderer?.content != null }!!
            .tabRenderer
            ?.content
            ?.richGridRenderer
            ?.contents
            ?.last()
            ?.continuationItemRenderer
            ?.continuationEndpoint
            ?.continuationCommand
            ?.token
    }
}

/**
 * 追加読み込み時の返り値
 * */
@Serializable
data class MoreChannelResponseData(
    val onResponseReceivedActions: List<OnResponseReceivedActions>,
)

@Serializable
data class OnResponseReceivedActions(
    val appendContinuationItemsAction: AppendContinuationItemsAction,
)

@Serializable
data class AppendContinuationItemsAction(
    val continuationItems: List<RichGridRendererContent>,
)

@Serializable
data class Header(
    val c4TabbedHeaderRenderer: C4TabbedHeaderRenderer,
)

/**
 * @param banner 未設定の場合があるのでnull
 * */
@Serializable
data class C4TabbedHeaderRenderer(
    val channelId: String,
    val title: String,
    val avatar: Thumbnail,
    val banner: Thumbnail? = null,
    val subscriberCountText: SubscriberCountText,
)

@Serializable
data class SubscriberCountText(
    val simpleText: String,
)

@Serializable
data class Contents(
    val twoColumnBrowseResultsRenderer: TwoColumnSearchResultsRenderer,
)

@Serializable
data class TwoColumnSearchResultsRenderer(
    val tabs: List<Tab>,
)

@Serializable
data class Tab(
    val tabRenderer: TabRenderer? = null,
)

@Serializable
data class TabRenderer(
    val content: Content? = null,
)

@Serializable
data class Content(
    val richGridRenderer: RichGridRenderer,
)

@Serializable
data class RichGridRenderer(
    val contents: List<RichGridRendererContent>,
)

@Serializable
data class RichGridRendererContent(
    // richItemRenderer 以外が来ることがある。オプショナルフィールドの場合は null を初期値にする
    val richItemRenderer: RichItemRenderer? = null,
    // 最後に入ってるオブジェクト。追加ロードで使われる
    val continuationItemRenderer: ContinuationItemRenderer? = null,
)

@Serializable
data class RichItemRenderer(
    val content: RichItemRendererContent? = null,
)

@Serializable
data class RichItemRendererContent(
    val videoRenderer: VideoRenderer,
)

/** やっと動画情報データクラス */
@Serializable
data class VideoRenderer(
    val videoId: String,
    val thumbnail: Thumbnail,
    val title: Title,
    val publishedTimeText: PublishedTimeText? = null,
    val viewCountText: ViewCountText? = null,
    val thumbnailOverlays: List<ThumbnailOverlays>,
)

@Serializable
data class ThumbnailOverlays(
    val thumbnailOverlayTimeStatusRenderer: ThumbnailOverlayTimeStatusRenderer? = null,
)

@Serializable
data class ThumbnailOverlayTimeStatusRenderer(
    val text: Text,
)

@Serializable
data class Text(
    val simpleText: String? = null,
)

@Serializable
data class ViewCountText(
    val simpleText: String,
)

@Serializable
data class PublishedTimeText(
    val simpleText: String,
)

@Serializable
data class Title(
    val runs: List<TitleText>,
)

@Serializable
data class TitleText(
    val text: String,
)

@Serializable
data class Thumbnail(
    val thumbnails: List<ThumbnailUrl>,
)

@Serializable
data class ThumbnailUrl(
    val url: String,
)

@Serializable
data class ContinuationItemRenderer(
    val continuationEndpoint: ContinuationEndpoint,
)

@Serializable
data class ContinuationEndpoint(
    val continuationCommand: ContinuationCommand,
)

@Serializable
data class ContinuationCommand(
    val token: String,
)