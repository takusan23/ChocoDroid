package io.github.takusan23.chocodroid.ui.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.takusan23.chocodroid.R

/**
 * ダウンロードしたコンテンツを端末のフォルダへコピーするボタン
 *
 * @param onClick 押したとき
 * */
@Composable
fun ExportDeviceMediaFolderButton(onClick: () -> Unit) {
    LeftStartTextButton(
        modifier = Modifier
            .padding(5.dp),
        onClick = onClick,
        content = {
            Icon(painter = painterResource(id = R.drawable.ic_outline_drive_file_move_24), contentDescription = null)
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = stringResource(id = R.string.video_list_menu_export_media_folder))
        }
    )

}