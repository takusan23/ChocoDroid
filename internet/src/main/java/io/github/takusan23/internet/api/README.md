# api パッケージ
動画検索、チャンネル情報はHTMLの中のJSON解析ではなく、APIを叩いてJSONをもらいます。

## YTAPICall
POSTリクエストを飛ばす関数がある。
APIキーが変更になったときにAPIキーを取り直すようになっています。(そもそもAPIキー変わるの？)