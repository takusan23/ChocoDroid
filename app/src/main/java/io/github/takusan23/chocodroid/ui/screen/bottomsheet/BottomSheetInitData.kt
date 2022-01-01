package io.github.takusan23.chocodroid.ui.screen.bottomsheet

/**
 * BottomSheet表示時にわたすデータ
 *
 * Compose版Navigationを使おうと思ったが、
 *
 * バックスタックで保持してほしくないのとURLのパラメータで渡していくのは限界、、、なので
 *
 * このクラスでは一つしか引数を持ちません、ので他の画面用にこのクラスを継承して使えばいいんじゃないかと思います。
 *
 * @param screen 何の画面を表示させるのか
 * */
open class BottomSheetInitData(val screen: BottomSheetScreenList) {
    /** ナビゲーション先の定義 */
    enum class BottomSheetScreenList {
        /** お気に入りフォルダ作成 */
        AddFavoriteFolder,

        /** 画質変更 */
        QualityChange,

        /** 動画メニュー表示 */
        VideoListMenu,

        /** お気に入りフォルダへ追加 */
        AddVideoToFavoriteFolder,

        /** ダウンロード画面 */
        VideoDownload,
    }

}