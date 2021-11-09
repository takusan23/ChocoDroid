package io.github.takusan23.internet.data.watchpage

import io.github.takusan23.internet.magic.DecryptMagic
import io.github.takusan23.internet.magic.data.AlgorithmFuncNameData
import io.github.takusan23.internet.magic.data.AlgorithmInvokeData
import java.net.URLDecoder

/**
 * 動画URLを作成する際のパラメーターを解読する情報を入れたデータクラス
 *
 * 復号化する関数もあります
 *
 * @param baseJsURL 暗号解除のJSコードのURL
 * @param algorithmFuncNameData 暗号解除で使うの文字列操作する関数名を入れたデータクラス
 * @param decryptInvokeList 暗号解除で使う関数を呼ぶ順番に入れた配列
 * @param urlParamFixJSCode どうやらURLのあるパラメーターの値を変更しないと、ダウンロード速度制限をサーバーでかけてるみたいなので、それを解除するJavaScriptコード
 * */
data class DecryptData(
    val baseJsURL: String,
    val algorithmFuncNameData: AlgorithmFuncNameData,
    val decryptInvokeList: List<AlgorithmInvokeData>,
    val urlParamFixJSCode: String,
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