package io.github.takusan23.chocodroid.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.database.entity.FavoriteChDBEntity

/**
 * お気に入りチャンネルのカルーセルUI
 *
 * @param channelList お気に入りチャンネル情報配列
 * @param onChannelClick チャンネルアイコン押したとき。チャンネルIDが渡されます
 * @param onLabelClick チャンネル一覧遷移ボタンを押したとき
 * */
@Composable
fun FavoriteChCarouselItem(
    channelList: List<FavoriteChDBEntity>,
    onChannelClick: (String) -> Unit,
    onLabelClick: () -> Unit,
) {
    LeftStartTextButton(onClick = onLabelClick) {
        Icon(
            painter = painterResource(id = R.drawable.ic_outline_folder_special_24),
            contentDescription = null
        )
        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
        Text(
            text = stringResource(id = R.string.favorite_channel),
            fontSize = 25.sp
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            painter = painterResource(id = R.drawable.ic_outline_arrow_forward_24),
            contentDescription = null
        )
    }
    LazyRow(content = {
        items(channelList) { channel ->
            FavoriteChCarouselRowItem(
                favoriteChDBEntity = channel,
                onClick = onChannelClick
            )
        }
    })
}

/**
 * カルーセルUIの各項目。Twitterのフリートみたいなやつ
 *
 * @param favoriteChDBEntity お気に入りチャンネル情報
 * @param onClick 押したとき。チャンネルIDが渡されます
 * */
@Composable
private fun FavoriteChCarouselRowItem(
    favoriteChDBEntity: FavoriteChDBEntity,
    onClick: (String) -> Unit,
) {
    Surface(
        interactionSource = remember { MutableInteractionSource() },
        indication = rememberRipple(),
        onClick = { onClick(favoriteChDBEntity.channelId) }
    ) {
        Column(
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(50)),
                painter = rememberImagePainter(
                    data = favoriteChDBEntity.thumbnailUrl,
                    builder = {
                        crossfade(true)
                    }
                ),
                contentScale = ContentScale.Crop,
                contentDescription = null
            )
            Text(
                modifier = Modifier
                    .width(70.dp)
                    .padding(top = 5.dp),
                text = favoriteChDBEntity.name,
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                maxLines = 1
            )
        }
    }
}