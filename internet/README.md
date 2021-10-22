# internet モジュール
インターネット関係を担当する。

今回はappモジュールがクソデカにならないように、**通信部分だけ**取り出しました。Android非依存なので他環境に持っていけるかもしれない。

## 視聴ページ取得関数

`WatchPageHTML#getWatchPage()`

引数はKDoc見て。動画URLや動画情報はここから手に入れる
第２引数以降は初回時nullでもいいですが、返り値の

- base.jsのパス
- 復号操作に使う関数名を入れたデータクラス
- 復号操作の順番を入れた配列

をローカルにJSON形式とかで保存しておいて、二回目以降叩く際に使うとbase.jsを取得してアルゴリズムの解析する作業をスキップできるのでおすすめです。
もちろんbase.jsのパスが変わっている場合はアルゴリズムを再度解析します。

## 検索用関数
こちらはクラスになっていて、検索する前に`init`関数を呼ぶ必要があります

```
val searchAPI = SearchAPI()
searchAPI.init()
val searchResponseData = searchAPI.search("バックレカラオケ", SearchAPI.PARAMS_SORT_UPLOAD_DATE)
```

`searchResponseData`には、APIキーが含まれていて、二回目以降検索をする際はinit関数の引数に入れることでAPIキー取得作業をスキップできます。

## テストコード
testsパッケージ内の@Testアノテーションを付けた関数はAndroid Studioを利用して開発環境のJava VMで実行できます。
`SearchAPITest`、`WatchPageHTMLTest`参照。

## YTAPICall
APIキーが変化しているときに備えて、APIキーが変わっていた際は取り直すようにPOSTリクエストをする関数がある。
検索API、チャンネル投稿動画APIがこれを経由して呼んでいる。