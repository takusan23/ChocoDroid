# database パッケージ
AndroidのRoomのコンポーネントがあります。

Kotlin CoroutinesのFlowのおかげでデータベースに変更があったらFlowに流してくれます。リアルタイムGJ

これもモジュール分割したかったんだけどなんかRoom関係は出来ないっぽい？

## データベース詳細

- DownloadContentDB
    - ダウンロードした動画をまとめておくデータベース
    - 動画情報、動画ファイルのパスとかを保存しておく
    - 動画ファイル自体はアプリ固有フォルダに入ってます
- FavoriteDB
    - お気に入り動画データベース
    - テーブルは2つある
        - FavoriteFolderDBEntity
            - お気に入りフォルダを格納するデータベース
        - FavoriteVideoDBEntity
            - お気に入り動画を格納するデータベース
        - folderId が共通しています
- FavoriteChDB
    - お気に入りチャンネルデータベース
- HistoryDB
    - 視聴履歴データベース