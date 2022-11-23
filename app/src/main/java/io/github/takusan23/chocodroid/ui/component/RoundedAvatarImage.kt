package io.github.takusan23.chocodroid.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest

/**
 * 丸いアバターアイコン
 *
 * @param modifier [Modifier]
 * @param avatarUrl 画像のURL
 * */
@Composable
fun RoundedAvatarImage(
    modifier: Modifier,
    avatarUrl: String,
) {
    Image(
        modifier = modifier
            .clip(RoundedCornerShape(50)),
        painter = rememberAsyncImagePainter(
            ImageRequest.Builder(LocalContext.current)
                .data(avatarUrl)
                .apply { crossfade(true) }
                .build()
        ),
        contentScale = ContentScale.Crop,
        contentDescription = null
    )
}