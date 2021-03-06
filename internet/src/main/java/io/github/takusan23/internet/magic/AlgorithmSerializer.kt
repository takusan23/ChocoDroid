package io.github.takusan23.internet.magic

import io.github.takusan23.internet.tool.SerializationTool
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString

/**
 * [io.github.takusan23.internet.magic.data.AlgorithmFuncNameData]、[io.github.takusan23.internet.magic.data.AlgorithmInvokeData]をJSON形式で保存するためのクラス
 *
 * inline。呼び出し元にコードが展開されるってことらしい。
 * */
object AlgorithmSerializer {

    /**
     * データクラスをJSONに変換する
     * @param data データクラス
     * @return JSON文字列
     * */
    inline fun <reified T> toJSON(data: T): String {
        return SerializationTool.jsonSerialization.encodeToString(data)
    }

    /**
     * JSONをデータクラスに戻す
     * @param json JSON文字列。nullなら返り値もnull
     * @return データクラス
     * */
    inline fun <reified T> toData(json: String?): T? {
        return if (json != null) SerializationTool.jsonSerialization.decodeFromString<T>(json) else null
    }

}