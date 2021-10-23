package io.github.takusan23.chocodroid.ui.component.videodetail

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.data.DownloadRequestData
import io.github.takusan23.internet.data.watchpage.WatchPageData

/**
 * ダウンロード画面
 *
 * @param watchPageData 動画情報
 * @param onDownloadClick 動画ダウンロードボタンを押したときに呼ばれる。渡されるデータは画質の指定など
 * */
@Composable
fun VideoDetailDownloadScreen(
    watchPageData: WatchPageData,
    onDownloadClick: (DownloadRequestData) -> Unit = {},
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            fontSize = 20.sp,
            text = stringResource(id = R.string.download),
        )

        Button(
            modifier = Modifier.padding(5.dp),
            elevation = ButtonDefaults.elevation(0.dp),
            shape = RoundedCornerShape(50),
            onClick = {
                onDownloadClick(DownloadRequestData(watchPageData.watchPageJSONResponseData.videoDetails.videoId))
            },
            content = {
                Icon(painter = painterResource(id = R.drawable.ic_outline_file_download_24), contentDescription = null)
                Text(text = stringResource(id = R.string.download))
            }
        )

    }
}