package io.github.takusan23.chocodroid.tool

import java.io.PrintWriter
import java.io.StringWriter

object StacktraceToString {

    /**
     * スタックトレースを文字列に変換する
     *
     * @param throwable 捕まえた例外
     * @return printStackTrace()で出力される文字列
     * */
    fun stackTraceToString(throwable: Throwable): String {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        throwable.printStackTrace(pw)
        pw.flush()
        return sw.toString()
    }

}