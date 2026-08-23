# Entity ジェネレーター

`blog-infra` は MyBatis-Plus の Entity クラスのみを生成します。
Mapper/XML、Repository、Service、Controller は生成しません。

IDE から `EntityCodeGenerator` を実行し、以下のプログラム引数を指定します。

```text
<module-path> <table-name> <entity-name>
```

作業ディレクトリが `blog-infra` の場合は、次のように指定します。

```text
../blog-module-member t_member Member
```

第1引数に出力先モジュール、第2引数にデータベースのテーブル名、
第3引数に生成する Entity のクラス名を指定します。
上記の例では、`t_member` テーブルから `Member.java` を生成し、
`com.manpowergroup.blog.module.member.domain.model` に出力します。

データベース接続設定は、環境変数 `DB_URL`、`DB_USER`、`DB_PWD` で上書きできます。
