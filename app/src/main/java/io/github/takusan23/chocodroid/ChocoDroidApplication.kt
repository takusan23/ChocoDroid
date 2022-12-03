package io.github.takusan23.chocodroid

import android.app.Application
import io.github.takusan23.chocodroid.player.ChocoDroidPlayer

/**
 * ChocoDroid の [Application]。アプリ作成時に一度だけ作成され、アプリが行きてる間ずっと生きている。
 *
 * プレイヤーを Application に持たせることで バックグラウンド / フォアグラウンド 切り替えができるように
 */
class ChocoDroidApplication : Application() {

    /** 動画プレイヤー */
    val chocoDroidPlayer by lazy { ChocoDroidPlayer(this) }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {

        /** [ChocoDroidApplication]を取得する */
        var instance: ChocoDroidApplication? = null
            private set
    }
}