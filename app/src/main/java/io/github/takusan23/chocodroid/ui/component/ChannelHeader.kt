package io.github.takusan23.chocodroid.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import io.github.takusan23.chocodroid.R
import io.github.takusan23.internet.data.channel.C4TabbedHeaderRenderer

/**
 * チャンネル画面のヘッダー部分
 *
 * @param header チャンネルAPIレスポンスのヘッダー部分のデータ
 * @param isAddedFavoriteCh お気に入りチャンネル登録済みならtrue
 * @param onAddFavoriteChClick お気に入りチャンネル押したら呼ばれる
 * @param onOpenBrowserClick ブラウザで開く押したとき
 * */
@Composable
fun ChannelHeader(
    header: C4TabbedHeaderRenderer,
    isAddedFavoriteCh: Boolean = false,
    onAddFavoriteChClick: () -> Unit,
    onOpenBrowserClick: () -> Unit,
) {

    Column {
        if (header.banner != null) {
            Image(
                painter = rememberImagePainter(data = header.banner!!.thumbnails.last().url),
                contentDescription = "ヘッダー",
                modifier = Modifier.fillMaxWidth()
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // アイコン
            Surface(
                shape = RoundedCornerShape(50),
                modifier = Modifier.padding(all = 10.dp)
            ) {
                Image(
                    painter = rememberImagePainter(data = header.avatar.thumbnails.last().url),
                    contentDescription = "アバター",
                    modifier = Modifier.size(60.dp)
                )
            }
            // チャンネル名と登録者数
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = header.title,
                    maxLines = 2,
                    fontSize = 20.sp
                )
                Text(
                    modifier = Modifier.padding(top = 5.dp),
                    maxLines = 1,
                    text = header.subscriberCountText.simpleText,
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            // 登録ボタン。登録済みならアウトラインにする？
            if (isAddedFavoriteCh) {
                OutlinedButton(
                    onClick = onAddFavoriteChClick,
                    modifier = Modifier
                        .padding(start = 5.dp)
                        .wrapContentWidth()
                ) {
                    Icon(painter = painterResource(id = R.drawable.ic_outline_done_24), contentDescription = null)
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(text = stringResource(id = R.string.already_added_favourite_list))
                }
            } else {
                Button(
                    onClick = onAddFavoriteChClick,
                    modifier = Modifier
                        .padding(start = 5.dp)
                        .wrapContentWidth()
                ) {
                    Icon(painter = painterResource(id = R.drawable.ic_outline_folder_special_24), contentDescription = null)
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(text = stringResource(id = R.string.add_favourite_list))
                }
            }
            // ブラウザで開く
            IconButton(
                modifier = Modifier
                    .padding(end = 10.dp),
                onClick = onOpenBrowserClick
            ) { Icon(painter = painterResource(id = R.drawable.ic_outline_open_in_browser_24), contentDescription = null) }
        }

    }


}
