package io.github.takusan23.htmlparse.tool

import kotlinx.serialization.json.Json

/** Kotlinx.serializationで使うやつ */
object SerializationTool {

    /** JSONパースするときに使う */
    val Serialization = Json {
        ignoreUnknownKeys = true
    }

}