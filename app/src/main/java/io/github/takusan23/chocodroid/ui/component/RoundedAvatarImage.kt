package io.github.takusan23.chocodroid.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberImagePainter

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
        painter = rememberImagePainter(
            data = avatarUrl,
            builder = {
                crossfade(true)
            }
        ),
        contentScale = ContentScale.Crop,
        contentDescription = null
    )
}