# downloadpocket モジュール

テストしやすいかなという理由でモジュールに切り出しました。これもAndroid非依存。
OkHttpとCoroutineで書かれている分割ダウンローダーです。たちみどろいどでも使ってたやつ。

参考：https://takusan.negitoro.dev/posts/okhttp_split_download/

## 既知の不具合
- 分割数を10以上？にすると結合後再生できない
    - HTTP Range 有識者おらん？