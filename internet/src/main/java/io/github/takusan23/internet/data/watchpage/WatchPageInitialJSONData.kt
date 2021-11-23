package io.github.takusan23.internet.data.watchpage

import kotlinx.serialization.Serializable

/**
 * 今の所アイコンの画像URL取得のためにパースしてる
 * */
@Serializable
data class WatchPageInitialJSONData(
    val contents: Contents,
)

@Serializable
data class Contents(
    val twoColumnWatchNextResults: TwoColumnWatchNextResults,
)

@Serializable
data class TwoColumnWatchNextResults(
    val results: Results,
    val secondaryResults: SecondaryResults,
)

@Serializable
data class SecondaryResults(
    val secondaryResults: SecondaryResultsSecondaryResults,
)

@Serializable
data class SecondaryResultsSecondaryResults(
    val results: List<SecondaryResultsResults>,
)

@Serializable
data class SecondaryResultsResults(
    val compactVideoRenderer: CompactVideoRenderer? = null,
)

/** 関連動画 */
@Serializable
data class CompactVideoRenderer(
    val videoId: String,
    val title: Title,
    val shortViewCountText: ViewCountText,
    val lengthText: LengthText? = null,
    val thumbnail: Thumbnail,
    val channelThumbnail: Thumbnail,
    val publishedTimeText: PublishedTimeText? = null,
    val longBylineText: LongBylineText,
)

@Serializable
data class LongBylineText(
    val runs: List<LongBylineTextRun>,
)

@Serializable
data class LongBylineTextRun(
    val text: String,
)

@Serializable
data class PublishedTimeText(
    val simpleText: String,
)

@Serializable
data class LengthText(
    val simpleText: String,
)

/** ライブ配信時は[runs]に値が入る */
@Serializable
data class ViewCountText(
    val simpleText: String? = null,
    val runs: List<ViewCountTextRunsText>? = null,
)

@Serializable
data class ViewCountTextRunsText(
    val text: String,
)


@Serializable
data class Title(
    val simpleText: String,
)

@Serializable
data class Results(
    val results: ResultsResults,
)

@Serializable
data class ResultsResults(
    val contents: List<ContentsContents>,
)

@Serializable
data class ContentsContents(
    val videoSecondaryInfoRenderer: VideoSecondaryInfoRenderer? = null,
)

@Serializable
data class VideoSecondaryInfoRenderer(
    val owner: Owner,
)

@Serializable
data class Owner(
    val videoOwnerRenderer: VideoOwnerRenderer,
)

@Serializable
data class VideoOwnerRenderer(
    val thumbnail: Thumbnail,
)


