package io.github.takusan23.chocodroid.ui.component.tool

import android.app.Activity
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

/**
 * スリープモードにしない
 *
 * 多分ComposeViewがActivityに無いとうまく動きません
 *
 * @param isEnable スリープを無効にするならtrue
 * */
@Composable
fun SetActivitySleepComposeApp(isEnable: Boolean = true) {
    val context = LocalContext.current
    if (context is Activity) {
        LaunchedEffect(key1 = isEnable, block = {
            if (isEnable) {
                context.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            } else {
                context.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        })
        // 終了時
        DisposableEffect(key1 = Unit, effect = {
            onDispose {
                context.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        })
    }
}