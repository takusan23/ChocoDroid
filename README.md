# ChocoDroid

## ダウンロード

`GitHub Actions`からダウンロード出来ます。
多分一番上を選んで`app-release.apk`をダウンロードすると、zipファイルが手に入るので展開してAPKをインストールしてください。

https://github.com/takusan23/ChocoDroid/actions/workflows/generate_release_apk.yml

## 開発者向け情報

### APKリリース

`GitHub Actions`にやらせてます。
releaseブランチへpushすると起動します。

### ブランチ
- master
    - 開発中のコード
- release
    - 一区切りついたらリリースするのでその時点のコード
    - このブランチにpushすると`GitHub Actions`がAPK作成タスクを初めてくれる