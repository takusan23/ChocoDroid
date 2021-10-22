package io.github.takusan23.htmlparse.data.search

import kotlinx.serialization.Serializable

/**
 * 検索結果のデータクラス
 *
 * @param apiKey APIキー。２回目以降これをinit関数で指定するとAPIキーを見つける作業をスキップします。
 * @param videoContentList 動画の配列
 * */
@Serializable
data class SearchResponseData(
    @Deprecated("YTAPICall#apiKeyFlow()を使ってください。") val apiKey: String,
    val videoContentList: List<VideoContent>?,
)

/** 検索結果のレスポンスボデー */
@Serializable
data class SearchResponseJSON(
    val contents: Contents,
)

@Serializable
data class Contents(
    val twoColumnSearchResultsRenderer: TwoColumnSearchResultsRenderer,
)

@Serializable
data class TwoColumnSearchResultsRenderer(
    val primaryContents: PrimaryContents,
)

@Serializable
data class PrimaryContents(
    val sectionListRenderer: SectionListRenderer,
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
    val onResponseReceivedCommands: List<OnResponseReceivedCommands>,
)

@Serializable
data class OnResponseReceivedCommands(
    val appendContinuationItemsAction: AppendContinuationItemsAction,
)

@Serializable
data class AppendContinuationItemsAction(
    val continuationItems: List<ContinuationItem>,
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
    val contents: List<VideoContent>,
)

/**
 * 「ニュース」で検索するとnullになります。のでnullableになってる
 *
 * ちなみにテストコードではnonNullでも怒られない。は？
 * */
@Serializable
data class VideoContent(
    val videoRenderer: VideoRenderer? = null,
)

/**
 * @param publishedTimeText プレミア公開時はnullになる
 * @param viewCountText プレミア公開時はnullになる
 * */
@Serializable
data class VideoRenderer(
    val videoId: String,
    val title: Title,
    val thumbnail: Thumbnail,
    val ownerText: OwnerText,
    val lengthText: LengthText? = null,
    val publishedTimeText: PublishedTimeText? = null,
    val viewCountText: ViewCountText? = null,
)

@Serializable
data class ViewCountText(
    val simpleText: String? = null,
    val runs: List<ViewCountTextRun>? = null,
)

@Serializable
class ViewCountTextRun(
    val text: String,
)

@Serializable
data class LengthText(
    val simpleText: String,
)

@Serializable
data class PublishedTimeText(
    val simpleText: String,
)

@Serializable
data class OwnerText(
    val runs: List<OwnerData>,
)

@Serializable
data class OwnerData(
    val text: String,
    val navigationEndpoint: NavigationEndpoint,
)

@Serializable
data class NavigationEndpoint(
    val browseEndpoint: BrowseEndpoint,
)

@Serializable
data class BrowseEndpoint(
    val browseId: String,
)

@Serializable
data class Thumbnail(
    val thumbnails: List<ThumbnailUrl>,
)

@Serializable
data class ThumbnailUrl(
    val url: String,
    val width: Int,
    val height: Int,
)

@Serializable
data class Title(
    val runs: List<TitleText>,
)

@Serializable
data class TitleText(
    val text: String,
)
