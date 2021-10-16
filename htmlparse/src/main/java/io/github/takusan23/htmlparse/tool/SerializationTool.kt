package io.github.takusan23.htmlparse.tool

import kotlinx.serialization.json.Json

/** Kotlinx.serializationで使うやつ */
object SerializationTool {

    /** JSONパースするときに使う */
    val jsonSerialization = Json {
        // JSONのキーが全部揃ってなくてもパース
        ignoreUnknownKeys = true
        // data class の省略時の値を使うように
        encodeDefaults = true
    }

}