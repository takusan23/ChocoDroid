# magic
`signatureCipher`を正しいURLへ変換する関数と、(DecryptMagic)
変換後のURLだとダウンロードが遅すぎて実用レベルに達しないのでそれも直す関数があります。(UnlockMagic)

## DecryptMagic
`signatureCipher`の文字を入れ替えたりすることで正規のURLが出来上がる。
正規のURLとか言ってるけどめっちゃダウンロードが遅い。
こっちはまだマシなのでKotlinで実装出来ますが...

## UnlockMagic
URLのパラメーターを修正することでダウンロード速度の制限が解除されます。が、そのパラメーターを書き換えてる部分のJavaScriptコードが
すごく複雑な上に、型がないで有名なJavaScriptなので配列に何でもかんでも入れててマジ無理。Kotlinで書き直すのは無理だと思う

実行するためにはJavaScriptエンジンが必要です。
AndroidならWebViewがありますが、テストコードはAndroid非依存なので動きません。