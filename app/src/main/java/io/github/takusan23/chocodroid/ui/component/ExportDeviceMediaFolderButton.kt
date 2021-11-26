package io.github.takusan23.chocodroid.ui.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import io.github.takusan23.chocodroid.R

/**
 * ダウンロードしたコンテンツを端末のフォルダへコピーするボタン
 *
 * @param modifier [Modifier]
 * @param onClick 押したとき
 * */
@Composable
fun ExportDeviceMediaFolderButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    LeftStartTextButton(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        content = {
            Icon(painter = painterResource(id = R.drawable.ic_outline_drive_file_move_24), contentDescription = null)
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = stringResource(id = R.string.video_list_menu_export_media_folder))
        }
    )
}