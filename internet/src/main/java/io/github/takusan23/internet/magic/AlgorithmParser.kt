package io.github.takusan23.internet.magic

import io.github.takusan23.internet.magic.data.AlgorithmFuncNameData
import io.github.takusan23.internet.magic.data.AlgorithmInvokeData

/**
 * HTMLの変更で署名復号アルゴリズムが変化しても対応できるようにする
 *
 * 正規表現なんもわからん。Regex←単語かっこいい
 * */
object AlgorithmParser {

    /**
     * 文字列操作をしている関数の名前を取得する関数
     *
     * @param baseJS base.jsの内容
     * @return 関数名たち
     * */
    fun funcNameParser(baseJS: String): AlgorithmFuncNameData {
        /**
         * とりあえず 文字の入れ替え、文字を逆順、文字を消す 関数の名前を取り出す
         * 上記3つの処理自体は多分変わってない、webpackあたりの技術で圧縮された際に名前が変わるだけっぽい？
         *
         * でも上記3つの関数の呼ばれる順番が変わっている。ぶっ壊れるのはそのせい
         * */
        val swapFuncGetRegex = "(.{2}):function\\(a,b\\)\\{var c=a\\[0\\];a\\[0\\]=a\\[b%a\\.length\\];a\\[b%a\\.length\\]=c\\}".toRegex()
        val reverseFuncGetRegex = "(.{2}):function\\(a\\)\\{a\\.reverse\\(\\)\\}".toRegex()
        val dropFuncGetRegex = "(.{2}):function\\(a,b\\)\\{a\\.splice\\(0,b\\)\\}".toRegex()
        // 関数名を取る
        val swapFuncName = swapFuncGetRegex.find(baseJS)!!.groupValues[1]
        val reverseFuncName = reverseFuncGetRegex.find(baseJS)!!.groupValues[1]
        val dropFuncName = dropFuncGetRegex.find(baseJS)!!.groupValues[1]
        // 返す
        return AlgorithmFuncNameData(swapFuncName, reverseFuncName, dropFuncName)
    }

    /**
     * 文字の入れ替え、文字を逆順、文字を消す の処理部分を解析する。
     *
     * どこの文字を入れ替えているのか、何番目の文字を消しているのかなどを解析する
     *
     * @param baseJS base.jsの内容
     * @return 関数を呼ぶ順番
     * */
    fun algorithmFuncParser(baseJS: String): List<AlgorithmInvokeData> {
        // 多分 a.split("");のコードが一箇所しか無いのでそこから正規表現を組み立てる
        val decryptAlgorithmFuncCodeRegex = "function\\(a\\)\\{a=a\\.split\\(\"\"\\);(.*?)return a\\.join\\(\"\"\\)\\}".toRegex()
        // カッコの中身なので配列で結果を貰って1個目の要素
        val algorithmCode = decryptAlgorithmFuncCodeRegex.find(baseJS)!!.groupValues[1]
        // 関数の前の部分を消す。関数だけにする
        val removeObjectNameRegex = ".{2}\\.".toRegex()
        val removeObjectNameAlgorithmCode = algorithmCode.replace(removeObjectNameRegex, "")
        // 関数ごとに配列にする
        val funcList = removeObjectNameAlgorithmCode.split(";").filter { it.isNotEmpty() }
        // 数字を見つける正規表現
        val numberRegex = "a,(\\d{1,})".toRegex()
        val invokeList = funcList.map { funcCode -> AlgorithmInvokeData(funcCode.substring(0, 2), numberRegex.find(funcCode)!!.groupValues[1].toInt()) }
        return invokeList
    }


    /**
     * 正規表現のエスケープ必須文字列をエスケープ済み文字列に変換する
     *
     * テストコードで使ってます
     *
     * @param rawText エスケープ処理前文字列
     * @return エスケープ処理後文字列
     * */
    fun replaceRegexEscapeChar(rawText: String): String {
        val needEscapeCharList = listOf("/", "*", "+", ".", "?", "{", "}", "(", ")", "[", "]", "^", "$", "-", "|")
        var afterText = rawText
        needEscapeCharList.forEach {
            afterText = afterText.replace(it, "\\$it")
        }
        return afterText
    }

}