package io.github.takusan23.chocodroid

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import io.github.takusan23.chocodroid.ui.screen.ChocoDroidMainScreen
import io.github.takusan23.chocodroid.viewmodel.MainScreenViewModel

/**
 * 最初に表示する画面。
 *
 * Compose楽しみだなー
 * */
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainScreenViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent { ChocoDroidMainScreen(viewModel) }

        /**
         * 共有から起動した
         *
         * ただし、画面回転した場合は動かさない
         * */
        if (savedInstanceState == null) {
            // 共有から起動
            lunchFromShare()
            // ブラウザから起動
            launchFromBrowser()
        }

    }

    /** ブラウザから起動 */
    private fun launchFromBrowser() {
        intent.data?.toString()?.apply { viewModel.loadWatchPage(this) }
    }

    /** 共有から起動したとき */
    private fun lunchFromShare() {
        if (Intent.ACTION_SEND == intent.action) {
            val extras = intent.extras
            // URLを開く
            val url = extras?.getCharSequence(Intent.EXTRA_TEXT) ?: return
            viewModel.loadWatchPage(url.toString())
        }
    }

}
