- AWS CLIを用いて、Redshiftクラスタを最小構成でローンチする
```
aws redshift create-cluster --db-name test --cluster-identifier test-cluster --node-type dw2.large --cluster-type single-node --master-username administrator --master-user-password Passw0rd
```
- psqlでクラスタに接続し、DDLを実行
```
psql -h {Endpoint} -U administrator -d test -p 5439
```
```
CREATE DATABASE src;
CREATE DATABASE trgt;

\c src
CREATE USER user1 WITH PASSWORD 'User1User1';
CREATE USER user2 WITH PASSWORD 'User2User2';
CREATE SCHEMA schema1;
ALTER SCHEMA schema1 OWNER TO user1;
CREATE SCHEMA schema2;
ALTER SCHEMA schema2 OWNER TO user2;

\c src user1
CREATE TABLE schema1.table1 (a CHAR);
INSERT INTO schema1.table1 (a) VALUES ('x');
INSERT INTO schema1.table1 (a) VALUES ('y');
INSERT INTO schema1.table1 (a) VALUES ('z');
CREATE TABLE schema1.zero (a CHAR);
ALTER TABLE schema1.table1 OWNER TO user1;
ALTER TABLE schema1.zero OWNER TO user1;

\c src user2
CREATE TABLE schema2.table1 (a CHAR);
INSERT INTO schema2.table1 (a) VALUES ('x');
INSERT INTO schema2.table1 (a) VALUES ('y');
INSERT INTO schema2.table1 (a) VALUES ('z');
ALTER TABLE schema2.table1 OWNER TO user2;

\c trgt administrator
CREATE SCHEMA schema1;
ALTER SCHEMA schema1 OWNER TO user1;

\c trgt user1
CREATE TABLE schema1.table1 (a CHAR);
ALTER TABLE schema1.table1 OWNER TO user1;
```