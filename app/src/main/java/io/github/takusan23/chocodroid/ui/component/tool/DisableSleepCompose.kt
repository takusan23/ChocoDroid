package io.github.takusan23.chocodroid.ui.component.tool

import android.app.Activity
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

/**
 * スリープモードにしない
 *
 * 多分ComposeViewがActivityに無いとうまく動きません
 * */
@Composable
fun DisableSleepComposeApp() {
    val context = LocalContext.current
    if (context is Activity) {
        context.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        // 終了時
        DisposableEffect(key1 = Unit, effect = {
            onDispose {
                context.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        })
    }
}