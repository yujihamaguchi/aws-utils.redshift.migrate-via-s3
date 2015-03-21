(ns aws-utils.redshift.migrate-via-s3-test
  (:require [clojure.test :refer :all]
            [amazonica.aws.s3 :as s3]
            [aws-utils.redshift.migrate-via-s3 :refer :all]
            [aws-utils.redshift.properties :refer :all]))

(def credentials
  (str "aws_access_key_id=" ((get-aws-default-credentials) :access-key) ";aws_secret_access_key=" ((get-aws-default-credentials) :secret-key)))

(def s3-prefix "ut-unload-redshift-data")

(deftest ut-unload-redshift-data
  ; 初期化
  (clear-s3-path s3-bucket-name s3-prefix)
  ; 存在しないスキーマ名を指定した場合、例外が発生し、指定したパスにS3オブジェクトが存在しないこと
  (is
    (= (try
         (unload-redshift-data "hoge.fuga" (str "s3://" s3-bucket-name "/" s3-prefix) credentials)
         (catch Exception e
           (.getMessage e)))
       "ERROR: schema \"hoge\" does not exist"
    )
  )
  (is
    (= 0 (count (:object-summaries (s3/list-objects :bucket-name s3-bucket-name :prefix s3-prefix))))
  )
  ; 存在しないテーブル名を指定した場合、例外が発生し、指定したパスにS3オブジェクトが存在しないこと
  (is
    (= (try
         (unload-redshift-data "public.hoge" (str "s3://" s3-bucket-name "/" s3-prefix) credentials)
         (catch Exception e
           (.getMessage e)))
       "ERROR: relation \"public.hoge\" does not exist"
    )
  )
  (is
    (= 0 (count (:object-summaries (s3/list-objects :bucket-name s3-bucket-name :prefix s3-prefix))))
  )
  ; 不正な文法のクエリを指定した場合、例外が発生し、指定したパスにS3オブジェクトが存在しないこと
  (is
    (= (try
         (unload-redshift-data "SELECT * FROM public.test WHERE" (str "s3://" s3-bucket-name "/" s3-prefix) credentials)
         (catch Exception e
           (apply str (take 19 (.getMessage e)))))
       "ERROR: syntax error"
    )
  )
  (is
    (= 0 (count (:object-summaries (s3/list-objects :bucket-name s3-bucket-name :prefix s3-prefix))))
  )
  ; 参照権限が無いスキーマのテーブル指定した場合、例外が発生し、指定したパスにS3オブジェクトが存在しないこと
  (is
    (= (try
         (unload-redshift-data "schema2.table1" (str "s3://" s3-bucket-name "/" s3-prefix) credentials)
         (catch Exception e
           (.getMessage e)))
       "ERROR: permission denied for schema schema2"
    )
  )
  (is
    (= 0 (count (:object-summaries (s3/list-objects :bucket-name s3-bucket-name :prefix s3-prefix))))
  )
  ; 対象テーブルが0件の場合
  (let [table-name "schema1.zero"
          s3-prefix (str s3-prefix "/" (get-local-date) "/" table-name)]
    (is
      (= [0] (unload-redshift-data table-name (str "s3://" s3-bucket-name "/" s3-prefix) credentials))
    )
    (is
      (< 0 (count (:object-summaries (s3/list-objects :bucket-name s3-bucket-name :prefix s3-prefix))))
    )
  )
  ; 対象テーブルにデータがある場合
  (let [table-name "schema1.table1"
          s3-prefix (str s3-prefix "/" (get-local-date) "/" table-name)]
    (is
      (= [0] (unload-redshift-data table-name (str "s3://" s3-bucket-name "/" s3-prefix) credentials))
    )
    (is
      (< 0 (count (:object-summaries (s3/list-objects :bucket-name s3-bucket-name :prefix s3-prefix))))
    )
  )
)

(deftest ut-clear-s3-path
  ; 存在しないバケットを指定した場合、例外を出力すること
  (is (= "The specified bucket does not exist"
         (try
           (clear-s3-path "not-exist-backet" "hoge")
           (catch Exception e
             (apply str (take 35 (.getMessage e)))))))
  ; 存在しないパス（バケットは存在）を指定した場合、なにも処理を行わないこと
  (is (= nil
         (try
           (clear-s3-path s3-bucket-name "not-exist-path")
           (catch Exception e
             (.getMessage e)))))
  ; 指定したパスにオブジェクトが存在する場合、オブジェクトが削除されること
  (is
    (< 0 (count (:deleted-objects (clear-s3-path s3-bucket-name s3-prefix))))
  )
  (is
    (= 0 (count (:object-summaries (s3/list-objects :bucket-name s3-bucket-name :prefix s3-prefix))))
  )
)
