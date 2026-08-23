# Entity ジェネレーター

`blog-infra` は MyBatis-Plus の Entity クラスのみを生成します。
Mapper/XML、Repository、Service、Controller は生成しません。

IDE から `EntityCodeGenerator` を実行し、以下のプログラム引数を指定します。

```text
<module-path> <table-name> [table-name...]
```

作業ディレクトリが `blog-infra` の場合は、次のように指定します。

```text
../blog-module-system t_sys_role_menu
```

モジュールのディレクトリ名から、出力先パッケージとテーブルプレフィックスを決定します。
上記の例では、Entity を `com.manpowergroup.blog.module.system.entity` に出力し、
生成するクラス名から `t_sys_` を除外します。

データベース接続設定は、環境変数 `DB_URL`、`DB_USER`、`DB_PWD` で上書きできます。
