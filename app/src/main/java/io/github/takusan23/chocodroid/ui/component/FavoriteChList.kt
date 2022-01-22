import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.database.entity.FavoriteChDBEntity

/**
 * チャンネル一覧
 *
 * @param channelList お気に入りチャンネルの配列
 * @param onClick 項目押したとき。チャンネルIDが渡されます
 * @param onMenuClick メニュー押したとき
 * */
@Composable
fun FavoriteChList(
    channelList: List<FavoriteChDBEntity>,
    onClick: (String) -> Unit,
    onMenuClick: (FavoriteChDBEntity) -> Unit,
) {
    LazyColumn(content = {
        items(channelList) { channelData ->
            FavoriteChItemList(
                favoriteChDBEntity = channelData,
                onClick = onClick,
                onMenuClick = onMenuClick
            )
        }
    })
}

/**
 * チャンネル一覧の各項目
 *
 * @param favoriteChDBEntity お気に入りチャンネルのデータ
 * @param onClick 項目押したとき。チャンネルIDが渡されます
 * @param onMenuClick メニュー押したとき
 * */
@Composable
private fun FavoriteChItemList(
    favoriteChDBEntity: FavoriteChDBEntity,
    onClick: (String) -> Unit,
    onMenuClick: (FavoriteChDBEntity) -> Unit,
) {
    Surface(
        interactionSource = remember { MutableInteractionSource() },
        indication = rememberRipple(),
        color = Color.Transparent,
        onClick = { onClick(favoriteChDBEntity.channelId) }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                modifier = Modifier
                    .size(70.dp)
                    .padding(10.dp)
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
                modifier = Modifier.weight(1f),
                text = favoriteChDBEntity.name,
                maxLines = 2
            )
            IconButton(
                modifier = Modifier
                    .padding(10.dp),
                onClick = { onMenuClick(favoriteChDBEntity) }
            ) { Icon(painter = painterResource(id = R.drawable.ic_outline_more_vert_24), contentDescription = null) }
        }
    }
}