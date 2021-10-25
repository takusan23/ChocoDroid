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
            ?.sectionListRenderer
            ?.contents
            ?.get(0)?.itemSectionRenderer
            ?.contents
            ?.get(0)
            ?.gridRenderer
            ?.items
            ?.mapNotNull { it.gridVideoRenderer }
            ?.map { CommonVideoData(it) }
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
    val continuationItems: List<GridRendererItem>,
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
    val sectionListRenderer: SectionListRenderer,
)

@Serializable
data class SectionListRenderer(
    val contents: List<SectionListRendererContent>,
)

@Serializable
data class SectionListRendererContent(
    val itemSectionRenderer: ItemSectionRenderer,
)

@Serializable
data class ItemSectionRenderer(
    val contents: List<ItemSectionRendererContent>,
)

@Serializable
data class ItemSectionRendererContent(
    val gridRenderer: GridRenderer,
)

@Serializable
data class GridRenderer(
    val items: List<GridRendererItem>,
)

/**
 * たぶん最後にgridVideoRenderer以外のが入ってるのでnull
 * */
@Serializable
data class GridRendererItem(
    val gridVideoRenderer: GridVideoRenderer? = null,
)

/** やっと動画情報データクラス */
@Serializable
data class GridVideoRenderer(
    val videoId: String,
    val thumbnail: Thumbnail,
    val title: Title,
    val publishedTimeText: PublishedTimeText,
    val viewCountText: ViewCountText,
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
    val simpleText: String,
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
