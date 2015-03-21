(ns aws-utils.redshift.properties)

(def s3-bucket-name "yujihamaguchi")

(def src-db {:subprotocol "postgresql"
             :subname "//test-cluster.cjcazlpdxwsj.ap-northeast-1.redshift.amazonaws.com:5439/src"
             :user "user1"
             :password "User1User1"})

(def trgt-db {:subprotocol "postgresql"
             :subname "//test-cluster.cjcazlpdxwsj.ap-northeast-1.redshift.amazonaws.com:5439/trgt"
             :user "user1"
             :password "User1User1"})
