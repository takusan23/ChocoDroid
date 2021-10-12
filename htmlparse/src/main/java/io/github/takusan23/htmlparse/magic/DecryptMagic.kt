package io.github.takusan23.htmlparse.magic

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