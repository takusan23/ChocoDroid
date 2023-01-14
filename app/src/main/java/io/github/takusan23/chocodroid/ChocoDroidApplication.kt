package io.github.takusan23.chocodroid

import android.app.Application

/**
 * ChocoDroid の [Application]。アプリ作成時に一度だけ作成され、アプリが行きてる間ずっと生きている。
 *
 * プレイヤーを Application に持たせることで バックグラウンド / フォアグラウンド 切り替えができるように
 */
class ChocoDroidApplication : Application() {

    /** 動画プレイヤー */
    // val chocoDroidPlayer by lazy { ChocoDroidPlayer(this) }

    /** 動画読み込むやつ */
    // val chocoDroidContentLoader by lazy { ChocoDroidContentLoader(this, chocoDroidPlayer) }

    /**
     * プレイヤー、再生中コンテンツの破棄を行う。
     * もう再生しない場合に呼ぶ。
     */
    // fun playerDestroy() {
    //     chocoDroidPlayer.destroy()
    //     chocoDroidContentLoader.destroy()
    // }

    override fun onCreate() {
        super.onCreate()
        _instance = this
    }

    companion object {

        private var _instance: ChocoDroidApplication? = null

        /** [ChocoDroidApplication]を取得する */
        //  val instance: ChocoDroidApplication
        //      get() = _instance!!
    }
}