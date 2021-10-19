package io.github.takusan23.htmlparse.data.watchpage

import kotlinx.serialization.Serializable

/**
 * 今の所アイコンの画像URL取得のためにパースしてる
 * */
@Serializable
data class WatchPageJSONInitialData(
    val contents: Contents
)

@Serializable
data class Contents(
    val twoColumnWatchNextResults: TwoColumnWatchNextResults
)

@Serializable
data class TwoColumnWatchNextResults(
    val results: Results
)

@Serializable
data class Results(
    val results: ResultsResults
)

@Serializable
data class ResultsResults(
    val contents: List<ContentsContents>,
)

@Serializable
data class ContentsContents(
    val videoSecondaryInfoRenderer: VideoSecondaryInfoRenderer? = null
)

@Serializable
data class VideoSecondaryInfoRenderer(
    val owner: Owner
)

@Serializable
data class Owner(
    val videoOwnerRenderer: VideoOwnerRenderer
)

@Serializable
data class VideoOwnerRenderer(
    val thumbnail: Thumbnail
)


