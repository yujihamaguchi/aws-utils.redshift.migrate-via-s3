# migrate-via-s3

Redshift上のテーブル間のデータ移行を簡便化するコマンドラインツールの提供を目的としたモジュール

接続先クラスタ、データベースやS3バケットは使用者から指定できない

## Building

```
$ lein deps
$ lein uberjar
```

## Usage

・同期(デフォルト)
```
$ java -jar ./target/aws-utils.redshift.migrate-via-s3-0.1.0-standalone.jar [移行元スキーマ名].[移行元テーブル名] [移行先スキーマ名].[移行先テーブル名]
```
・追加
```
$ java -jar ./target/aws-utils.redshift.migrate-via-s3-0.1.0-standalone.jar [移行元スキーマ名].[移行元テーブル名] [移行先スキーマ名].[移行先テーブル名] add yes
```

## Examples

[コマンドラインテストのサンプル](https://github.com/yujihamaguchi/aws-utils.redshift.migrate-via-s3/blob/master/doc/commandline_test_sample.md)を参照

### License

Copyright © 2015 Yuji Hamaguchi

Distributed under the Eclipse Public License, the same as Clojure.