## Powershell
# nominal scinario
```
$ java -jar ./target/aws-utils.redshift.migrate-via-s3-0.1.0-standalone.jar schema1.table1 schema1.table1
2015/03/22 08:17:03 [INFO] START: UNLOAD FROM 'schema1.table1' (cluster: test-cluster, database: src)
2015/03/22 08:17:04 [INFO] END  : UNLOAD FROM 'schema1.table1' (cluster: test-cluster, database: src)
2015/03/22 08:17:05 [INFO] START: TRUNCATE 'schema1.table1' (cluster: test-cluster, database: trgt)
2015/03/22 08:17:06 [INFO] END  : TRUNCATE 'schema1.table1' (cluster: test-cluster, database: trgt)
2015/03/22 08:17:06 [INFO] START: COPY TO 'schema1.table1' (cluster: test-cluster, database: trgt)
2015/03/22 08:17:19 [INFO] END  : COPY TO 'schema1.table1' (cluster: test-cluster, database: trgt)
2015/03/22 08:17:19 [INFO] START: CLEAN S3 OBJECTS 's3://yujihamaguchi/2015/03/22 08:17:03/schema1.table1/'
2015/03/22 08:17:22 [INFO] END  : CLEAN S3 OBJECTS 's3://yujihamaguchi/2015/03/22 08:17:03/schema1.table1/'
$ echo $LASTEXITCODE
0
```

```
\c trgt administrator
trgt=# SELECT * FROM schema1.table1 ORDER BY 1;
 a
---
 x
 y
 z
(3 行)
```

```
$ java -jar ./target/aws-utils.redshift.migrate-via-s3-0.1.0-standalone.jar schema1.table1 schema1.table1 add yes
2015/03/22 08:19:11 [INFO] START: UNLOAD FROM 'schema1.table1' (cluster: test-cluster, database: src)
2015/03/22 08:19:11 [INFO] END  : UNLOAD FROM 'schema1.table1' (cluster: test-cluster, database: src)
2015/03/22 08:19:12 [INFO] START: COPY TO 'schema1.table1' (cluster: test-cluster, database: trgt)
2015/03/22 08:19:13 [INFO] END  : COPY TO 'schema1.table1' (cluster: test-cluster, database: trgt)
2015/03/22 08:19:13 [INFO] START: CLEAN S3 OBJECTS 's3://yujihamaguchi/2015/03/22 08:19:11/schema1.table1/'
2015/03/22 08:19:16 [INFO] END  : CLEAN S3 OBJECTS 's3://yujihamaguchi/2015/03/22 08:19:11/schema1.table1/'
$ echo $LASTEXITCODE
0
```

```
trgt=# SELECT * FROM schema1.table1 ORDER BY 1;
 a
---
 x
 x
 y
 y
 z
 z
(6 行)
```

```
$ java -jar ./target/aws-utils.redshift.migrate-via-s3-0.1.0-standalone.jar "SELECT * FROM schema1.table1 WHERE a <> 'x'" schema1.table1
2015/03/22 08:20:20 [INFO] START: UNLOAD FROM 'SELECT * FROM schema1.table1 WHERE a <> 'x'' (cluster: test-cluster, database: src)
2015/03/22 08:20:23 [INFO] END  : UNLOAD FROM 'SELECT * FROM schema1.table1 WHERE a <> 'x'' (cluster: test-cluster, database: src)
2015/03/22 08:20:23 [INFO] START: TRUNCATE 'schema1.table1' (cluster: test-cluster, database: trgt)
2015/03/22 08:20:23 [INFO] END  : TRUNCATE 'schema1.table1' (cluster: test-cluster, database: trgt)
2015/03/22 08:20:23 [INFO] START: COPY TO 'schema1.table1' (cluster: test-cluster, database: trgt)
2015/03/22 08:20:24 [INFO] END  : COPY TO 'schema1.table1' (cluster: test-cluster, database: trgt)
2015/03/22 08:20:24 [INFO] START: CLEAN S3 OBJECTS 's3://yujihamaguchi/2015/03/22 08:20:20/schema1.table1/'
2015/03/22 08:20:27 [INFO] END  : CLEAN S3 OBJECTS 's3://yujihamaguchi/2015/03/22 08:20:20/schema1.table1/'
$ echo $LASTEXITCODE
0
```

```
trgt=# SELECT * FROM schema1.table1 ORDER BY 1;
 a
---
 y
 z
(2 行)
```

