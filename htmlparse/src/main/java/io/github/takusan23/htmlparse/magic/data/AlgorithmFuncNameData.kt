package io.github.takusan23.htmlparse.magic.data

import kotlinx.serialization.Serializable

/**
 * 関数名を入れたデータクラス
 *
 * @param swapFuncName CharArrayの指定位置Charを入れ替える関数の名前
 * @param substringFuncName 指定位置のCharを取り除く関数の名前
 * @param reverseFuncName CharArrayを逆順にする関数の名前
 * */
@Serializable
data class AlgorithmFuncNameData(
    val swapFuncName: String,
    val reverseFuncName: String,
    val substringFuncName: String,
)