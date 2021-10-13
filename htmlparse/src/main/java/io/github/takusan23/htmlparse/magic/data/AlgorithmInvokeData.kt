package io.github.takusan23.htmlparse.magic.data

import kotlinx.serialization.Serializable

/**
 * 関数を解析した結果
 *
 * @param funcName 関数名
 * @param secondParameterValue 第2引数の値
 * */
@Serializable
data class AlgorithmInvokeData(
    val funcName: String,
    val secondParameterValue: Int
)