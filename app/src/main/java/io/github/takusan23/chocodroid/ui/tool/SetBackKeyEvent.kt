package io.github.takusan23.chocodroid.ui.tool

import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/**
 * 戻るキーをComposeで監視する
 *
 * 基本的に利用したいときだけこのコンポーズ関数を呼んで、使わない際は呼ばないように条件分岐して下さい。
 *
 * ```
 * if(isMiniPlayer){
 *     // isEnabled の代わり
 *     SetBackKeyEvent { close() }
 * }else{
 *     // 何もしない
 * }
 * ```
 *
 * @param isEnabled 有効にするならtrue
 * @param onBackPress バックキー押したらtrue
 * */
@Composable
fun SetBackKeyEvent(
    isEnabled: Boolean = true,
    onBackPress: () -> Unit,
) {
    val context = LocalContext.current

    /** 戻るキーコールバック */
    val backCallback = remember {
        object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                onBackPress()
            }
        }
    }
    // 戻るキーコールバックを有効にするか。
    backCallback.isEnabled = isEnabled
    // 戻るキーコールバックを登録して、Composeが破棄されたら解除する
    DisposableEffect(key1 = Unit, effect = {
        if (context is ComponentActivity) {
            context.onBackPressedDispatcher.addCallback(backCallback)
        }
        onDispose {
            backCallback.remove()
        }
    })

}