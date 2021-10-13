package io.github.takusan23.htmlparse.magic

import io.github.takusan23.htmlparse.tool.SerializationTool
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString

/**
 * [io.github.takusan23.htmlparse.magic.data.AlgorithmFuncNameData]、[io.github.takusan23.htmlparse.magic.data.AlgorithmInvokeData]をJSON形式で保存するためのクラス
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
        return SerializationTool.Serialization.encodeToString(data)
    }

    /**
     * JSONをデータクラスに戻す
     * @param json JSON文字列。nullなら返り値もnull
     * @return データクラス
     * */
    inline fun <reified T> toData(json: String?): T? {
        return if (json != null) SerializationTool.Serialization.decodeFromString<T>(json) else null
    }

}