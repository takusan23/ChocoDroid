# ui パッケージ
Composeのコンポーネントとか画面とか。

ViewModelとのやり取りにはLiveDataではなくMutableStateFlowを使ってます。

# M3

Material 3の略。
`tonalElevation`は色の明るさ、暗さを設定するパラメーターらしい。

# screen.bottomsheet

ボトムシートの画面遷移にはKotlin Flowで制御してます。
Compose版Navigationだとバックスタック機能とか、Fragmentのargumentみたいにパラメータつけるのができないので、
データクラスをFlowに突っ込んで画面遷移するようにする。