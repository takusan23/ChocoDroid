package io.github.takusan23.internet.magic

import io.github.takusan23.internet.data.watchpage.DecryptData
import io.github.takusan23.internet.data.watchpage.WatchPageData
import io.github.takusan23.internet.magic.UnlockMagic.getParamFixJavaScriptCode
import okhttp3.HttpUrl.Companion.toHttpUrl

/**
 * ダウンロード速度がくっそ遅いのを解除する
 *
 * そのままだとものすごくダウンロードが遅いので（先読みどころか現在位置の取得すら出来てない）
 *
 * - URLから「n」パラメーターを取得します。
 * - base.js内にパラメーターを修正する関数があるので探す。[getParamFixJavaScriptCode]
 * - なんらかの方法で上記のJavaScriptコードを実行する（多分最短はAndroidなどのWebView）
 * - 実行した結果、パラメーターが戻り値で取得できるので、nパラメーターに入れ替える
 * - これで快適に見れるはず。
 *
 * */
object UnlockMagic {

    /**
     * [DecryptMagic.decrypt]でURLを作成すると、たしかに動画は再生される。
     *
     * されるんだけどものすごく遅い。
     *
     * ブラウザ追いかけてると、JSのオブジェクトに書いてあるURLと実際に取得しているURLが違う。
     *
     * よく見ると、URLのnパラメータ？をJavaScriptで変更してからリクエストしている模様。
     *
     * というわけでそのパラメータを書き換えてる部分のコードを正規表現で引き抜く
     *
     * @param baseJS base.jsの内容
     * @return 実際に書き換えてる部分。JSコードとしては不十分で、文字列の先頭にconst fix = とか付けないとダメです
     * */
    fun getParamFixJavaScriptCode(baseJS: String): String {
        /**
         * nパラメーターを引数に取っていじって返す正規表現。雑な解説
         *
         * - function(a)から始まる
         * - (アルファベット1文字).split("") が含まれている
         * - (アルファベット1文字)=[ が含まれている
         * */
        val regex = """function\(a\)\{(.*?)=[a-z].split\(""\)(.*?)=(\[)([\s\S]*?)(\};)""".toRegex()
        return regex.find(baseJS)!!.value
    }

    /**
     * URLを修正することでダウンロード速度制限を解除する。なんだけどJavaScript実行環境が必要。(複雑すぎてKotlinで実装できない気がする)
     *
     * なので、現状はAndroid側でJavaScriptを実行してこっちに戻すって感じ？。一つのモジュールで完結させたかったんだけどなぁ
     *
     * 動画である必要があります。生放送では実行しないでください。
     *
     * @param runJavaScriptResult 実行するべきJavaScriptコードが渡されるのでAndroidのWebViewで実行して、戻り値をください
     * @param urlParamFixJSCode [DecryptData.urlParamFixJSCode]を渡して
     * @param watchPageData 視聴ページ情報
     * @return 修正した視聴ページ情報
     * */
    suspend fun fixUrlParam(watchPageData: WatchPageData, urlParamFixJSCode: String, runJavaScriptResult: suspend (String) -> String): WatchPageData {
        // URLのパラメーター
        val fixParamName = "n"
        val fixParamValue = watchPageData.contentUrlList.first().videoTrackUrl!!.toHttpUrl().queryParameter(fixParamName)
        // 実行するJavaScript
        val evalJavaScriptCode = """
             const fix = $urlParamFixJSCode;
             fix("$fixParamValue");
         """.trimIndent()
        // JavaScriptエンジンで実行した結果を待つ
        val fixedValue = runJavaScriptResult(evalJavaScriptCode)
        return watchPageData.copy(
            contentUrlList = watchPageData.contentUrlList.map {
                it.copy(
                    videoTrackUrl = it.videoTrackUrl!!.toHttpUrl().newBuilder().setQueryParameter(fixParamName, fixedValue).build().toString(),
                    audioTrackUrl = it.audioTrackUrl!!.toHttpUrl().newBuilder().setQueryParameter(fixParamName, fixedValue).build().toString()
                )
            }
        )
    }

}