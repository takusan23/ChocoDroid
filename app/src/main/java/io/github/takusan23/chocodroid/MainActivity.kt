package io.github.takusan23.chocodroid

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.net.toUri
import io.github.takusan23.chocodroid.ui.screen.ChocoDroidMainScreen
import io.github.takusan23.chocodroid.viewmodel.MainScreenViewModel

/**
 * 最初に表示する画面。
 *
 * Compose楽しみだなー
 * */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel by viewModels<MainScreenViewModel>()
            ChocoDroidMainScreen(viewModel)
        }

    }

}
