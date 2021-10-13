package io.github.takusan23.htmlparse.html.data

import io.github.takusan23.htmlparse.magic.DecryptMagic
import io.github.takusan23.htmlparse.magic.data.AlgorithmFuncNameData
import io.github.takusan23.htmlparse.magic.data.AlgorithmInvokeData
import java.net.URLDecoder

/**
 * 視聴ページ取得関数の戻り値
 *
 * @param watchPageJSONResponseData 動画情報など
 * @param baseJsURL 暗号解除のJSコードのURL
 * @param algorithmFuncNameData 暗号解除で使うの文字列操作する関数名を入れたデータクラス
 * @param decryptInvokeList 暗号解除で使う関数を呼ぶ順番に入れた配列
 * */
data class WatchPageData(
    val watchPageJSONResponseData: WatchPageJSONResponseData,
    val baseJsURL: String,
    val algorithmFuncNameData: AlgorithmFuncNameData,
    val decryptInvokeList: List<AlgorithmInvokeData>,
) {

    /**
     * 復号化済みURLを返します
     * @param signatureCipher 署名とURLがついてる文字列
     * @return 復号化済みURL。アクセスできます
     * */
    fun decryptURL(signatureCipher: String): String {
        val params = signatureCipher
            .split("&")
            .map { URLDecoder.decode(it.split("=")[1], "utf-8") } // key=value の文字列を value だけにしてパーセントエンコーディングを戻す
        val decryptKey = DecryptMagic.decrypt(params.first(), algorithmFuncNameData, decryptInvokeList)
        return "${params[2]}&sig=$decryptKey"
    }

}