```
$ java -jar ./target/aws-utils.redshift.migrate-via-s3-0.1.0-standalone.jar schema1.zero schema1.table1
2015/03/22 08:22:06 [INFO] START: UNLOAD FROM 'schema1.zero' (cluster: test-cluster, database: src)
2015/03/22 08:22:06 [INFO] END  : UNLOAD FROM 'schema1.zero' (cluster: test-cluster, database: src)
2015/03/22 08:22:07 [INFO] START: TRUNCATE 'schema1.table1' (cluster: test-cluster, database: trgt)
2015/03/22 08:22:07 [INFO] END  : TRUNCATE 'schema1.table1' (cluster: test-cluster, database: trgt)
2015/03/22 08:22:07 [INFO] START: COPY TO 'schema1.table1' (cluster: test-cluster, database: trgt)
2015/03/22 08:22:08 [INFO] END  : COPY TO 'schema1.table1' (cluster: test-cluster, database: trgt)
2015/03/22 08:22:08 [INFO] START: CLEAN S3 OBJECTS 's3://yujihamaguchi/2015/03/22 08:22:06/schema1.table1/'
2015/03/22 08:22:11 [INFO] END  : CLEAN S3 OBJECTS 's3://yujihamaguchi/2015/03/22 08:22:06/schema1.table1/'
$ echo $LASTEXITCODE
0
```

```
trgt=# SELECT * FROM schema1.table1 ORDER BY 1;
 a
---
(0 行)
```

# non-nominal scinario

```
$ java -jar ./target/aws-utils.redshift.migrate-via-s3-0.1.0-standalone.jar
Wrong number of args ( 0 )
$ echo $LASTEXITCODE
1
```

```
$ java -jar ./target/aws-utils.redshift.migrate-via-s3-0.1.0-standalone.jar schema1.table1
Wrong number of args ( 1 )
$ echo $LASTEXITCODE
1
```

```
$ java -jar ./target/aws-utils.redshift.migrate-via-s3-0.1.0-standalone.jar hoge.table1 schema1.table1
2015/03/22 08:24:33 [INFO] START: UNLOAD FROM 'hoge.table1' (cluster: test-cluster, database: src)
2015/03/22 08:24:34 [ERROR] ERROR: schema "hoge" does not exist
2015/03/22 08:24:34 [INFO] START: CLEAN S3 OBJECTS 's3://yujihamaguchi/2015/03/22 08:24:33/schema1.table1/'
2015/03/22 08:24:35 [INFO] END  : CLEAN S3 OBJECTS 's3://yujihamaguchi/2015/03/22 08:24:33/schema1.table1/'
$ echo $LASTEXITCODE
1
```

```
$ java -jar ./target/aws-utils.redshift.migrate-via-s3-0.1.0-standalone.jar schema1.hoge schema1.table1
2015/03/22 08:26:17 [INFO] START: UNLOAD FROM 'schema1.hoge' (cluster: test-cluster, database: src)
2015/03/22 08:26:17 [ERROR] ERROR: relation "schema1.hoge" does not exist
2015/03/22 08:26:17 [INFO] START: CLEAN S3 OBJECTS 's3://yujihamaguchi/2015/03/22 08:26:17/schema1.table1/'
2015/03/22 08:26:19 [INFO] END  : CLEAN S3 OBJECTS 's3://yujihamaguchi/2015/03/22 08:26:17/schema1.table1/'
$ echo $LASTEXITCODE
1
```

```
$ java -jar ./target/aws-utils.redshift.migrate-via-s3-0.1.0-standalone.jar schema1.table1 hoge.table1
2015/03/22 08:27:12 [INFO] START: UNLOAD FROM 'schema1.table1' (cluster: test-cluster, database: src)
2015/03/22 08:27:12 [INFO] END  : UNLOAD FROM 'schema1.table1' (cluster: test-cluster, database: src)
2015/03/22 08:27:12 [ERROR] ERROR: schema "hoge" does not exist
2015/03/22 08:27:12 [INFO] START: CLEAN S3 OBJECTS 's3://yujihamaguchi/2015/03/22 08:27:12/hoge.table1/'
2015/03/22 08:27:16 [INFO] END  : CLEAN S3 OBJECTS 's3://yujihamaguchi/2015/03/22 08:27:12/hoge.table1/'
$ echo $LASTEXITCODE
1
```

```
$ java -jar ./target/aws-utils.redshift.migrate-via-s3-0.1.0-standalone.jar schema1.table1 schema1.hoge
2015/03/22 08:27:47 [INFO] START: UNLOAD FROM 'schema1.table1' (cluster: test-cluster, database: src)
2015/03/22 08:27:48 [INFO] END  : UNLOAD FROM 'schema1.table1' (cluster: test-cluster, database: src)
2015/03/22 08:27:48 [ERROR] ERROR: relation "schema1.hoge" does not exist
2015/03/22 08:27:48 [INFO] START: CLEAN S3 OBJECTS 's3://yujihamaguchi/2015/03/22 08:27:47/schema1.hoge/'
2015/03/22 08:27:51 [INFO] END  : CLEAN S3 OBJECTS 's3://yujihamaguchi/2015/03/22 08:27:47/schema1.hoge/'
$ echo $LASTEXITCODE
1
```
