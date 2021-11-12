package io.github.takusan23.chocodroid.ui.screen.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.takusan23.chocodroid.R
import io.github.takusan23.chocodroid.ui.component.AboutSettingAppInfo
import io.github.takusan23.chocodroid.ui.component.AboutSettingHeader
import io.github.takusan23.chocodroid.ui.component.BackButtonSmallTopBar
import io.github.takusan23.chocodroid.ui.component.M3Scaffold

object AboutSettingScreen {
    /** GitHubリンク */
    const val SourceCodeGitHubUrl = "https://github.com/takusan23/ChocoDroid"

    /** Twitterリンク */
    const val TwitterUrl = "https://twitter.com/takusan__23"

    /** Twitter ID */
    const val TwitterId = "@takusan__23"

    /** 更新した日 */
    const val UpdateReleaseDate = "2021-11-xx"
}

/**
 * このアプリについて
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutSettingScreen(onBack: () -> Unit) {
    M3Scaffold(
        topBar = {
            BackButtonSmallTopBar(
                title = { Text(text = stringResource(id = R.string.setting_kono_app_title)) },
                onBack = onBack
            )
        },
        content = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                // ヘッダー部
                AboutSettingHeader()
                // スペース
                Spacer(modifier = Modifier.height(50.dp))
                // バージョン情報
                AboutSettingAppInfo()
            }
        }
    )
}