package io.github.takusan23.chocodroid

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import io.github.takusan23.chocodroid.service.SmoothBackgroundPlayService
import io.github.takusan23.chocodroid.ui.screen.ChocoDroidMainScreen
import io.github.takusan23.chocodroid.ui.tool.PictureInPictureTool
import io.github.takusan23.chocodroid.viewmodel.MainScreenViewModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

/**
 * 最初に表示する画面。
 *
 * Compose楽しみだなー
 * */
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainScreenViewModel>()
    private val pictureInPictureTool by lazy {
        PictureInPictureTool(
            activity = this,
            onPictureInPictureModeChange = { viewModel.setPictureInPictureMode(it) }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ChocoDroidMainScreen(
                viewModel = viewModel,
                onPictureInPictureModeChange = { pictureInPictureTool.enterPictureInPicture() }
            )
        }

        lifecycleScope.launch {
            // ピクチャーインピクチャーの値を監視する
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.pictureInPictureRect.filterNotNull().collect {
                    pictureInPictureTool.setPictureInPictureRect(it)
                }
            }
        }
        lifecycleScope.launch {
            // 再生中のみピクチャーインピクチャーを有効にする
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                ChocoDroidApplication.instance.chocoDroidPlayer.playbackStateFlow.collect {
                    pictureInPictureTool.isEnablePictureInPicture = ChocoDroidApplication.instance.chocoDroidPlayer.isContentPlaying
                }
            }
        }

        // 共有から起動した
        // ただし、画面回転した場合は動かさない
        if (savedInstanceState == null) {
            // 共有から起動
            launchFromShare()
            // ブラウザから起動
            launchFromBrowser()
        }

    }

    /** アプリ起動中に他アプリから共有で開いた場合に呼ばれる。launchMode=singleTask */
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            launchFromShare(intent)
        }
    }

    /** フォアグラウンドへ戻った際は終了 */
    override fun onResume() {
        super.onResume()
        // ピクチャーインピクチャーに入っても呼ばれる？らしいのでやめる
        if (!pictureInPictureTool.isPictureInPicture) {
            SmoothBackgroundPlayService.stopService(this)
        }
    }

    /** バックグラウンド再生へ */
    override fun onPause() {
        super.onPause()
        // 視聴行動中のみ
        if (ChocoDroidApplication.instance.chocoDroidPlayer.isContentPlaying) {
            SmoothBackgroundPlayService.startService(this)
        }
    }

    /** ホームボタン押したら */
    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        pictureInPictureTool.onUserLeaveHint()
    }

    /** ブラウザから起動 */
    private fun launchFromBrowser() {
        val url = intent.data?.toString() ?: return
        ChocoDroidApplication.instance.chocoDroidContentLoader.loadWatchPage(url)
    }

    /** 共有から起動したとき */
    private fun launchFromShare(intent: Intent = this@MainActivity.intent) {
        if (Intent.ACTION_SEND == intent.action) {
            val extras = intent.extras
            // URLを開く
            val url = extras?.getCharSequence(Intent.EXTRA_TEXT) ?: return
            ChocoDroidApplication.instance.chocoDroidContentLoader.loadWatchPage(url.toString())
        }
    }

}
