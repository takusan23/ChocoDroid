package io.github.takusan23.chocodroid.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import io.github.takusan23.chocodroid.R
import io.github.takusan23.internet.data.CommonVideoData

/**
 * カルーセルUI
 *
 * @param favoriteFolderDBEntity フォルダ情報
 * @param favoriteVideoList フォルダ内の動画
 * @param onLabelClick フォルダ名ラベルを押したときに呼ばれる
 * @param onVideoClick 動画押したとき。動画IDが渡されます
 * */
@Composable
fun FavoriteFolderVideoCarouselItem(
    modifier: Modifier = Modifier,
    folderName: String,
    folderId: Int,
    favoriteVideoList: List<CommonVideoData>,
    onLabelClick: (Int) -> Unit,
    onVideoClick: (String) -> Unit,
) {
    Column(modifier = modifier) {
        LeftStartTextButton(onClick = { onLabelClick(folderId) }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_outline_folder_special_24),
                contentDescription = null
            )
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(
                text = folderName,
                fontSize = 25.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                painter = painterResource(id = R.drawable.ic_outline_arrow_forward_24),
                contentDescription = null
            )
        }
        if (favoriteVideoList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) { Text(text = stringResource(id = R.string.favorite_folder_video_empty)) }
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                content = {
                    items(favoriteVideoList) {
                        // 各アイテム
                        FavoriteFolderVideoCarouselRowItem(
                            videoId = it.videoId,
                            videoTitle = it.videoTitle,
                            duration = it.duration ?: "--:--",
                            ownerName = it.ownerName,
                            thumbnailUrl = it.thumbnailUrl,
                            onClick = onVideoClick
                        )
                    }
                }
            )
        }
    }
}

/**
 * お気に入りフォルダ追加ボタン
 *
 * @param modifier [Modifier]
 * @param onClick 押したときに呼ばれる
 * */
@Composable
fun CreateFavoriteFolderItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier,
        interactionSource = remember { MutableInteractionSource() },
        indication = rememberRipple(),
        color = Color.Transparent,
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        content = {
            Column(
                modifier = Modifier
                    .width(150.dp)
                    .height(100.dp),
            ) {
                Icon(
                    modifier = Modifier
                        .weight(1f)
                        .align(alignment = Alignment.CenterHorizontally),
                    painter = painterResource(id = R.drawable.ic_outline_create_24),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null,
                )
                Text(
                    modifier = Modifier.padding(5.dp),
                    textAlign = TextAlign.Center,
                    text = stringResource(id = R.string.add_favorite_folder),
                )
            }
        }
    )
}

/**
 * カルーセル内の各項目
 *
 * @param videoId 動画ID
 * @param videoTitle タイトル
 * @param duration 動画時間
 * @param thumbnailUrl サムネURL
 * @param ownerName 投稿者
 * @param onClick 押したとき。動画IDが渡されます
 * */
@Composable
private fun FavoriteFolderVideoCarouselRowItem(
    videoId: String,
    videoTitle: String,
    duration: String,
    ownerName: String,
    thumbnailUrl: String,
    onClick: (String) -> Unit,
) {
    Surface(
        interactionSource = remember { MutableInteractionSource() },
        indication = rememberRipple(),
        color = Color.Transparent,
        onClick = { onClick(videoId) }
    ) {
        Column(modifier = Modifier.width(IntrinsicSize.Min)) {
            // Glideの代わりにCoilを試す
            Box(modifier = Modifier.padding(5.dp)) {
                Image(
                    modifier = Modifier
                        .width(150.dp)
                        .aspectRatio(1.7f)
                        .clip(RoundedCornerShape(10.dp)),
                    painter = rememberImagePainter(
                        data = thumbnailUrl,
                        builder = {
                            crossfade(true)
                        }
                    ),
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                )
                Text(
                    modifier = Modifier
                        .offset((-5).dp, (-5).dp)
                        .background(color = Color.Black.copy(0.5f), shape = RoundedCornerShape(2.dp))
                        .align(alignment = Alignment.BottomEnd),
                    color = Color.White,
                    text = duration
                )
            }
            Text(
                fontSize = 16.sp,
                text = videoTitle,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )
            Text(
                fontSize = 14.sp,
                text = ownerName,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }
}