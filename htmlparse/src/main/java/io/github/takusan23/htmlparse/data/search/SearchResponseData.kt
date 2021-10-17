package io.github.takusan23.htmlparse.data.search

import kotlinx.serialization.Serializable

/** 検索結果のJSONオブジェクト */
@Serializable
data class SearchResponseData(
    val contents: Contents
)

@Serializable
data class Contents(
    val twoColumnSearchResultsRenderer: TwoColumnSearchResultsRenderer
)

@Serializable
data class TwoColumnSearchResultsRenderer(
    val primaryContents: PrimaryContents
)

@Serializable
data class PrimaryContents(
    val sectionListRenderer: SectionListRenderer
)

@Serializable
data class SectionListRenderer(
    val contents: List<SelectionListContents>,
)

/**
 * 追加で検索したときのレスポンスJSON
 * */
@Serializable
data class MoreSearchResponseData(
    val onResponseReceivedCommands: List<OnResponseReceivedCommands>
)

@Serializable
data class OnResponseReceivedCommands(
    val appendContinuationItemsAction: AppendContinuationItemsAction,
)

@Serializable
data class AppendContinuationItemsAction(
    val continuationItems: List<ContinuationItem>
)

@Serializable
data class ContinuationItem(
    val itemSectionRenderer: ItemSectionRenderer? = null,
)

@Serializable
data class SelectionListContents(
    val itemSectionRenderer: ItemSectionRenderer? = null,
)

@Serializable
data class ItemSectionRenderer(
    val contents: List<VideoContent>
)

@Serializable
data class VideoContent(
    val videoRenderer: VideoRenderer
)

@Serializable
data class VideoRenderer(
    val videoId: String,
    val title: Title,
    val thumbnail: Thumbnail,
    val ownerText: OwnerText,
    val publishedTimeText: PublishedTimeText,
    val lengthText: LengthText,
    val viewCountText: ViewCountText,
)

@Serializable
data class ViewCountText(
    val simpleText: String
)

@Serializable
data class LengthText(
    val simpleText: String
)

@Serializable
data class PublishedTimeText(
    val simpleText: String
)

@Serializable
data class OwnerText(
    val runs: List<OwnerData>
)

@Serializable
data class OwnerData(
    val text: String,
    val navigationEndpoint: NavigationEndpoint
)

@Serializable
data class NavigationEndpoint(
    val browseEndpoint: BrowseEndpoint
)

@Serializable
data class BrowseEndpoint(
    val browseId: String
)

@Serializable
data class Thumbnail(
    val thumbnails: List<ThumbnailUrl>
)

@Serializable
data class ThumbnailUrl(
    val url: String,
    val width: Int,
    val height: Int
)

@Serializable
data class Title(
    val runs: List<TitleText>
)

@Serializable
data class TitleText(
    val text: String
)
