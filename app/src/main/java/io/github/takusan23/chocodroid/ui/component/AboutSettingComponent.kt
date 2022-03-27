package io.github.takusan23.chocodroid.ui.component

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.tool.TimeFormatTool
import io.github.takusan23.chocodroid.ui.screen.setting.AboutSettingScreen

/** このアプリについて のヘッダー部 */
@Composable
fun AboutSettingHeader() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .size(100.dp)
                .padding(10.dp),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
            painter = painterResource(id = R.drawable.chocodroid_white_android),
            contentDescription = null
        )
        Text(
            text = stringResource(id = R.string.app_name),
            fontSize = 25.sp
        )
    }
}

/** アプリバージョンなど */
@Composable
fun AboutSettingAppInfo() {
    val context = LocalContext.current
    val appInfo = remember { context.packageManager.getPackageInfo(context.packageName, 0) }
    // アプリ更新日時、この値は string.xml ではなく build.gradle.kts によって作成されます。
    val buildDate = TimeFormatTool.unixTimeToFormatText(stringResource(id = R.string.build_date).toLong())

    Column(modifier = Modifier.padding(start = 10.dp, end = 10.dp)) {
        RoundedIconButton(
            modifier = Modifier,
            mainText = appInfo.versionName,
            subText = stringResource(id = R.string.version),
            iconPainter = painterResource(id = R.drawable.ic_outline_info_24),
        )
        Spacer(modifier = Modifier.padding(5.dp))
        RoundedIconButton(
            modifier = Modifier,
            mainText = buildDate,
            subText = stringResource(id = R.string.build_update_date),
            iconPainter = painterResource(id = R.drawable.ic_outline_today_24),
        )
        Spacer(modifier = Modifier.padding(5.dp))
        RoundedIconButton(
            modifier = Modifier,
            mainText = stringResource(id = R.string.app_name),
            subText = stringResource(id = R.string.source_code),
            iconPainter = painterResource(id = R.drawable.ic_outline_open_in_browser_24),
            onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, AboutSettingScreen.SourceCodeGitHubUrl.toUri())) }
        )
        Spacer(modifier = Modifier.padding(5.dp))
    }

}
