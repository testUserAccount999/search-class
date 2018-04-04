# search-class
Javaのクラスファイルをテキスト変換して指定のキーワードで検索します。<br />
[Commons BCEL](https://commons.apache.org/proper/commons-bcel/)を使用してクラスファイルをテキスト変換し、キーワードで検索を行います。

# ビルド方法
mvn package

# 実行方法
`java -jar search-keyword-jar-with-dependencies.jar "targetDirectry" "keyword.txt" "searchResult.txt"`

targetDirectry：検索対象のクラスファイル、jarファイルが格納されたディレクトリパス<br />
keyword.txt：検索するキーワードを記載したテキストファイルのパス. 1キーワードにつき1行で記載する。<br />
searchResult.txt：検索結果を出力するテキストファイルのパス<br />
