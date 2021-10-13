package io.github.takusan23.htmlparse.magic

import io.github.takusan23.htmlparse.magic.data.AlgorithmFuncNameData
import io.github.takusan23.htmlparse.magic.data.AlgorithmInvokeData
import java.util.ArrayList

/**
 * 暗号化を解除する。
 *
 * なお 文字列を逆順にする、指定した文字列の部分を取得する、文字を入れ替える が実装できれば勝ち
 *
 * ソースはようつべ視聴ページ内base.jsの、prototype.getAvailableAudioTracks の近くに書いてある。
 *
 * そしたら、上記の関数たちがとこから呼ばれているかを調べるために「a.split("");」で検索をかけます。
 *
 * 作成時：2021/10/11
 * */
object DecryptMagic {

    /**
     * sinパラメータの値を作成する
     *
     * @param signatureText 暗号化された文字列
     * @return URLにsinパラメーターとして付与してください。
     * */
    fun decrypt(signatureText: String): String {
        val textList = signatureText.toCharArray().toMutableList()

        substring(textList, 1)
        swap(textList, 53)
        substring(textList, 3)
        swap(textList, 10)
        swap(textList, 5)
        swap(textList, 12)
        substring(textList, 3)
        reverse(textList, 37)

        return textList.joinToString(separator = "")
    }

    /**
     * sinパラメータの値を作成する。[AlgorithmParser]を利用した動的JS解析対応版
     *
     * @param invokeList 文字列操作を呼ぶ順番に合わせた配列
     * @param algorithmFuncNameData 文字列操作の関数名
     * @param signatureText 暗号化された文字列
     * @return URLにsinパラメーターとして付与してください。
     * */
    fun decrypt(signatureText: String, algorithmFuncNameData: AlgorithmFuncNameData, invokeList: List<AlgorithmInvokeData>): String {
        // 難読化されてる関数名とKotlinで実装した関数本体とのペア
        val funcNameToFuncMap = listOf(
            algorithmFuncNameData.swapFuncName to ::swap,
            algorithmFuncNameData.reverseFuncName to ::reverse,
            algorithmFuncNameData.substringFuncName to ::substring
        )
        // 関数を呼ぶ順番に合わせた配列を使って復号化する
        val textList = signatureText.toCharArray().toMutableList()
        invokeList.forEach { invokeData ->
            funcNameToFuncMap.find { map -> invokeData.funcName == map.first }!!.second.invoke(textList, invokeData.secondParameterValue)
        }
        return textList.joinToString("")
    }

    /** Charの配列を入れ替える */
    private fun swap(a: MutableList<Char>, b: Int) {
        val c = a[0]
        a[0] = a[b % a.size]
        a[b % a.size] = c
    }

    /** 文字を逆順に。第２引数は使ってないけど他と合わせるために */
    private fun reverse(a: MutableList<Char>, b: Int) {
        a.reverse()
    }

    /** 最初から指定した数だけ文字を取り除く */
    private fun substring(a: MutableList<Char>, b: Int) {
        val list = a.drop(b)
        a.clear()
        a.addAll(list)
    }

}